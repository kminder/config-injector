ConfigurationInjector
=====================
A very basic framework inspired by dependency injection but targeted at simple configuration injection.  

Usage
-----
```java
package net.minder.config;

import org.junit.Test;
import static net.minder.config.ConfigurationInjectorBuilder.configuration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UsageTest {

  class Target {
    @Configure
    private String user;
  }

  @Test
  public void testFieldBindingUsingBuilderBinding() {
    Target target = new Target();
    configuration()
        .target( target )
        .source( System.getProperties() )
        .bind( "user", "user.name" )
        .inject();
    assertThat( target.user, is(System.getProperty("user.name")));
  }

}
```