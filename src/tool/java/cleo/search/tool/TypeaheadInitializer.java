package cleo.search.tool;

import cleo.search.Element;
import cleo.search.typeahead.Typeahead;

/**
 * TypeaheadInitializer
 * 
 * @author jwu
 * @since 02/08, 2011
 */
public interface TypeaheadInitializer<E extends Element> {

  /**
   * @return the initialized <code>Typeahead</code>.
   */
  public Typeahead<E> getTypeahead();
  
}
