package cleo.search.test.typeahead;

import java.io.File;

import cleo.search.SimpleElement;
import cleo.search.tool.BrowseTypeaheadInitializer;
import cleo.search.typeahead.BrowseTypeahead;
import cleo.search.typeahead.BrowseTypeaheadConfig;

/**
 * TestBrowseTypeaheadConfig
 * 
 * @author jwu
 * @since 02/13, 2011
 */
public class TestBrowseTypeaheadConfig extends TestBrowseTypeahead {

  @Override
  protected BrowseTypeahead<SimpleElement> createTypeahead() throws Exception {
    BrowseTypeaheadConfig<SimpleElement> config = new BrowseTypeaheadConfig<SimpleElement>();
    
    config.setName("Browse");
    config.setSelectorFactory(createSelectorFactory());
    config.setElementSerializer(createElementSerializer());
    config.setElementStoreDir(new File(getHomeDir(), "element-store"));
    config.setElementStoreIndexStart(getElementStoreIndexStart());
    config.setElementStoreCapacity(getElementStoreCapacity());
    config.setElementStoreSegmentMB(32);
    config.setFilterPrefixLength(getFilterPrefixLength());
    
    BrowseTypeaheadInitializer<SimpleElement> initializer =
      new BrowseTypeaheadInitializer<SimpleElement>(config);
    
    return (BrowseTypeahead<SimpleElement>)initializer.getTypeahead();
  }
  
}
