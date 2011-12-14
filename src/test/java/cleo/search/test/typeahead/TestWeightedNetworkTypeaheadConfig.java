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
