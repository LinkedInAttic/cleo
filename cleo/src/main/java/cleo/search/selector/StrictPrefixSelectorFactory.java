package cleo.search.selector;

import cleo.search.Element;

/**
 * StrictPrefixSelectorFactory
 * 
 * @author jwu
 * @since 02/16, 2012
 */
public class StrictPrefixSelectorFactory<E extends Element> implements SelectorFactory<E> {
  private static final long serialVersionUID = 1L;
  
  @Override
  public Selector<E> createSelector(String... terms) {
    return new StrictPrefixSelector<E>(terms);
  }
}
