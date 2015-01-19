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

import java.util.Iterator;
import java.util.ServiceLoader;

public class ConfigurationInjectorFactory {

  private static ConfigurationInjector INSTANCE = null;

  public static synchronized ConfigurationInjector create() {
    if( INSTANCE == null ) {
      INSTANCE = loadImplementationClass();
    }
    return INSTANCE;
  }

  public static void configure( Object target, Object source ) {
    ConfigurationInjectorFactory.create().configure( target, source );
  }

  public static void configure( Object target, ConfigurationAdapter source ) {
    ConfigurationInjectorFactory.create().configure( target, source );
  }

  private static synchronized ConfigurationInjector loadImplementationClass() {
    ConfigurationInjector injector = null;
    ServiceLoader<ConfigurationInjector> loader = ServiceLoader.load( ConfigurationInjector.class );
    if( loader != null ) {
      Iterator<ConfigurationInjector> iterator = loader.iterator();
      if( iterator != null ) {
        while( iterator.hasNext() ) {
          injector = iterator.next();
          break;
        }
      }
    }
    if( injector == null ) {
      throw new ConfigurationException( String.format(
          "Failed to load an implementation of %s", ConfigurationInjector.class.getName() ) );
    }
    return injector;
  }


//  private Object source = null;
//  private Object target = null;
//  private ConfigurationAdapter adapter = null;
//  private ConfigurationBinding binding = null;
//
//  public static ConfigurationInjectorFactory create() {
//    return new ConfigurationInjectorFactory();
//  }
//
//  public ConfigurationInjectorFactory target( Object target ) {
//    this.target = target;
//    return this;
//  }
//
//  public ConfigurationInjectorFactory source( Object source ) {
//    this.source = source;
//    return this;
//  }
//
//  public ConfigurationInjectorFactory adapter( ConfigurationAdapter adapter ) {
//    this.adapter = adapter;
//    return this;
//  }
//
//  public ConfigurationInjectorFactory adapter( ConfigurationBinding binding ) {
//    this.binding = binding;
//    return this;
//  }
//
//  public void configure() throws ConfigurationException {
//    ConfigurationInjector injector = new DefaultConfigurationInjector();
//    if( adapter == null ) {
//      adapter = new BeanConfigurationAdapter( source );
//    }
//    if( binding == null ) {
//      binding = new DefaultConfigurationBinding();
//    }
//    injector.configure( target, adapter, binding );
//  }

}
