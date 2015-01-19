ToDo
====
* ConfigurationInjectorFactory.create().target( target ).source( source ).binding( binding ).configure();
* Add @Binding(name) instead of overloading @Configure(name)
* Add CDI example to samples.
* Move adapters to a separate package.
* Implement Bean adapter.
* Consider tyring all adapters that support a given type.
* If you do that consider changing the inject method to inject( target, adapter... )
* Think about a separate ConfigurationBinding mechanism for external name translation.

Done
====
* Add configure()/inject() methods to *Factory class and change samples to simplify.
* Consider configure() instead of inject()
* @Optional/@Required handling - Injection should fail if all required injecting aren't fulfilled.
* Add @Default(value) especially for method parameters.
