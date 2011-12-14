package cleo.search.bootstrap;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import krati.util.Chronos;
import cleo.search.ElementFactory;
import cleo.search.TypeaheadElement;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ConnectionsStore;
import cleo.search.tool.GenericTypeaheadInitializer;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.typeahead.GenericTypeaheadConfig;
import cleo.search.typeahead.TypeaheadConfigFactory;
import cleo.search.util.CompositeTermsHandler;
import cleo.search.util.ElementScoreScanner;
import cleo.search.util.ElementScoreSetter;
import cleo.search.util.PropertiesResolver;
import cleo.search.util.TermsDedup;
import cleo.search.util.TermsScanner;

/**
 * GenericTypeaheadBootstrap
 * 
 * @author jwu
 * @since 02/28, 2011
 */
public class GenericTypeaheadBootstrap {

  @SuppressWarnings("unchecked")
  public static void main(String[] args) throws Exception {
    Chronos c = new Chronos();
    
    /**
     * Load bootstrap properties
     */
    String configFile = args[0];
    Properties properties = new Properties();
    FileInputStream inStream = new FileInputStream(configFile);
    InputStreamReader reader = new InputStreamReader(inStream, "UTF-8");
    properties.load(reader);
    PropertiesResolver.resolve(properties);
    properties.list(System.out);
    
    System.out.println("-- starting bootstrap --");
    
    File termsDir = new File(properties.getProperty("cleo.search.generic.typeahead.config.source.terms.dir"));
    File line1Dir = new File(properties.getProperty("cleo.search.generic.typeahead.config.source.line1.dir"));
    File line2Dir = new File(properties.getProperty("cleo.search.generic.typeahead.config.source.line2.dir"));
    File line3Dir = new File(properties.getProperty("cleo.search.generic.typeahead.config.source.line3.dir"));
    File mediaDir = new File(properties.getProperty("cleo.search.generic.typeahead.config.source.media.dir"));
    File scoreDir = new File(properties.getProperty("cleo.search.generic.typeahead.config.source.score.dir"));

    ElementFactory<TypeaheadElement> elementFactory = (ElementFactory<TypeaheadElement>)
      Class.forName(properties.getProperty("cleo.search.generic.typeahead.config.elementFactory.class")).newInstance();
    
    /**
     * Configure GenericTypeahead
     */
    GenericTypeaheadConfig<TypeaheadElement> config = TypeaheadConfigFactory.createGenericTypeaheadConfig(properties);
    System.out.printf("Initialize typeahead %s ...%n", config.getName());
    
    GenericTypeaheadInitializer<TypeaheadElement> initializer = new GenericTypeaheadInitializer<TypeaheadElement>(config);
    GenericTypeahead<TypeaheadElement> typeahead = (GenericTypeahead<TypeaheadElement>) initializer.getTypeahead();
    
    System.out.printf("Initialize typeahead %s: %d ms%n", config.getName(), c.tick());
    
    /**
     * Bootstrap element-store
     */
    ArrayStoreElement<TypeaheadElement> elementStore = typeahead.getElementStore();
    String elementStoreName = config.getElementStoreDir().getName();
    System.out.printf("Bootstrap %s ...%n", elementStoreName);
    
    TypeaheadElementStoreBootstrap elementStoreBootstrap =  new TypeaheadElementStoreBootstrap(elementStore, elementFactory);
    
    elementStoreBootstrap.loadLine1(line1Dir);
    elementStoreBootstrap.loadLine2(line2Dir);
    elementStoreBootstrap.loadLine3(line3Dir);
    elementStoreBootstrap.loadMedia(mediaDir);
    
    TermsScanner scanner = new TermsScanner(termsDir);
    scanner.scan(new CompositeTermsHandler().add(new TermsDedup()).add(elementStoreBootstrap));
    elementStore.sync();
    
    System.out.printf("Bootstrap %s: %d ms%n", elementStoreName, c.tick());
    
    /**
     * Bootstrap connections-store
     */
    ConnectionsStore<String> connectionsStore = typeahead.getConnectionsStore();
    String connectionsStoreName = config.getConnectionsStoreDir().getName();
    System.out.printf("Bootstrap %s ...%n", connectionsStoreName);
    
    // Load element scores
    if(scoreDir != null && scoreDir.exists()) {
      ElementScoreSetter<TypeaheadElement> handler = new ElementScoreSetter<TypeaheadElement>(elementStore);
      ElementScoreScanner scan = new ElementScoreScanner(scoreDir);
      scan.scan(handler);
    }
    
    ConnectionsCollector connectionsCollector = new ConnectionsCollector(config.getConnectionsStoreCapacity());
    connectionsCollector.collect(elementStore, config.getMaxKeyLength());
    connectionsCollector.store(connectionsStore);
    
    System.out.printf("Bootstrap %s: %d ms%n", connectionsStoreName, c.tick());
    
    System.out.println();
    System.out.printf("Total: %d seconds%n", c.getTotalTime()/1000);
  }
}
