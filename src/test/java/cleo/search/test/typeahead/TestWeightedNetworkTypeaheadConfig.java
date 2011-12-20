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

package cleo.search.test.typeahead;

import java.io.File;

import cleo.search.SimpleElement;
import cleo.search.tool.WeightedNetworkTypeaheadInitializer;
import cleo.search.typeahead.NetworkTypeaheadConfig;
import cleo.search.typeahead.WeightedNetworkTypeahead;

/**
 * TestWeightedNetworkTypeaheadConfig
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public class TestWeightedNetworkTypeaheadConfig extends TestWeightedNetworkTypeahead {

  @Override
  protected WeightedNetworkTypeahead<SimpleElement> createTypeahead() throws Exception {
    NetworkTypeaheadConfig<SimpleElement> config = new NetworkTypeaheadConfig<SimpleElement>();
    
    config.setName("Network");
    
    config.setPartitionStart(getPartitionStart());
    config.setPartitionCount(getPartitionCount());
    
    config.setElementSerializer(createElementSerializer());
    config.setElementStoreDir(new File(getHomeDir(), "element-store"));
    config.setElementStoreIndexStart(getElementStoreIndexStart());
    config.setElementStoreCapacity(getElementStoreCapacity());
    config.setElementStoreSegmentMB(32);
    
    config.setConnectionsStoreDir(new File(getHomeDir(), "weighted-connections-store"));
    config.setConnectionsStoreIndexStart(getConnectionsStoreIndexStart());
    config.setConnectionsStoreCapacity(getConnectionsStoreCapacity());
    config.setConnectionsStoreSegmentMB(32);
    
    config.setConnectionFilter(createConnectionFilter());
    config.setSelectorFactory(createSelectorFactory());
    config.setFilterPrefixLength(getFilterPrefixLength());
    
    WeightedNetworkTypeaheadInitializer<SimpleElement> initializer =
      new WeightedNetworkTypeaheadInitializer<SimpleElement>(config);
    
    return (WeightedNetworkTypeahead<SimpleElement>)initializer.getTypeahead();
  }
}
