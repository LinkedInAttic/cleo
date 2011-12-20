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
import cleo.search.tool.VanillaNetworkTypeaheadInitializer;
import cleo.search.typeahead.VanillaNetworkTypeahead;
import cleo.search.typeahead.NetworkTypeaheadConfig;

/**
 * TestVanillaNetworkTypeaheadConfig
 * 
 * @author jwu
 * @since 02/13, 2011
 */
public class TestVanillaNetworkTypeaheadConfig extends TestVanillaNetworkTypeahead {

  @Override
  protected VanillaNetworkTypeahead<SimpleElement> createTypeahead() throws Exception {
    NetworkTypeaheadConfig<SimpleElement> config = new NetworkTypeaheadConfig<SimpleElement>();
    
    config.setName("Network");

    config.setPartitionStart(getPartitionStart());
    config.setPartitionCount(getPartitionCount());
    
    config.setElementSerializer(createElementSerializer());
    config.setElementStoreDir(new File(getHomeDir(), "element-store"));
    config.setElementStoreIndexStart(getElementStoreIndexStart());
    config.setElementStoreCapacity(getElementStoreCapacity());
    config.setElementStoreSegmentMB(32);
    
    config.setConnectionsStoreDir(new File(getHomeDir(), "connections-store"));
    config.setConnectionsStoreIndexStart(getConnectionsStoreIndexStart());
    config.setConnectionsStoreCapacity(getConnectionsStoreCapacity());
    config.setConnectionsStoreSegmentMB(32);
    
    config.setConnectionFilter(createConnectionFilter());
    config.setSelectorFactory(createSelectorFactory());
    config.setFilterPrefixLength(getFilterPrefixLength());
    
    VanillaNetworkTypeaheadInitializer<SimpleElement> initializer =
      new VanillaNetworkTypeaheadInitializer<SimpleElement>(config);
    
    return (VanillaNetworkTypeahead<SimpleElement>)initializer.getTypeahead();
  }
}
