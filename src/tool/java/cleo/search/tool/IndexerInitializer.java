package cleo.search.tool;

import cleo.search.Element;
import cleo.search.Indexer;

/**
 * IndexerInitializer
 * 
 * @author jwu
 * @since 03/16, 2011
 */
public interface IndexerInitializer<E extends Element> {
  
  /**
   * @return the initialized <code>Indexer</code>.
   */
  public Indexer<E> getIndexer();
  
}
