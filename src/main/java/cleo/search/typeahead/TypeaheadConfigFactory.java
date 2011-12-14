package cleo.search.typeahead;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.connection.ConnectionFilter;
import cleo.search.util.PropertiesResolver;
import cleo.search.util.Range;

/**
 * TypeaheadConfigFactory creates standard configurations for {@link cleo.search.typeahead.Typeahead Typeahead}.
 * 
 * @author jwu
 * @since 04/21, 2011
 * 
 * <p>
 * 12/12, 2011 - Added factory methods based on config file <br/>
 */
public class TypeaheadConfigFactory {
  
  /**
   * Creates an instance of {@link GenericTypeaheadConfig} based on the specified properties.
   * 
   * <p>
   * Here is a sample for generic typeahead configuration:
   * </p>
   * 
   * <pre>
   * cleo.search.generic.typeahead.config.name=i001
   * cleo.search.generic.typeahead.config.partition.start=0
   * cleo.search.generic.typeahead.config.partition.count=1000000
   * cleo.search.generic.typeahead.config.homeDir=bootstrap/company/i001
   * 
   * cleo.search.generic.typeahead.config.elementSerializer.class=cleo.search.TypeaheadElementSerializer
   * 
   * cleo.search.generic.typeahead.config.elementStoreDir=${cleo.search.generic.typeahead.config.homeDir}/element-store
   * cleo.search.generic.typeahead.config.elementStoreIndexStart=${cleo.search.generic.typeahead.config.partition.start}
   * cleo.search.generic.typeahead.config.elementStoreCapacity=${cleo.search.generic.typeahead.config.partition.count}
   * cleo.search.generic.typeahead.config.elementStoreSegmentMB=32
   * 
   * cleo.search.generic.typeahead.config.connectionsStoreDir=${cleo.search.generic.typeahead.config.homeDir}/connections-store
   * cleo.search.generic.typeahead.config.connectionsStoreCapacity=1000000
   * cleo.search.generic.typeahead.config.connectionsStoreSegmentMB=64
   * cleo.search.generic.typeahead.config.connectionsStoreIndexSegmentMB=8
   * 
   * cleo.search.generic.typeahead.config.filterPrefixLength=2
   * cleo.search.generic.typeahead.config.maxKeyLength=5
   * </pre>
   * 
   * @param properties - Typeahead configuration properties
   * @return <code>GenericTypeaheadConfig</code> if the properties specify a correct configuration.
   * @throws Exception if the properties specify an incorrect configuration.
   */
  @SuppressWarnings("unchecked")
  public static <E extends Element> GenericTypeaheadConfig<E> createGenericTypeaheadConfig(Properties properties) throws Exception {
    // Resolve config properties
    PropertiesResolver.resolve(properties);
    
    GenericTypeaheadConfig<E> config = new GenericTypeaheadConfig<E>();
    config.setName(properties.getProperty("cleo.search.generic.typeahead.config.name"));
    
    // elementSerializer
    ElementSerializer<E> elementSerializer = (ElementSerializer<E>)
    Class.forName(properties.getProperty("cleo.search.generic.typeahead.config.elementSerializer.class")).newInstance();
    config.setElementSerializer(elementSerializer);
    
    SegmentFactory elementStoreSegmentFactory = new MemorySegmentFactory();
    
    // elementStore
    config.setElementStoreDir(new File(properties.getProperty("cleo.search.generic.typeahead.config.elementStoreDir")));
    config.setElementStoreIndexStart(Integer.parseInt(properties.getProperty("cleo.search.generic.typeahead.config.elementStoreIndexStart")));
    config.setElementStoreCapacity(Integer.parseInt(properties.getProperty("cleo.search.generic.typeahead.config.elementStoreCapacity")));
    config.setElementStoreSegmentMB(Integer.parseInt(properties.getProperty("cleo.search.generic.typeahead.config.elementStoreSegmentMB")));
    config.setElementStoreSegmentFactory(elementStoreSegmentFactory);
    
    SegmentFactory connectionsStoreIndexSegmentFactory = new MemorySegmentFactory();
    SegmentFactory connectionsStoreSegmentFactory = new MemorySegmentFactory();
    
    // connectionsStore
    config.setConnectionsStoreDir(new File(properties.getProperty("cleo.search.generic.typeahead.config.connectionsStoreDir")));
    config.setConnectionsStoreCapacity(Integer.parseInt(properties.getProperty("cleo.search.generic.typeahead.config.connectionsStoreCapacity")));
    config.setConnectionsStoreSegmentFactory(connectionsStoreSegmentFactory);
    config.setConnectionsStoreSegmentMB(Integer.parseInt(properties.getProperty("cleo.search.generic.typeahead.config.connectionsStoreSegmentMB")));
    config.setConnectionsStoreIndexSegmentFactory(connectionsStoreIndexSegmentFactory);
    config.setConnectionsStoreIndexSegmentMB(Integer.parseInt(properties.getProperty("cleo.search.generic.typeahead.config.connectionsStoreIndexSegmentMB")));
    
    config.setMaxKeyLength(Integer.parseInt(properties.getProperty("cleo.search.generic.typeahead.config.maxKeyLength")));
    config.setFilterPrefixLength(Integer.parseInt(properties.getProperty("cleo.search.generic.typeahead.config.filterPrefixLength")));
    
    return config;
  }
  
  /**
   * Creates an instance of {@link NetworkTypeaheadConfig} based on the specified properties.
   * <p>
   * Here is a sample for network typeahead configuration:
   * </p>
   * 
   * <pre>
   *  cleo.search.network.typeahead.config.name=i001
   *  cleo.search.network.typeahead.config.partition.start=0
   *  cleo.search.network.typeahead.config.partition.count=5000000
   *  cleo.search.network.typeahead.config.homeDir=network-typeahead/member/i001
   *  
   *  cleo.search.network.typeahead.config.elementSerializer.class=cleo.search.TypeaheadElementSerializer
   *  cleo.search.network.typeahead.config.connectionFilter.class=cleo.search.connection.TransitivePartitionConnectionFilter
   *  
   *  cleo.search.network.typeahead.config.elementStoreDir=${cleo.search.network.typeahead.config.homeDir}/element-store
   *  cleo.search.network.typeahead.config.elementStoreIndexStart=${cleo.search.network.typeahead.config.partition.start}
   *  cleo.search.network.typeahead.config.elementStoreCapacity=${cleo.search.network.typeahead.config.partition.count}
   *  cleo.search.network.typeahead.config.elementStoreSegmentMB=64
   *  
   *  cleo.search.network.typeahead.config.connectionsStoreDir=${cleo.search.network.typeahead.config.homeDir}/weighted-connections-store
   *  cleo.search.network.typeahead.config.connectionsStoreIndexStart=0
   *  cleo.search.network.typeahead.config.connectionsStoreCapacity=150000000
   *  cleo.search.network.typeahead.config.connectionsStoreSegmentMB=64
   *  
   *  cleo.search.network.typeahead.config.filterPrefixLength=2
   * </pre>
   * 
   * @param properties - Typeahead configuration properties
   * @return <code>NetworkTypeaheadConfig</code> if the properties specify a correct configuration.
   * @throws Exception if the properties specify an incorrect configuration. 
   */
  @SuppressWarnings("unchecked")
  public static <E extends Element> NetworkTypeaheadConfig<E> createNetworkTypeaheadConfig(Properties properties) throws Exception {
    // Resolve config properties
    PropertiesResolver.resolve(properties);
    
    NetworkTypeaheadConfig<E> config = new NetworkTypeaheadConfig<E>();
    config.setName(properties.getProperty("cleo.search.network.typeahead.config.name"));
    
    // connectionFilter for network partition
    config.setPartitionStart(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.partition.start")));
    config.setPartitionCount(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.partition.count")));
    Range partitionRange = new Range(config.getPartitionStart(), config.getPartitionCount());
    
    ConnectionFilter connectionFilter = (ConnectionFilter)
      Class.forName(properties.getProperty("cleo.search.network.typeahead.config.connectionFilter.class")).getConstructor(Range.class).newInstance(partitionRange);
    config.setConnectionFilter(connectionFilter);
    
    // elementSerializer
    ElementSerializer<E> elementSerializer = (ElementSerializer<E>)
      Class.forName(properties.getProperty("cleo.search.network.typeahead.config.elementSerializer.class")).newInstance();
    config.setElementSerializer(elementSerializer);
    
    // elementStore
    config.setElementStoreDir(new File(properties.getProperty("cleo.search.network.typeahead.config.elementStoreDir")));
    config.setElementStoreSegmentMB(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.elementStoreSegmentMB")));
    config.setElementStoreIndexStart(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.elementStoreIndexStart")));
    config.setElementStoreCapacity(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.elementStoreCapacity")));
    
    // connectionsStore
    config.setConnectionsStoreDir(new File(properties.getProperty("cleo.search.network.typeahead.config.connectionsStoreDir")));
    config.setConnectionsStoreCapacity(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.connectionsStoreCapacity")));
    config.setConnectionsStoreIndexStart(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.connectionsStoreIndexStart")));
    config.setConnectionsStoreSegmentMB(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.connectionsStoreSegmentMB")));
    
    // BloomFilter prefix length
    config.setFilterPrefixLength(Integer.parseInt(properties.getProperty("cleo.search.network.typeahead.config.filterPrefixLength")));
    
    return config;
  }
  
  /**
   * Creates an instance of {@link GenericTypeaheadConfig} based on the specified configuration properties file.
   * 
   * @param configFile - the configuration properties file
   * @return the configuration for instantiating {@link cleo.search.typeahead.GenericTypeahead GenericTypeahead}.
   * @throws Exception if the configuration cannot be created.
   */
  public static <E extends Element> GenericTypeaheadConfig<E> createGenericTypeaheadConfig(File configFile) throws Exception {
    // Load config properties
    Properties properties = new Properties();
    FileInputStream inStream = new FileInputStream(configFile);
    InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
    properties.load(reader);
    
    try {
      return TypeaheadConfigFactory.createGenericTypeaheadConfig(properties);
    } finally {
      // List config properties
      properties.list(System.out);
    }
  }
  
  /**
   * Creates an instance of {@link NetworkTypeaheadConfig} based on the specified configuration properties file.
   * 
   * @param configFile - the configuration properties file
   * @return the configuration for instantiating {@link cleo.search.typeahead.NetworkTypeahead NetworkTypeahead}.
   * @throws Exception if the configuration cannot be created.
   */
  public static <E extends Element> NetworkTypeaheadConfig<E> createNetworkTypeaheadConfig(File configFile) throws Exception {
    // Load config properties
    Properties properties = new Properties();
    FileInputStream inStream = new FileInputStream(configFile);
    InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
    properties.load(reader);
    
    try {
      return TypeaheadConfigFactory.createNetworkTypeaheadConfig(properties);
    } finally {
      // List config properties
      properties.list(System.out);
    }
  }
}
