/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.minder.config;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UnitTests {

  public static class TestBean {
    @Configure
    String stringMember = "stringDefault";

    @Configure
    int intMember = 1;

    @Configure
    Integer integerMember = Integer.valueOf( 1 );

    @Configure
    public void setStringProp( String s ) {
      stringPropField = s;
    }
    protected String stringPropField = "stringDefault";

    @Configure("altStringProp")
    public void setNamedStringProp( String s ) {
      stringPropFieldAlt = s;
    }
    protected String stringPropFieldAlt = "stringDefault";

    @Configure
    public void setNamedArgMethod( @Configure("altArgStringProp") String s ) {
      stringPropFieldAltArg = s;
    }
    protected String stringPropFieldAltArg = "stringDefault";

    @Configure
    public void setMultiArgs(
        @Configure("multiArg1") String s,
        @Configure("multiArg2") Integer i,
        @Configure("multiArg3") int n ) {
      multiArgStringField = s;
      multiArgIntegerField = i;
      multiArgIntField = n;
    }
    String multiArgStringField = "default";
    Integer multiArgIntegerField = 0;
    int multiArgIntField = 0;

  }

  @Test
  public void testMapOfStrings() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();

    Map<String,String> testConfig = new HashMap<String,String>();
    testConfig.put( "stringMember", "stringValue" );
    testConfig.put( "intMember", "2" );
    testConfig.put( "integerMember", "2" );
    testConfig.put( "stringProp", "stringValue" );
    testConfig.put( "altStringProp", "stringValue" );
    testConfig.put( "altArgStringProp", "stringValue" );
    testConfig.put( "multiArg1", "stringValue" );
    testConfig.put( "multiArg2", "42" );
    testConfig.put( "multiArg3", "42" );

    TestBean testBean = new TestBean();

    injector.inject( testBean, testConfig );

    assertThat( testBean.stringMember, is( "stringValue" ) );
    assertThat( testBean.intMember, is( 2 ) );
    assertThat( testBean.integerMember, is( new Integer(2) ) );
    assertThat( testBean.stringPropField, is( "stringValue" ) );
    assertThat( testBean.stringPropFieldAlt, is( "stringValue" ) );
    assertThat( testBean.stringPropFieldAltArg, is( "stringValue" ) );
    assertThat( testBean.multiArgStringField, is( "stringValue" ) );
    assertThat( testBean.multiArgIntegerField, is( 42 ) );
    assertThat( testBean.multiArgIntField, is( 42 ) );
  }

  @Test
  public void testProperties() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();

    Properties testConfig = new Properties();
    testConfig.put( "stringMember", "stringValue" );
    testConfig.put( "intMember", "2" );
    testConfig.put( "integerMember", "2" );
    testConfig.put( "stringProp", "stringValue" );
    testConfig.put( "altStringProp", "stringValue" );
    testConfig.put( "altArgStringProp", "stringValue" );
    testConfig.put( "multiArg1", "stringValue" );
    testConfig.put( "multiArg2", "42" );
    testConfig.put( "multiArg3", "42" );

    TestBean testBean = new TestBean();

    injector.inject( testBean, testConfig );

    assertThat( testBean.stringMember, is( "stringValue" ) );
    assertThat( testBean.intMember, is( 2 ) );
    assertThat( testBean.integerMember, is( new Integer(2) ) );
    assertThat( testBean.stringPropField, is( "stringValue" ) );
    assertThat( testBean.stringPropFieldAlt, is( "stringValue" ) );
    assertThat( testBean.stringPropFieldAltArg, is( "stringValue" ) );
    assertThat( testBean.multiArgStringField, is( "stringValue" ) );
    assertThat( testBean.multiArgIntegerField, is( 42 ) );
    assertThat( testBean.multiArgIntField, is( 42 ) );
  }

  public static class TestAdapter implements ConfigurationAdapter {

    private Map<String,String> config;

    public TestAdapter( Map<String,String> config ) {
      this.config = config;
    }

    @Override
    public String getConfigurationValue( String name ) {
      return config.get( name );
    }

  }

  @Test
  public void testExplicitProvider() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();

    Map<String,String> testConfig = new HashMap<String,String>();
    testConfig.put( "stringMember", "stringValue" );
    testConfig.put( "intMember", "2" );
    testConfig.put( "integerMember", "2" );
    testConfig.put( "stringProp", "stringValue" );
    testConfig.put( "altStringProp", "stringValue" );
    testConfig.put( "altArgStringProp", "stringValue" );
    testConfig.put( "multiArg1", "stringValue" );
    testConfig.put( "multiArg2", "42" );
    testConfig.put( "multiArg3", "42" );

    TestBean testBean = new TestBean();

    injector.inject( testBean, new TestAdapter(testConfig) );

    assertThat( testBean.stringMember, is( "stringValue" ) );
    assertThat( testBean.intMember, is( 2 ) );
    assertThat( testBean.integerMember, is( new Integer(2) ) );
    assertThat( testBean.stringPropField, is( "stringValue" ) );
    assertThat( testBean.stringPropFieldAlt, is( "stringValue" ) );
    assertThat( testBean.stringPropFieldAltArg, is( "stringValue" ) );
    assertThat( testBean.multiArgStringField, is( "stringValue" ) );
    assertThat( testBean.multiArgIntegerField, is( 42 ) );
    assertThat( testBean.multiArgIntField, is( 42 ) );
  }

  @Test
  public void testMapOfObjects() {
    ConfigurationInjector injector = ConfigurationInjectorFactory.create();

    Map<Object,Object> testConfig = new HashMap<Object,Object>();
    testConfig.put( "stringMember", "stringValue" );
    testConfig.put( "intMember", 42 );
    testConfig.put( "integerMember", new Integer(42) );
    testConfig.put( "stringProp", "stringValue" );
    testConfig.put( "altStringProp", "stringValue" );
    testConfig.put( "altArgStringProp", "stringValue" );
    testConfig.put( "multiArg1", "stringValue" );
    testConfig.put( "multiArg2", new Integer(42) );
    testConfig.put( "multiArg3", "42" );

    TestBean testBean = new TestBean();

    injector.inject( testBean, testConfig );

    assertThat( testBean.stringMember, is( "stringValue" ) );
    assertThat( testBean.intMember, is( 42 ) );
    assertThat( testBean.integerMember, is( new Integer(42) ) );
    assertThat( testBean.stringPropField, is( "stringValue" ) );
    assertThat( testBean.stringPropFieldAlt, is( "stringValue" ) );
    assertThat( testBean.stringPropFieldAltArg, is( "stringValue" ) );
    assertThat( testBean.multiArgStringField, is( "stringValue" ) );
    assertThat( testBean.multiArgIntegerField, is( 42 ) );
    assertThat( testBean.multiArgIntField, is( 42 ) );
  }

}
