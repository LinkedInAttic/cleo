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
import cleo.search.tool.ScannerTypeaheadInitializer;
import cleo.search.typeahead.ScannerTypeahead;
import cleo.search.typeahead.ScannerTypeaheadConfig;

/**
 * TestScannerTypeaheadConfig
 * 
 * @author jwu
 * @since 03/24, 2011
 */
public class TestScannerTypeaheadConfig extends TestScannerTypeahead {

  @Override
  protected ScannerTypeahead<SimpleElement> createTypeahead() throws Exception {
    ScannerTypeaheadConfig<SimpleElement> config = new ScannerTypeaheadConfig<SimpleElement>();
    
    config.setName("Browse");
    config.setSelectorFactory(createSelectorFactory());
    config.setElementSerializer(createElementSerializer());
    config.setElementStoreDir(new File(getHomeDir(), "element-store"));
    config.setElementStoreIndexStart(getElementStoreIndexStart());
    config.setElementStoreCapacity(getElementStoreCapacity());
    config.setElementStoreSegmentMB(32);
    config.setFilterPrefixLength(getFilterPrefixLength());
    
    ScannerTypeaheadInitializer<SimpleElement> initializer =
      new ScannerTypeaheadInitializer<SimpleElement>(config);
    
    return (ScannerTypeahead<SimpleElement>)initializer.getTypeahead();
  }
}
