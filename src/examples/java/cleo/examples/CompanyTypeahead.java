package cleo.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cleo.search.Hit;
import cleo.search.Indexer;
import cleo.search.MultiIndexer;
import cleo.search.SimpleTypeaheadElement;
import cleo.search.TypeaheadElement;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;
import cleo.search.selector.ScoredElementSelectorFactory;
import cleo.search.tool.GenericTypeaheadInitializer;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.typeahead.GenericTypeaheadConfig;
import cleo.search.typeahead.MultiTypeahead;
import cleo.search.typeahead.Typeahead;
import cleo.search.typeahead.TypeaheadConfigFactory;

/**
 * CompanyTypeahead
 * 
 * @author jwu
 * @since 09/20, 2011
 */
public class CompanyTypeahead {
  
  /**
   * Creates a new GenericTypeahead based on its configuration file.
   * 
   * @param configFile
   * @throws Exception
   */
  public static GenericTypeahead<TypeaheadElement> createTypeahead(File configFile) throws Exception {
    // Create typeahead config
    GenericTypeaheadConfig<TypeaheadElement> config =
      TypeaheadConfigFactory.createGenericTypeaheadConfig(configFile);
    config.setSelectorFactory(new ScoredElementSelectorFactory<TypeaheadElement>());
    
    // Create typeahead initializer
    GenericTypeaheadInitializer<TypeaheadElement> initializer =
      new GenericTypeaheadInitializer<TypeaheadElement>(config);
    
    return (GenericTypeahead<TypeaheadElement>)initializer.getTypeahead();
  }
  
  /**
   * Creates a new TypeaheadElement.
   * 
   * @param elementId - the element Id
   * @param terms     - the index terms
   * @param line1     - the display line1 (e.g. title)
   * @param line2     - the display line2 (e.g. description)
   * @param media     - the media URL
   * @param score     - the ranking score
   * @return a new TypeaheadElement
   */
  public static TypeaheadElement createElement(int elementId, String[] terms, String line1, String line2, String media, float score) {
    TypeaheadElement elem = new SimpleTypeaheadElement(elementId);
    elem.setTerms(terms);
    elem.setLine1(line1);
    elem.setLine2(line2);
    elem.setMedia(media);
    elem.setScore(score);
    elem.setTimestamp(System.currentTimeMillis());
    return elem;
  }
  
  /**
   * Index a number of elements that represent companies.
   * 
   * @param indexer - the element indexer
   * @throws Exception
   */
  public static void indexElements(Indexer<TypeaheadElement> indexer) throws Exception {
    // Add elements to the index 
    indexer.index(createElement(
        1307,
        new String[] {"fidelity", "investments"},
        "Fidelity Investments", "Financial Services", "/media/a101af", 0.27f));
    indexer.index(createElement(
        1337,
        new String[] {"linkedin", "lnkd"},
        "LinkedIn", "Professional Social Network", "/media/a105cd", 0.62f));
    indexer.index(createElement(
        12653,
        new String[] {"linden", "lab"},
        "Linden Lab", "Computer Software", "/media/fbb123", 0.12f));
    indexer.index(createElement(
        10667,
        new String[] {"facebook"},
        "Facebook", "The Largest Social Network", "/media/0235de", 0.71f));
    indexer.index(createElement(
        108137,
        new String[] {"lab126", "126"},
        "Lab126", null, "/media/0de5d1", 0.021f));
    indexer.index(createElement(
        1432416,
        new String[] {"funny"},
        "Funny", null, "/media/0235de", 0.001f));
    
    indexer.flush();
  }
  
  /**
   * <pre>
   * JVM Arguments
   *   -server -Xms4g -Xmx4g
   *   
   * Program Arguments:
   *   src/examples/resources/company-config/i001.config src/examples/resources/company-config/i002.config
   * </pre>
   */
  public static void main(String[] args) throws Exception {
    List<Indexer<TypeaheadElement>> indexerList = new ArrayList<Indexer<TypeaheadElement>>();
    List<Typeahead<TypeaheadElement>> searcherList = new ArrayList<Typeahead<TypeaheadElement>>();
    
    // Create indexer and searcher
    for(String filePath : args) {
      File configFile = new File(filePath);
      GenericTypeahead<TypeaheadElement> gta = createTypeahead(configFile);
      indexerList.add(gta);
      searcherList.add(gta);
    }
    
    Indexer<TypeaheadElement> indexer = new MultiIndexer<TypeaheadElement>("Company", indexerList);
    Typeahead<TypeaheadElement> searcher = new MultiTypeahead<TypeaheadElement>("Company", searcherList);
    
    // Populate typeahead indexes
    indexElements(indexer);
    
    // Perform typeahead searches
    Collector<TypeaheadElement> collector;
    
    System.out.println("----- query=l");
    collector = new SortedCollector<TypeaheadElement>(10, 100);
    collector = searcher.search(0, new String[] {"l"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- query=lin");
    collector = new SortedCollector<TypeaheadElement>(10, 100);
    collector = searcher.search(0, new String[] {"lin"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- query=link");
    collector = new SortedCollector<TypeaheadElement>(10, 100);
    collector = searcher.search(0, new String[] {"link"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- query=f");
    collector = new SortedCollector<TypeaheadElement>(10, 100);
    collector = searcher.search(0, new String[] {"f"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- query=fu");
    collector = new SortedCollector<TypeaheadElement>(10, 100);
    collector = searcher.search(0, new String[] {"fu"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- query=lab");
    collector = new SortedCollector<TypeaheadElement>(10, 100);
    collector = searcher.search(0, new String[] {"lab"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- query=lab1");
    collector = new SortedCollector<TypeaheadElement>(10, 100);
    collector = searcher.search(0, new String[] {"lab1"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- query=investment fi");
    collector = new SortedCollector<TypeaheadElement>(10, 100);
    collector = searcher.search(0, new String[] {"investment", "fi"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
  }
}
