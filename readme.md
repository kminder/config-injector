```
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
  static { config.put( "configField", "5" ); }

  @Test
  public void sample() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();
    Target target = new Target();
    injector.inject( target, config );
    assertThat( target.retryLimit, is(5) );
  }

}
```

```
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

```
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
      Object value = config.get( name );
      return value == null ? null : value.toString();
    }
  }

  static Hashtable config = new Hashtable();
  static{ config.put( "username", "somebody" ); }

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