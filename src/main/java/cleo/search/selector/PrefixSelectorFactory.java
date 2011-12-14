package cleo.search.selector;

import cleo.search.Element;

/**
 * PrefixSelectorFactory
 * 
 * @author jwu
 * @since 01/18, 2011
 * 
 * <p>
 * 05/19, 2011 - Added default serialVersionUID <br/>
 */
public class PrefixSelectorFactory<E extends Element> implements SelectorFactory<E> {
  private static final long serialVersionUID = 1L;
  
  @Override
  public Selector<E> createSelector(String... terms) {
    return new PrefixSelector<E>(terms);
  }
}
