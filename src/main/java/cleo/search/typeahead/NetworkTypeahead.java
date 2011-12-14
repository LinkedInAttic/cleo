package cleo.search.typeahead;

import cleo.search.Element;
import cleo.search.collector.Collector;
import cleo.search.util.Range;

/**
 * NetworkTypeahead
 * 
 * @author jwu
 * @since 04/28, 2011
 */
public interface NetworkTypeahead<E extends Element> extends Typeahead<E> {
  
  /**
   * @return the range managed by this NetworkTypeahead.
   */
  public Range getRange();
  
  /**
   * Creates a network typeahead search context for a searcher.
   * 
   * @param uid       - Searcher Id
   * @return the network typeahead search context for a searcher.
   */
  public NetworkTypeaheadContext createContext(int uid);
  
  /**
   * Search for elements matching given search terms using a collector within a specified network typeahead context.
   * The elements to search are from the first and second degree connections of the source in the specified context.
   * 
   * It is not required that the searcher (i.e. <code>uid</code>) is the same as the source in the context.
   * This means that a member (searcher) can search another member's network connections.
   * 
   * @param uid       - Searcher Id
   * @param terms     - Search terms
   * @param collector - Element collector
   * @param context   - Network typeahead context
   *                    which contains a member's first degree connections (or connection strengths).
   *                    The member may or may not be the searcher.
   * @return the element collector which may be the original collector or a newly created collector. 
   */
  public Collector<E> searchNetwork(int uid, String[] terms, Collector<E> collector, NetworkTypeaheadContext context);
}
