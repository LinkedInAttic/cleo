package cleo.search.selector;

import java.io.Serializable;

import cleo.search.Element;

/**
 * SelectorFactory
 * 
 * @author jwu
 * @since 01/12, 2011
 */
public interface SelectorFactory<E extends Element> extends Serializable {
  
  public Selector<E> createSelector(String... terms);
  
}
