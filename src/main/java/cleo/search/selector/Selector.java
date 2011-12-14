package cleo.search.selector;

import java.io.Serializable;

import cleo.search.Element;

/**
 * Selector
 * 
 * @author jwu
 * @since 01/12, 2011
 */
public interface Selector<E extends Element> extends Serializable {
  
  /**
   * Selects an element and calculates its score.
   * 
   * @param element - Element to select.
   * @param ctx     - Selector context which stores the calculated score if element is selected.
   * @return <code>true</code> if the element is selected (i.e. accepted) by this selector. Otherwise, <code>false</code>.
   */
  public boolean select(E element, SelectorContext ctx);
  
}
