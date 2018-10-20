# AppModel4Flow Demo Application

This is a simple demo application that shows some (but not all) of the features of AppModel4Flow.

To try it out, clone and build AppModel4Flow. Then, inside this module, run:

```
$ mvn jetty:run
```

You should then be able to access the application at [http://localhost:8080](http://localhost:8080).

## What can I find in the demo?

`ContactModel` and `ContactController` contain the model stuff, i.e. the actions and properties. The 
controller has the same scope as `ContactView` and the model the same scope as `ContactDialog`.
 
The controller uses an in-memory data provider to hold the data so it contains some boilerplate code to
manage that. In a real-world application, your controller - or whatever you choose to call it - would
interact with a backend layer through services or commands. You would *not* implement any business logic
inside actions.

`ContactView` and `ContactDialog` contain the presentation stuff and this is also where the bindings are
created. The view demonstrates action and selection bindings whereas the dialog demonstrates field
bindings, component bindings, action bindings and binding groups.
