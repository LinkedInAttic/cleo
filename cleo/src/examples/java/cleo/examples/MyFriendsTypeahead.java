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

package cleo.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cleo.search.Hit;
import cleo.search.Indexer;
import cleo.search.MultiIndexer;
import cleo.search.SimpleTypeaheadElement;
import cleo.search.TypeaheadElement;
import cleo.search.collector.Collector;
import cleo.search.collector.SimpleCollector;
import cleo.search.connection.ConnectionIndexer;
import cleo.search.connection.MultiConnectionIndexer;
import cleo.search.connection.SimpleConnection;
import cleo.search.selector.ScoredElementSelectorFactory;
import cleo.search.tool.WeightedNetworkTypeaheadInitializer;
import cleo.search.typeahead.MultiTypeahead;
import cleo.search.typeahead.NetworkTypeaheadConfig;
import cleo.search.typeahead.Typeahead;
import cleo.search.typeahead.TypeaheadConfigFactory;
import cleo.search.typeahead.WeightedNetworkTypeahead;

/**
 * MyFriendsTypeahead
 * 
 * @author jwu
 * @since 11/20, 2011
 */
public class MyFriendsTypeahead {

  public static WeightedNetworkTypeahead<TypeaheadElement> createTypeahead(File configFile) throws Exception {
    // Create typeahead config
    NetworkTypeaheadConfig<TypeaheadElement> config =
      TypeaheadConfigFactory.createNetworkTypeaheadConfig(configFile);
    config.setSelectorFactory(new ScoredElementSelectorFactory<TypeaheadElement>());
    
    // Create typeahead initializer
    WeightedNetworkTypeaheadInitializer<TypeaheadElement> initializer = 
      new WeightedNetworkTypeaheadInitializer<TypeaheadElement>(config);
    
    return (WeightedNetworkTypeahead<TypeaheadElement>)initializer.getTypeahead();
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
   * Indexes a number of typeahead elements that represent people.
   * 
   * @param elemIndexer - the element indexer
   * @throws Exception
   */
  public static void indexElements(Indexer<TypeaheadElement> elemIndexer) throws Exception {
    Random rand = new Random();
    elemIndexer.index(createElement(5, new String[]{"jay", "kaspers"}, "J Kaspers", "Senior Software Engineer", "/photos/00000005.png", rand.nextFloat()));
    elemIndexer.index(createElement(29, new String[]{"peter", "smith"}, "Peter Smith", "Product Manager", "/photos/00000029.png", rand.nextFloat()));
    elemIndexer.index(createElement(167, new String[]{"steve", "jobs"}, "Steve Jobs", "Apple CEO", "/photos/00000167.png", rand.nextFloat()));
    elemIndexer.index(createElement(1007, new String[]{"ken", "miller"}, "Ken Miller", "Micro Blogging", "/photos/00001007.png", rand.nextFloat()));
    elemIndexer.index(createElement(2007, new String[]{"kay", "moore"}, "Kay Moore", "", "/photos/00002007.png", rand.nextFloat()));
    elemIndexer.index(createElement(180208, new String[]{"snow", "white"}, "Snow White", "Princess", "/photos/00180208.png", rand.nextFloat()));
    elemIndexer.index(createElement(119205, new String[]{"richard", "jackson"}, "Richard Jackson", "Engineering Director", "/photos/00119205.png", rand.nextFloat()));
    elemIndexer.flush();
  }
  
  /**
   * Indexes a number of connections in the form of friendship.
   * 
   * @param connIndexer - the connection indexer
   * @throws Exception
   */
  public static void indexConnections(ConnectionIndexer connIndexer) throws Exception {
    connIndexer.index(new SimpleConnection(5, 1007, true));
    connIndexer.index(new SimpleConnection(5, 2007, true));
    connIndexer.index(new SimpleConnection(5, 780208, true));
    connIndexer.index(new SimpleConnection(167, 180208, true));
    connIndexer.index(new SimpleConnection(167, 119205, true));
    connIndexer.index(new SimpleConnection(167, 29, true));

    connIndexer.index(new SimpleConnection(1, 5, true));
    connIndexer.index(new SimpleConnection(1, 167, true));
    
    connIndexer.flush();
  }
  
  /**
   * <pre>
   * JVM Arguments
   *   -server -Xms4g -Xmx4g
   * 
   * Program Arguments:
   *   src/examples/resources/network-config/i001.config src/examples/resources/network-config/i002.config
   * </pre>
   */
  public static void main(String[] args) throws Exception {
    List<ConnectionIndexer> connIndexerList = new ArrayList<ConnectionIndexer>();
    List<Indexer<TypeaheadElement>> elemIndexerList = new ArrayList<Indexer<TypeaheadElement>>();
    List<Typeahead<TypeaheadElement>> searcherList = new ArrayList<Typeahead<TypeaheadElement>>();
    
    // Create indexer and searcher
    for(String filePath : args) {
      File configFile = new File(filePath);
      WeightedNetworkTypeahead<TypeaheadElement> nta = createTypeahead(configFile);
      connIndexerList.add(nta);
      elemIndexerList.add(nta);
      searcherList.add(nta);
    }
    
    ConnectionIndexer connIndexer = new MultiConnectionIndexer("Friends", connIndexerList);
    Indexer<TypeaheadElement> elemIndexer = new MultiIndexer<TypeaheadElement>("Friends", elemIndexerList);
    Typeahead<TypeaheadElement> searcher = new MultiTypeahead<TypeaheadElement>("Friends", searcherList);
    
    // Populate typeahead indexes
    indexElements(elemIndexer);
    indexConnections(connIndexer);

    // Perform typeahead searches
    Collector<TypeaheadElement> collector;
    
    System.out.println("----- id=5 query=k m");
    collector = new SimpleCollector<TypeaheadElement>();
    collector = searcher.search(5, new String[]{"k", "m"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- id=5 query=k mil");
    collector = new SimpleCollector<TypeaheadElement>();
    collector = searcher.search(5, new String[]{"k", "mil"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- id=167 query=s");
    collector = new SimpleCollector<TypeaheadElement>();
    collector = searcher.search(167, new String[]{"s"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- id=167 query=snow wh");
    collector = new SimpleCollector<TypeaheadElement>();
    collector = searcher.search(167, new String[]{"snow", "wh"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
    
    System.out.println("----- id=1 query=j");
    collector = new SimpleCollector<TypeaheadElement>();
    collector = searcher.search(1, new String[]{"j"}, collector);
    for(Hit<TypeaheadElement> h : collector.hits()) {
      System.out.println(h);
    }
  }
}
