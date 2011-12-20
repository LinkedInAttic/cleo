/*
 * Copyright (c) 2011 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package cleo.search.bootstrap;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import krati.core.segment.ChannelSegmentFactory;
import krati.util.Chronos;
import cleo.search.TypeaheadElement;
import cleo.search.tool.WeightedNetworkTypeaheadInitializer;
import cleo.search.typeahead.NetworkTypeaheadConfig;
import cleo.search.typeahead.TypeaheadConfigFactory;
import cleo.search.typeahead.WeightedNetworkTypeahead;
import cleo.search.util.PropertiesResolver;

/**
 * NetworkTypeaheadWeightedConnectionsStoreMaxScn
 * 
 * @author jwu
 * @since 05/27, 2011
 */
public class NetworkTypeaheadWeightedConnectionsStoreMaxScn {
  
  /**
   * Set NetworkTypeahead weighted connections store maxScn.
   * 
   * <pre>
   * JVM arguments:
   *     -server -Xms4g -Xmx8g 
   *     
   * Program arguments:
   *     network-typeahead-member.config maxScn
   * </pre>
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    /**
     * Load properties.
     */
    Properties properties = new Properties();
    try {
      String configFile = args[0];
      FileInputStream inStream = new FileInputStream(configFile);
      InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
      properties.load(reader);
      PropertiesResolver.resolve(properties);
      properties.list(System.out);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    long maxScn = 0;
    try {
      maxScn = Long.parseLong(args[1]);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    Chronos c = new Chronos();
    NetworkTypeaheadConfig<TypeaheadElement> config;
    WeightedNetworkTypeaheadInitializer<TypeaheadElement> initializer;
    
    /**
     * Configure NetworkTypeahead
     */
    config = TypeaheadConfigFactory.createNetworkTypeaheadConfig(properties);
    config.setElementStoreCached(false); // disable cache to bypass serialization
    config.setElementStoreSegmentFactory(new ChannelSegmentFactory());
    config.setConnectionsStoreSegmentFactory(new ChannelSegmentFactory());
    initializer = new WeightedNetworkTypeaheadInitializer<TypeaheadElement>(config);
    
    WeightedNetworkTypeahead<TypeaheadElement> typeahead =
      (WeightedNetworkTypeahead<TypeaheadElement>) initializer.getTypeahead();
    
    // Set NetworkTypehead connections store hwMark
    typeahead.getConnectionsStore().saveHWMark(maxScn);
    typeahead.getConnectionsStore().sync();
    
    System.out.printf("Time: %d ms.%n", c.tick());
  }
}
