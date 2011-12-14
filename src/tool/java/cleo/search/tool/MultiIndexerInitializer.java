package cleo.search.tool;

import java.util.ArrayList;
import java.util.List;

import cleo.search.Element;
import cleo.search.Indexer;
import cleo.search.MultiIndexer;

/**
 * MultiIndexerInitializer
 * 
 * @author jwu
 * @since 03/16, 2011
 */
public class MultiIndexerInitializer<E extends Element> implements IndexerInitializer<E> {
  protected final String name;
  protected final MultiIndexer<E> multiIndexer;
  
  public MultiIndexerInitializer(String name, List<IndexerInitializer<E>> subInitializers) {
    List<Indexer<E>> indexerList = new ArrayList<Indexer<E>>(); 
    for(IndexerInitializer<E> i : subInitializers) {
      if(i != null) {
        Indexer<E> indexer = i.getIndexer();
        if(indexer != null) indexerList.add(indexer);
      }
    }
    
    this.name = name;
    this.multiIndexer = new MultiIndexer<E>(name, indexerList);
  }
  
  public final String getName() {
    return name;
  }
  
  @Override
  public final Indexer<E> getIndexer() {
    return  multiIndexer;
  }
}
