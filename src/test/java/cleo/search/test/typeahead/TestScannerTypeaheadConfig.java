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
