package cleo.search.typeahead;

import cleo.search.Element;
import cleo.search.Searcher;

/**
 * Typeahead
 * 
 * @author jwu
 * @since 02/08, 2011
 */
public interface Typeahead<E extends Element> extends Searcher<E> {
  
  /**
   * Gets the name of Typeahead.
   */
  public String getName();
}
