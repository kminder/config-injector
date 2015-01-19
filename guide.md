User's Guide
============

ConfigurationAdapters
---------------------
Additional default ConfigurationAdapter implementations can be added via standard ServiceLoader techniques.
The service class that needs to be declared is ConfigurationAdapterDescriptor.

The ConfigurationAdapters interface is deliberately very easy to implement.
```java
package net.minder.config;

public interface ConfigurationAdapter {

  String getConfigurationValue( String name ) throws ConfigurationException;

}
```