# AppModel4Flow Library

This Maven project contains the implementation of AppModel4Flow that is considered production ready. It is my intention
to keep the API backwards compatible as much as possible while adding new features. However, this is still an
open source project mainly created for my own needs so I can't promise anything.

This README and the JavaDocs serve as the official documentation. If you find any conflicts between the code and this
README, the code wins. :-)

## Observable Values and Properties

An observable value is, like the name suggests, a value that can be observed. Listeners can be registered using either 
strong or weak references and they are notified whenever the value is changed. 

Observable values should mainly be 
accessed by the outside world through interfaces that expose different levels of functionality depending on the use
case. There are three interfaces in the interface hierarchy:

* [ObservableValue](src/main/java/net/pkhapps/appmodel4flow/property/ObservableValue.java): This is the base interface
that contains methods for retreiving the value and registering listeners. It is not possible to change the value.
* [WritableObservableValue](src/main/java/net/pkhapps/appmodel4flow/property/WritableObservableValue.java): This 
interface adds support for changing the value through the interface.
* [Property](src/main/java/net/pkhapps/appmodel4flow/property/Property.java): This interface adds additional flags, such
as *dirty* and *read-only*. We'll get back to these later.

There are two implementation classes ready for use:

* [DefaultObservableValue](src/main/java/net/pkhapps/appmodel4flow/property/DefaultObservableValue.java) implements
the `ObservableValue` interface.
* [DefaultProperty](src/main/java/net/pkhapps/appmodel4flow/property/DefaultProperty.java) implements
the `Property` interface.
* There is no default implementation of `WritableObservableValue` since you can use a `DefaultProperty` instead and then
choose whether to expose the instance through either the `Property` interface or the `WritableObservableValue` 
interface.

Let's look at an example:

```java
class MyModel {
    private final DefaultProperty<String> myString = new DefaultProperty<>();
    private final DefaultObservableValue<Integer> myReadOnlyInteger = new DefaultObservableValue<>();
    
    public Property<String> myString() {
        return myString;
    }
    
    public ObservableValue<Integer> myReadOnlyInteger() {
        return myReadOnlyInteger;
    }
}
```

In this case, the class has access to `myReadOnlyInteger` through the class instance, meaning it can actually change
the value. However, the outside world has only access through the `ObservableValue` interface and so can only observe
the value but not change it.

Also note that I am intentionally not prefixing the method names with `get`. This is because I don't consider these
to be JavaBean getter methods since you still need to invoke the `getValue` method to actually retrieve the value. The
code becomes more fluent if you get rid of the extra `get`:s.

### Listeners

Like I mentioned before, listeners can be registered using either strong references. When you register a strong 
listener, you get back a `Registration` handle that you can use to unregister the listener when no longer needed:

```java
class MyClient {  
    
    private Registration modelRegistration;
        
    public void init() {
        modelRegistration = myModel.myString().addValueChangeListener(event -> {
           // Do something with the event
        });
    }
    
    public void destroy() {
        modelRegistration.remove();
    }
}
```

If you register a weak listener, the listener will be automatically unregistered as soon as it becomes garbage 
collected. However, this means that you can't use lambdas or method pointers as listeners since they are essentially
instances of an anonymous class and are not referenced anywhere else. This code may work for a while, but eventually
the lambda is going to be garbage collected and then you won't receive any events anymore:

```java
class DontDoLikeThis {
    public void init() {
        myModel.myString().addWeakValueChangeListener(event -> {
           // Do something with the event. 
        });
    }
}
```

However, if you store a reference to your lambda, it will work as you expect:

```java
class DoThisInstead {
    
    private final SerializableConsumer<Property.ValueChangeEvent<String>> myListener = (event) -> {
        // Do something with the event.         
    };
    
    public void init() {
        myModel.myString().addWeakValueChangeListener(myListener);
    }
}
```

If the listener and your `ObservableValue` have the same scope you don't have to worry about unregistering as both 
objects will become garbage collected at the same time anyway.

### Computed and Combined Values

There are two special implementations of `ObservableValue`. Both of these values observe other `ObservableValue`s (let's
call these "dependencies" from now on) and recompute their values whenever any of the dependencies change. However, they
compute their values in different ways:

* [ComputedValue](src/main/java/net/pkhapps/appmodel4flow/property/ComputedValue.java) computes its value by invoking
a supplier function that returns the value. This function can do whatever it wants - it is not required to actually use
any values from the dependencies. This is useful when you want to combine values of different types into one computed
value.
* [CombinedValue](src/main/java/net/pkhapps/appmodel4flow/property/CombinedValue.java) computes its value by invoking
a combiner function that accepts a stream of values from the dependencies and produces the computed value. This is 
useful when you want to combine values of the same type into one computed value. The 
[Combiners](src/main/java/net/pkhapps/appmodel4flow/property/support/Combiners.java) class contains some combiner
functions for use with boolean and string dependencies. Check it out if you want to know more. Also note that only
non-empty values will be passed to the combiner function. If all dependencies are empty, the combiner function will
receive an empty stream.

Computed and combined values are always read only since it would not make any sense to explicitly set their values.

### Properties

Like I mentioned earlier, properties introduce two special flags: *dirty* and *read-only*. Both of these flags are 
`ObservableValue`s themselves, which means you can track them and perform operations when they change. Let's have a 
closer look at them.

The *dirty* flag tells whether the property value has been modified or not after it was initialized. This is especially
useful in forms when you need to know whether the user has made any changes or not. The *dirty* flag is always reset
when the property is initialized but it is also possible to explicitly reset it using either the `resetDirtyFlag()`
method or the `setCleanValue(..)` method. Properties also keep track of the "clean value", i.e. the value the property 
had before it became dirty. The `discard()` method reverts the property to its clean value (and informs all listeners, 
of course).

The *read-only* flag controls whether the property can be written to or not. Whenever a property is in read-only mode,
its value cannot be changed. This is intended for situations where a property is sometimes writable and sometimes 
read-only. For cases where a property is always read-only, consider using an `ObservableValue` instead.

### Value Mapping

Just like e.g. `Optional` and `Stream`, `ObservableValue` also has a `map` method. This makes it possible to map an
`ObservableValue` of one type into an `ObservableValue` of another type. This allows for some neat code, but also opens
up a risk of bugs as I'm sure there are a lot of edge cases that I have not thought about when I implemented it.
If you find any bugs, please let me know!

The way mapping works and why it can be useful is best explained with an example. Let's say you have an `ObservableValue`
that contains an unmodifiable `Collection`. You want to enable a certain UI element when this collection is non-empty
and disable it when it is empty. You could register a listener and query the collection directly, but you could also
create a mapped value like this:

```java
class ExampleOfMappedValue {
    
    private DefaultObservableValue<Collection<String>> myCollection;
    private ObservableValue<Boolean> isMyCollectionEmpty;
    
    public void init() {
        myCollection = new DefaultObservableValue<>();
        isMyCollectionEmpty = myCollection.map(Collection::isEmpty);
    }
}
```

Now, when ever `myCollection` is changed, `isMyCollectionEmpty` will also change and its value will be whether the 
collection is empty or not.

### Empty Values

Especially when binding `ObservableValue`s to UI elements (more about this later), you need to know when a value
is considered empty (to be able to detect if a required field is missing, for example). By default, a value is 
considered empty if it is `null`. However, this is not always true. An empty string may very well also be considered
empty even though it is not `null`.

To be able to support other empty values than `null`, you can either override the `ObservableValue.isEmpty(..)` method
or use the `withEmptyCheck(..)` method that is provided by the default implementation. This allows you to pass in 
a `Predicate` that will be used to test whether a *non-`null`* value is empty or not.

For example:
```java
class ExampleOfEmptyStringValue {
    private DefaultObservableValue<String> myString = new DefaultObservableValue<>().withEmptyCheck(String::isEmpty);
}
``` 

In this case, both `null`s and empty strings will be considered empty.

## Actions

An action is essentially an object-representation of a stateful function that can - but is not required to - return
a result. Since the function is stateful, it knows whether it can be performed or not. This is very useful for e.g. 
enabling or disabling UI elements such as buttons. It is also possible to register listeners that get notified whenever
an action is performed.

The action is defined in the [Action](src/main/java/net/pkhapps/appmodel4flow/action/Action.java) interface:
- The type of the action output is defined as a generic parameter. For actions that do not return any result, use `Void`.
- The *performable* flag is exposed as an `ObservableValue`.
- Perform listeners can be registered using either strong or weak listeners.

### Implementing Actions

In most cases you don't want to implement the `Action` interface directly. Instead, you want to extend the 
[AbstractAction](src/main/java/net/pkhapps/appmodel4flow/action/AbstractAction.java) class and implement the 
`doPerform()` method. When it comes to controlling the *performable* flag, you have a few options. You can either 
control the flag internally from inside the action or you can pass in an external `ObservableValue` and let the action
delegate to that. Both approaches have their pros and cons:
- Controlling the flag internally makes the action more self-contained and independent, but requires more coding.
- Controlling the flag externally is especially useful if the action depends on the existence of e.g. a selection in the
user interface, but can also make the action more fragile.

For actions that don't return any results, you can use 
[ActionWithoutResult](src/main/java/net/pkhapps/appmodel4flow/action/ActionWithoutResult.java). This is a concrete class
that can be instantiated using a lambda or method pointer that will be executed when the action is performed. However,
you can also extend and implement `doPerform()` if you want to.

Finally, the [AppModel](src/main/java/net/pkhapps/appmodel4flow/AppModel.java) class contains helper methods for easily 
creating actions using lambdas and/or method pointers. For example:
```java
class AppModelActionExample {
    final SelectionModel<Contact> contactSelectionModel = AppModel.newSelectionModel();
    final Action<Void> editSelectedContactAction = AppModel.asAction(
                contactSelectionModel.map(Selection::hasValue), 
                this::editSelectedContact);
    
    private void editSelectedContact(Contact contact) {
        // Do something.
    }
}
```
Here, the `editSelectedContactAction` will be performable whenever the `contactSelectionModel` is non-empty and it will
invoke the `editSelectedContact(..)` method when performed.

The intention behind the `AppModel` class is to make the code more fluent but whether that's actually the case remains 
to be seen. If you try it out, please let me know what you think!

## Selections

A selection represents a set of items that the user has selected.
* A selection can be empty, contain one item or contain multiple items.
* The [Selection](src/main/java/net/pkhapps/appmodel4flow/selection/Selection.java) interface defines the selection and
[DefaultSelection](src/main/java/net/pkhapps/appmodel4flow/selection/DefaultSelection.java) implements it.
* A selection is `Iterable` which means you can use it in `for` loops. 
* Selections are *immutable* and we will shortly return to why this is important.

Selections are contained and managed by a 
[SelectionModel](src/main/java/net/pkhapps/appmodel4flow/selection/SelectionModel.java). This model contains methods
for retrieving and clearing the selection and for selecting one or multiple items. However, the selection model is also
a `WritableObservableValue`. This means that all the features of observable values are automatically available in the
selection model as well, including value mapping. This is demonstrated in the code example in the previous section
(about actions and `AppModel`).
 
This is the reason why `Selection`s must be immutable. You have to replace an immutable selection if you want to change
it, which in turn will notify any listeners of the `SelectionModel`. If the selection was mutable, the model would not
know about any changes made to it and would thus not be able to inform any listeners.

The `SelectionModel` interface is implemented by 
[DefaultSelectionModel](src/main/java/net/pkhapps/appmodel4flow/selection/DefaultSelectionModel.java).
 
## Bindings

Observable values, properties, actions and selections are not really useful by themselves. They only become useful when
they are bound to UI elements, allowing the user to interact with them. This is where bindings come in. 

Before we move on to the details it is important to note that all bindings implement the Vaadin `Registration` 
interface which defines a single `remove()` method. This method is used to break the binding and free any resources when
the binding is no longer needed. Remember to call this method to avoid memory leaks, or make sure that the bindings and 
the participating objects are in the same scope.

### Action Bindings

Action bindings are the simplest ones. An action binding does the following:
- Whenever the action is performable, the bound UI element is enabled.
- Whenever the action is not performable, the bound UI element is disabled.
- Whenever the user invokes the UI element, the action is performed.

I consider action bindings to be *one-way* bindings since the action changes the state of the UI element, but the UI
element does not change the state of the action.

Currently there is only an action binding for 
[Button](src/main/java/net/pkhapps/appmodel4flow/binding/ActionButtonBinding.java). I plan to make more bindings as
more suitable UI elements are added to the Vaadin platform.

The [AppModel](src/main/java/net/pkhapps/appmodel4flow/AppModel.java) class contains helper methods for easily creating
action bindings. For example:
```java
class AppModelActionBindingExample {
    
    private Button saveButton;
    private Button cancelButton;

    private Action<Void> saveAction;
    private Action<Void> closeAction;
    
    public void init() {
        // Create the actions and buttons
        // ...
        AppModel.bind(saveAction, saveButton);
        AppModel.bind(closeAction, cancelButton);
    }    
}
```

### Selection Bindings

Selection bindings are also quite simple. A selection binding does the following:
- Whenever the selection is changed, the bound UI element is updated accordingly.
- Whenever the user changes the selection in the bound UI element, the selection model is updated.

As you can see, a selection binding is a *two-way* binding where the UI element can change the state of the selection
model and vice versa.

Currently there are selection bindings for
[Grid](src/main/java/net/pkhapps/appmodel4flow/binding/SelectionModelGridBinding.java) and
[ComboBox](src/main/java/net/pkhapps/appmodel4flow/binding/SelectionModelComboBoxBinding.java). I plan to make more 
bindings as more suitable UI elements are added to the Vaadin platform.

The [AppModel](src/main/java/net/pkhapps/appmodel4flow/AppModel.java) class contains helper methods for easily creating
selection bindings. For example:
```java
class AppModelSelectionBindingExample {
    
    private Grid<Contact> contactGrid;
    private ComboBox<Contact> contactComboBox;
    private SelectionModel<Contact> contactSelectionModel;
    
    public void init() {
        // Create the grid, combo box and selection model
        // ...
        AppModel.bind(contactSelectionModel, contactGrid);
        AppModel.bind(contactSelectionModel, contactComboBox);
    }    
}
```

Please note that in this example, both the combo box and the grid are bound to the same model. This means that if you
select an item in the combo box, the grid will update its selection and vice versa.

### Field Bindings

Field bindings are used to bind observable values and properties to UI fields. They are the most complex bindings since 
they can be either one-way or two-way. Two-way bindings also include include conversion and validation. 
To explain what this is, we need to introduce some new terminology:

* A field binding's *model* is the `Property` or `ObservableValue`.
* A field binding's *presentation* is the UI element - any component that implements the Vaadin `HasValue` interface.

One-way bindings implement the [FieldBinding](src/main/java/net/pkhapps/appmodel4flow/binding/FieldBinding.java)
interface and two-way bindings implement the 
[TwoWayFieldBinding](src/main/java/net/pkhapps/appmodel4flow/binding/TwoWayFieldBinding.java) interface. The default
implementations are 
[ObservableValueFieldBinding](src/main/java/net/pkhapps/appmodel4flow/binding/ObservableValueFieldBinding.java) and
[PropertyFieldBinding](src/main/java/net/pkhapps/appmodel4flow/binding/PropertyFieldBinding.java)

A one-way binding will update the presentation whenever the model is changed but not the other way around. A
two-way binding will also update the model whenever the presentation is changed. This makes things interesting.

#### Conversion

The first thing we need to notice is that the model and the presentation may have different types. Thus, a Vaadin 
`Converter` is needed to convert between these two (when the types are the same, we use an *identity* converter).

However, if the user input is incorrect, the conversion is not successful and at this point we end up with a situation
in which the model and the presentation are not in sync. To be able to detect this, a field binding has a 
*presentation valid* flag. When this flag is true, the presentation value has successfully been converted to a model
value. When it is false, the UI field contains incorrect input that can't be converted.

#### Validation

The second thing we need to notice is that even though a presentation value can be successfully converted to a model 
value, it may still be invalid in some context. Thus, we can add Vaadin `Validators` to validate the model value. If 
the validators pass all is well, but what happens if there is an error? To be able to detect this, a field binding has
a *model valid* flag. When this flag is true, the model value has passed validation. When it is false, at least one
validator has rejected the value.

By default, invalid model values will still be written to the model. You can change this by calling the 
`withWriteInvalidModelValuesDisabled` method of the `TwoWayFieldBinding` interface. The method is named like this 
because it is intended to be used in a fluent chained method call when the binding is first created.

#### Required Fields

Two-way field bindings can be marked as required, meaning the user has to provide a value. In practice, this is 
implemented as a special validator that consults with the `WritableObservableValue` whether the provided value is to
be considered empty or not. It is important to note that required value checks are performed on the model value, not
on the presentation value. Remember this when dealing with string fields, since Vaadin text fields will not return
nulls but empty strings when they are empty (you may now want to revisit the section about empty observable values).

The error message to show when a required field is missing can be either a static string or a supplier function that
returns the error message.

#### Error Reporting

From a UX point of view, the *presentation valid* and *model valid* flags are not enough ("There's something wrong with
your input but I'm not going to tell you what it is!"). We need a way of giving better feedback to the user about what
went wrong. For this, you can use a `BindingResultHandler`. This handler will be called whenever there is a value 
conversion or validation. Both successful and failed conversions and validations will be reported so that you can
either show or hide error messages to the user. You specify a binding result handler by calling the 
`withBindingResultHandler` method of the `TwoWayFieldBinding` interface.

#### Examples

Now when we (hopefully) have a better idea of how field bindings work, let's have a look at some code examples. Again,
these examples use the helper methods of the [AppModel](src/main/java/net/pkhapps/appmodel4flow/AppModel.java) class:
```java
class AppModelFieldBindingExample {
    
    private Property<String> firstName;
    private Property<String> lastName;
    private ObservableValue<String> fullName; // Combined value
    private Property<Integer> age;
    
    public void init() { 
        var firstNameField = new TextField("First name");
        AppModel.bind(firstName, firstNameField).asRequired("Please enter a first name");
        
        var lastNameField = new TextField("Last name");
        AppModel.bind(lastName, lastNameField).asRequired("Please enter a last name");
        
        var fullNameField = new TextField("Full name");
        AppModel.bindOneWay(fullName, fullNameField);
        
        var ageField = new TextField("Age");
        AppModel.bind(age, ageField, new StringToIntegerConverter("Please enter a valid age"));
    }
}
```

### Component Bindings

To do

### Binding Groups

To do