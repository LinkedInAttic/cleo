package cleo.search;

import java.util.List;

import cleo.search.collector.Collector;

/**
 * Searcher
 * 
 * @author jwu
 * @since 01/12, 2011
 * 
 * @param <E>
 */
public interface Searcher<E extends Element> {
  
  /**
   * Search for elements matching given search terms without timeout.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @return a list of elements matching given search terms.
   */
  public List<E> search(int uid, String[] terms);
  
  /**
   * Search for elements matching given search terms within a user-specified timeout.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @param timeoutMillis - Timeout in milliseconds
   * @return a list of elements matching given search terms.
   */
  public List<E> search(int uid, String[] terms, long timeoutMillis);
  
  /**
   * Search for elements matching given search terms within a user-specified timeout.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @param maxNumResults - Max number of results
   * @param timeoutMillis - Timeout in milliseconds
   * @return a list of elements matching given search terms up to a user-specified number of results.
   */
  public List<E> search(int uid, String[] terms, int maxNumResults, long timeoutMillis);
  
  /**
   * Search for elements matching given search terms using a collector.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @param collector     - Element collector
   * @return the element collector which may be the original collector or a newly created collector. 
   */
  public Collector<E> search(int uid, String[] terms, Collector<E> collector);
  
  /**
   * Search for elements matching given search terms using a collector within a user-specified timeout.
   * 
   * @param uid           - Searcher Id
   * @param terms         - Search terms
   * @param collector     - Element collector
   * @param timeoutMillis - Timeout in milliseconds
   * @return the element collector which may be the original collector or a newly created collector. 
   */
  public Collector<E> search(int uid, String[] terms, Collector<E> collector, long timeoutMillis);
  
}
