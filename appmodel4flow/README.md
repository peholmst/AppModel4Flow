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
functions for use with boolean and string dependencies. Check it out if you want to know more.

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

## Selections

## Bindings

## All together now
