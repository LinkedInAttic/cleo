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
