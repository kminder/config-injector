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
package net.minder.config.impl;

import org.apache.commons.beanutils.ConvertUtilsBean2;
import net.minder.config.ConfigurationAdapter;
import net.minder.config.ConfigurationAdapterFactory;
import net.minder.config.ConfigurationException;
import net.minder.config.ConfigurationInjector;
import net.minder.config.Configure;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultConfigurationInjector implements ConfigurationInjector {

  ConvertUtilsBean2 converter = new ConvertUtilsBean2();

  @Override
  public void inject( Object target, ConfigurationAdapter adapter )
      throws ConfigurationException {
    Class type = target.getClass();
    while( type != null ) {
      injectClass( type, target, adapter );
      type = type.getSuperclass();
    }
  }

  @Override
  public void inject( Object target, Object config )
      throws ConfigurationException {
    ConfigurationAdapter adapter = ConfigurationAdapterFactory.get( config );
    inject( target, adapter );
  }

  private void injectClass( Class type, Object instance, ConfigurationAdapter config )
      throws ConfigurationException {
    Field[] fields = type.getDeclaredFields();
    for( Field field : fields ) {
      injectField( field, instance, config );
    }
    Method[] methods = type.getDeclaredMethods();
    for( Method method : methods ) {
      injectMethod( method, instance, config );
    }
  }

  private void injectField( Field field, Object instance, ConfigurationAdapter adapter )
      throws ConfigurationException {
    Configure annotation = field.getAnnotation( Configure.class );
    String name = null;
    if( annotation != null ) {
      name = getConfigName( field, annotation );
      String strValue = null;
      try {
        strValue = adapter.getConfigurationValue( name );
      } catch( Exception e ) {
        throw new ConfigurationException( String.format(
            "Failed to retrieve field configuration property %s for field %s of %s via %s",
            name, field.getName(), instance.getClass().getName(), adapter.getClass().getName() ), e );
      }
      Object objValue = null;
      try {
        objValue = converter.convert( strValue, field.getType() );
      } catch( Exception e ) {
        throw new ConfigurationException( String.format(
            "Failed to convert field configuration property %s of %s",
             name, instance.getClass().getName() ), e );
      }
      field.setAccessible( true );
      try {
        field.set( instance, objValue );
      } catch( Exception e ) {
        throw new ConfigurationException( String.format(
            "Failed to inject field configuration property %s of %s",
            name, instance.getClass().getName() ), e );
      }
    }
  }

  private void injectMethod( Method method, Object instance, ConfigurationAdapter adapter )
      throws ConfigurationException {
    Configure methodTag = method.getAnnotation( Configure.class );
    if( methodTag != null ) {
      String methodName = getConfigName( method, methodTag );
      Class[] argTypes = method.getParameterTypes();
      Object[] args = new Object[ argTypes.length ];
      Annotation[][] argTags = method.getParameterAnnotations();
      for( int i=0; i<argTypes.length; i++ ) {
        String argName = getConfigName( methodName, argTags[ i ] );
        String strValue = null;
        try {
          strValue = adapter.getConfigurationValue( argName );
        } catch( Exception e ) {
          throw new ConfigurationException( String.format(
              "Failed to retrieve parameter configuration property %s for method %s of %s via %s",
              argName, methodName, instance.getClass().getName(), adapter.getClass().getName() ), e );
        }
        Object objValue = null;
        try {
          objValue = converter.convert( strValue, argTypes[ i ] );
        } catch( Exception e ) {
          throw new ConfigurationException( String.format(
              "Failed to convert parameter configuration property %s for method %s of %s",
              argName, methodName, instance.getClass().getName() ), e );
        }
        args[ i ] = objValue;
      }
      method.setAccessible( true );
      try {
        method.invoke( instance, args );
      } catch( Exception e ) {
        throw new ConfigurationException( String.format(
            "Failed to inject method configuration via %s of %s",
            methodName, instance.getClass().getName() ), e );
      }
    }
  }

  private static String pickName( String implied, Configure explicit ) {
    String name = implied;
    if( explicit != null ) {
      String tagValue = explicit.value().trim();
      if( tagValue.length() > 0 ) {
        name = tagValue;
      }
    }
    return name;
  }

  private static String getConfigName( Field field, Configure tag ) {
    return pickName( field.getName(), tag );
  }

  private static String getConfigName( String name, Annotation[] tags ) {
    if( tags != null ) {
      for( Annotation tag : tags ) {
        if( tag != null && tag instanceof Configure ) {
          Configure config = Configure.class.cast( tag );
          String tagValue = config.value().trim();
          if( tagValue.length() > 0 ) {
            name = tagValue;
            break;
          }
        }
      }
    }
    return name;
  }

  private static String getConfigName( Method method, Configure tag ) {
    return pickName( getConfigName( method ), tag );
  }

  private static String getConfigName( Method method ) {
    String methodName = method.getName();
    StringBuilder name = new StringBuilder( methodName.length() );
    if( methodName != null &&
        methodName.length() > 3 &&
        methodName.startsWith( "set" ) &&
        Character.isUpperCase( methodName.charAt( 3 ) ) ) {
      name.append( methodName.substring( 3 ) );
      name.setCharAt( 0, Character.toLowerCase( name.charAt( 0 ) ) );
    } else {
      name.append( name );
    }
    return name.toString();
  }

}
