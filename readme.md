ConfigurationInjector
=====================
A very basic framework inspired by dependency injection but targeted at simple configuration injection.  

Map Example (Field Injection)
-----------------------------
This is a basic example of injecting into a class member from a Map<String,String>.
```java
package net.minder.config;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapFieldSample {

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

Map Example (Method Injection)
------------------------------
This is a basic example of injecting via a method from a Map<String,String>.
```java
package net.minder.config;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MapMethodSample {

  public static class Target {
    private int limit = 3;

    @Configure
    public void setRetryLimit( int value ) {
      limit = value;
    }
  }

  static Map<String,String> config = new HashMap<String,String>();
  static { config.put( "retryLimit", "5" ); }

  @Test
  public void sample() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();
    Target target = new Target();
    injector.inject( target, config );
    assertThat( target.limit, is(5) );
  }

}
```

Properties Example (Field Injection)
------------------------------------
This example illustrates injection from a Properties object.
It also show specifying the binding name via the Configure annotation.
The System.getProperties() object is used as a convenient source of configuration for this sample. 
```java
package net.minder.config;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PropertiesFieldSample {

  public static class Target {
    @Configure("user.name")
    private String user = "nobody";
  }

  @Test
  public void sample() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();
    Target target = new Target();
    injector.inject( target, System.getProperties() );
    assertThat( target.user, is( System.getProperty( "user.name" ) ) );
  }

}
```

Properties Example (Method Injection)
-------------------------------------
This example demonstrates method injection with name binding for methods with a single and multiple parameters. 
```java
package net.minder.config;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PropertiesMethodSample {

  public static class Target {

    private String user = "nobody";
    private String home = "nowhere";
    private String temp = "nowhere";

    @Configure("user.name")
    public void setUser( String value ) {
      user = value;
    }

    @Configure
    public void setDirs(
        @Configure("home.dir") String home,
        @Configure("temp.dir") String temp ) {
      this.home = home;
      this.temp = temp;
    }
  }

  @Test
  public void sample() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();
    Target target = new Target();
    injector.inject( target, System.getProperties() );
    assertThat( target.user, is( System.getProperty( "user.name" ) ) );
    assertThat( target.home, is( System.getProperty( "home.dir" ) ) );
    assertThat( target.temp, is( System.getProperty( "temp.dir" ) ) );
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

The ConfigurationAdapters interface is deliberately very easy to implement.
```java
package net.minder.config;

public interface ConfigurationAdapter {

  String getConfigurationValue( String name ) throws ConfigurationException;

}
```