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

import net.minder.config.ConfigurationAdapter;
import net.minder.config.spi.ConfigurationAdapterDescriptor;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;

public class BeanConfigurationAdapterDescriptorTest {

  @Test
  public void testServiceLoader() {
    ServiceLoader<ConfigurationAdapterDescriptor> loader = ServiceLoader.load( ConfigurationAdapterDescriptor.class );
    Iterator<ConfigurationAdapterDescriptor> i = loader.iterator();
    while( i.hasNext() ) {
      if( i.next() instanceof BeanConfigurationAdapterDescriptor ) {
        return;
      }
    }
    fail( "Failed to load BeanConfigurationAdapterDescriptor" );
  }

  @Test
  public void testDescriptor() {
    ConfigurationAdapterDescriptor descriptor = new BeanConfigurationAdapterDescriptor();
    Map<Class<?>,Class<? extends ConfigurationAdapter>> map = descriptor.providedConfigurationAdapters();
    assertThat( map, hasKey( (Class)Object.class ) );
    Class<? extends ConfigurationAdapter> type = map.get( Object.class );
    assertThat(
        "Descriptor didn't return " + BeanConfigurationAdapter.class.getName(),
        type == BeanConfigurationAdapter.class );
  }

}
