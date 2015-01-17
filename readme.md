ConfigurationInjector
=====================
A very basic framework inspired by dependency injection but targeted at simple configuration injection.  

Map Example
-----------
This is a basic example of injecting into a class member from a Map<String,String>.
```java
package net.minder.config;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapSample {

  public static class Target {
    @Configure
    private int retryLimit = 3;
  }

  static Map<String,String> config = new HashMap<String,String>();
  static { config.put( "retryLimit", "5" ); }

  @Test
  public void sample() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();
    Target target = new Target();
    injector.inject( target, config );
    assertThat( target.retryLimit, is(5) );
  }

}
```

Properties Example
------------------
This example illustrates injection from a Properties object and also specifying the binding name via the Configure annotation.
```java
package net.minder.config;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PropertiesSample {

  public static class Target {
    @Configure("user.name")
    private String username = "nobody";
  }

  @Test
  public void sample() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();
    Target target = new Target();
    injector.inject( target, System.getProperties() );
    assertThat( target.username, is( System.getProperty( "user.name" ) ) );
  }

}
```

Adapter Example
---------------
This example shows the use of the ConfigurationAdapter interface.
A custom ConfigurationAdapter will be useful if none of the built-in adapters are not sufficient.
In this contrived example the config source is an untyped Hashtable where all names are upper case. 
```java
package net.minder.config;

import org.junit.Test;

import java.util.Hashtable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AdapterSample {

  public static class Target {
    @Configure
    private String username = null;
  }

  public static class Adapter implements ConfigurationAdapter {
    private Hashtable config;
    public Adapter( Hashtable config ) {
      this.config = config;
    }
    @Override
    public String getConfigurationValue( String name ) throws ConfigurationException {
      Object value = config.get( name.toUpperCase() );
      return value == null ? null : value.toString();
    }
  }

  static Hashtable config = new Hashtable();
  static{ config.put( "USERNAME", "somebody" ); }

  @Test
  public void sample() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();
    Target target = new Target();
    Adapter adapter = new Adapter( config );
    injector.inject( target, adapter );
    assertThat( target.username, is("somebody") );
  }

}
```

ConfigurationAdapters
---------------------
Additional default ConfigurationAdapter implementations can be added via standard ServiceLoader techniques.
The service class that needs to be declared is ConfigurationAdapterDescriptor.