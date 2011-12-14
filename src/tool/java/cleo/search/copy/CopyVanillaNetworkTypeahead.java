package cleo.search.copy;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import krati.core.segment.MappedSegmentFactory;
import krati.core.segment.WriteBufferSegmentFactory;
import krati.util.Chronos;
import cleo.search.TypeaheadElement;
import cleo.search.tool.VanillaNetworkTypeaheadInitializer;
import cleo.search.typeahead.NetworkTypeaheadConfig;
import cleo.search.typeahead.TypeaheadConfigFactory;
import cleo.search.typeahead.VanillaNetworkTypeahead;
import cleo.search.util.PropertiesResolver;

/**
 * CopyVanillaNetworkTypeahead
 * 
 * @author jwu
 * @since 06/03, 2011
 */
public class CopyVanillaNetworkTypeahead  {
  
  /**
   * Copy VanillaNetworkTypeahead.
   * 
   * <pre>
   * JVM arguments:
   *     -server -Xms10g -Xmx20g 
   *     
   * Program arguments:
   *     network-typeahead-member-source.config network-typeahead-member-target.config targetConnectionStoreHWMark
   * </pre>
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    /**
     * Load source properties.
     */
    Properties sourceProperties = new Properties();
    try {
      String configFile = args[0];
      FileInputStream inStream = new FileInputStream(configFile);
      InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
      sourceProperties.load(reader);
      PropertiesResolver.resolve(sourceProperties);
      sourceProperties.list(System.out);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    /**
     * Load target properties.
     */
    Properties targetProperties = new Properties();
    try {
      String configFile = args[1];
      FileInputStream inStream = new FileInputStream(configFile);
      InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
      targetProperties.load(reader);
      PropertiesResolver.resolve(targetProperties);
      targetProperties.list(System.out);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    long targetConnectionsStoreHWMark = 0;
    try {
      targetConnectionsStoreHWMark = Long.parseLong(args[2]);
    } catch(Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    Chronos c = new Chronos();
    NetworkTypeaheadConfig<TypeaheadElement> config;
    VanillaNetworkTypeaheadInitializer<TypeaheadElement> initializer;
    
    /**
     * Configure source NetworkTypeahead
     */
    config = TypeaheadConfigFactory.createNetworkTypeaheadConfig(sourceProperties);
    config.setElementStoreCached(false); // disable cache to bypass serialization
    config.setElementStoreSegmentFactory(new MappedSegmentFactory());
    config.setConnectionsStoreSegmentFactory(new MappedSegmentFactory());
    initializer = new VanillaNetworkTypeaheadInitializer<TypeaheadElement>(config);
    
    VanillaNetworkTypeahead<TypeaheadElement> sourceTypeahead =
      (VanillaNetworkTypeahead<TypeaheadElement>) initializer.getTypeahead();
    long sourceLoadTime = c.tick();
    
    /**
     * Configure target NetworkTypeahead
     */
    config = TypeaheadConfigFactory.createNetworkTypeaheadConfig(targetProperties);
    config.setElementStoreCached(false); // disable cache to bypass serialization
    config.setElementStoreSegmentFactory(new MappedSegmentFactory());
    config.setConnectionsStoreSegmentFactory(new WriteBufferSegmentFactory(config.getConnectionsStoreSegmentMB()));
    initializer = new VanillaNetworkTypeaheadInitializer<TypeaheadElement>(config);
    
    VanillaNetworkTypeahead<TypeaheadElement> targetTypeahead =
      (VanillaNetworkTypeahead<TypeaheadElement>) initializer.getTypeahead();
    long targetLoadTime = c.tick();
    
    // Set target NetworkTypehead connections store hwMark before copying.
    targetTypeahead.getConnectionsStore().saveHWMark(targetConnectionsStoreHWMark);
    
    // Copy now!
    Copy.copy(sourceTypeahead, targetTypeahead);
    long copyTime = c.tick();
    
    // Set target NetworkTypehead connections store hwMark after copying and then sync.
    targetTypeahead.getConnectionsStore().saveHWMark(targetConnectionsStoreHWMark);
    targetTypeahead.getConnectionsStore().sync();
    
    System.out.printf("Time: source-load: %d ms; target-load: %d ms; copy: %d ms.%n", sourceLoadTime, targetLoadTime, copyTime);
  }
}
