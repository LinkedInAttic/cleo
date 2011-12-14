package cleo.search.typeahead;

import cleo.search.Element;
import cleo.search.util.Range;

/**
 * RangeTypeahead
 * 
 * @author jwu
 * @since 03/23, 2011
 */
public interface RangeTypeahead<E extends Element> extends Typeahead<E> {
  
  /**
   * @return the range of this RangeTypeahead.
   */
  public Range getRange();
  
  /**
   * @return the start of this RangeTypeahead.
   */
  public int getRangeStart();
  
  /**
   * @return the end of this RangeTypeahead.
   */
  public int getRangeEnd();
  
}
