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
import cleo.search.tool.GenericTypeaheadInitializer;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.typeahead.GenericTypeaheadConfig;

import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;

/**
 * TestGenericTypeaheadConfig
 * 
 * @author jwu
 * @since 02/13, 2011
 */
public class TestGenericTypeaheadConfig extends TestGenericTypeahead {

  @Override
  protected GenericTypeahead<SimpleElement> createTypeahead() throws Exception {
    GenericTypeaheadConfig<SimpleElement> config = new GenericTypeaheadConfig<SimpleElement>();
    
    config.setName("Generic");
    config.setElementSerializer(createElementSerializer());
    config.setElementStoreDir(new File(getHomeDir(), "element-store"));
    config.setElementStoreIndexStart(getElementStoreIndexStart());
    config.setElementStoreCapacity(getElementStoreCapacity());
    config.setElementStoreSegmentMB(32);
    
    int connectionsStoreCapacity = 500000;
    int connectionsStoreIndexSegmentMB = 8;
    SegmentFactory connectionsStoreIndexSegmentFactory = new MemorySegmentFactory();
    int connectionsStoreSegmentMB = 32;
    SegmentFactory connectionsStoreSegmentFactory = new MemorySegmentFactory();
    
    config.setConnectionsStoreCapacity(connectionsStoreCapacity);
    config.setConnectionsStoreDir(new File(getHomeDir(), "connections-store"));
    config.setConnectionsStoreIndexSegmentFactory(connectionsStoreIndexSegmentFactory);
    config.setConnectionsStoreIndexSegmentMB(connectionsStoreIndexSegmentMB);
    config.setConnectionsStoreSegmentFactory(connectionsStoreSegmentFactory);
    config.setConnectionsStoreSegmentMB(connectionsStoreSegmentMB);
    
    config.setSelectorFactory(createSelectorFactory());
    config.setFilterPrefixLength(getFilterPrefixLength());
    config.setMaxKeyLength(getMaxKeyLength());
    
    GenericTypeaheadInitializer<SimpleElement> initializer =
      new GenericTypeaheadInitializer<SimpleElement>(config);
    
    return (GenericTypeahead<SimpleElement>)initializer.getTypeahead();
  }
}
