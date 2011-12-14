package cleo.search.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

import krati.core.segment.ChannelSegmentFactory;
import krati.core.segment.WriteBufferSegmentFactory;
import krati.util.Chronos;
import cleo.search.TypeaheadElement;
import cleo.search.connection.ConnectionFilter;
import cleo.search.store.ArrayStoreWeights;
import cleo.search.tool.WeightedNetworkTypeaheadInitializer;
import cleo.search.typeahead.NetworkTypeaheadConfig;
import cleo.search.typeahead.TypeaheadConfigFactory;
import cleo.search.typeahead.WeightedNetworkTypeahead;
import cleo.search.util.ConnectionsHandler;
import cleo.search.util.ConnectionsScanner;
import cleo.search.util.MultiConnectionsHandler;
import cleo.search.util.PropertiesResolver;

/**
 * NetworkTypeaheadWeightedConnectionsStoreBootstrap
 * 
 * @author jwu
 * @since 06/13, 2011
 */
public class NetworkTypeaheadWeightedConnectionsStoreBootstrap {
  
  /**
   * <pre>
   * java NetworkTypeaheadWeightedConnectionsStoreBootstrap -server -Xms4G -Xmx16G \
   *      connectionsDir maxScn configFile ...
   *      
   * java NetworkTypeaheadWeightedConnectionsStoreBootstrap -server -Xms4G -Xmx16G \
   *      bootstrap/member/connections 170403771779 \
   *      config/network-typeahead/member/i001.config \
   *      config/network-typeahead/member/i002.config \
   *      config/network-typeahead/member/i003.config
   * </pre>
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    Chronos c = new Chronos();
    MultiConnectionsHandler multiHandler = new MultiConnectionsHandler();
    
    File connectionsDir = new File(args[0]);
    long maxScn = Long.parseLong(args[1]);
    for(int i = 2; i < args.length; i++) {
      /**
       * Load bootstrap properties.
       */
      Properties properties = new Properties();
      try {
        String configFile = args[i];
        FileInputStream inStream = new FileInputStream(configFile);
        InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
        properties.load(reader);
        PropertiesResolver.resolve(properties);
        properties.list(System.out);
      } catch(Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
      
      /**
       * Configure NetworkTypeahead
       */
      NetworkTypeaheadConfig<TypeaheadElement> config =
        TypeaheadConfigFactory.createNetworkTypeaheadConfig(properties);
      config.setElementStoreCached(false); // disable cache to bypass serialization
      config.setElementStoreSegmentFactory(new ChannelSegmentFactory());
      config.setConnectionsStoreSegmentFactory(new WriteBufferSegmentFactory(config.getConnectionsStoreSegmentMB()));
      
      WeightedNetworkTypeaheadInitializer<TypeaheadElement> initializer =
        new WeightedNetworkTypeaheadInitializer<TypeaheadElement>(config);
      WeightedNetworkTypeahead<TypeaheadElement> typeahead =
        (WeightedNetworkTypeahead<TypeaheadElement>) initializer.getTypeahead();

      ConnectionFilter connectionFilter = typeahead.getConnectionFilter();
      ArrayStoreWeights weightedConnectionsStore = typeahead.getConnectionsStore();
      System.out.printf("Added %s%n", config.getConnectionsStoreDir().getParent());
      
      multiHandler.add(new WeightedConnectionsStoreConnectionsHandler(weightedConnectionsStore, connectionFilter, maxScn));
    }
    
    if(multiHandler.isEmpty()) return;
    
    /**
     * Bootstrap weighted-connections-store
     */
    System.out.println("-- starting bootstrap --");
    
    ConnectionsScanner scanner = new ConnectionsScanner(connectionsDir);
    scanner.scan(multiHandler);
    
    Iterator<ConnectionsHandler> iter = multiHandler.iterator();
    while(iter.hasNext()) {
      ArrayStoreWeights weightedConnectionsStore;
      weightedConnectionsStore = ((WeightedConnectionsStoreConnectionsHandler)iter.next()).getWeightedConnectionsStore();
      weightedConnectionsStore.sync();
    }
    
    System.out.println();
    System.out.printf("Bootstrap done in %d seconds%n", c.getTotalTime()/1000);
  }
}
