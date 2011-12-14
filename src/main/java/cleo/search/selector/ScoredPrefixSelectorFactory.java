package cleo.search.selector;

import cleo.search.Element;

/**
 * ScoredPrefixSelectorFactory
 * 
 * @author jwu
 * @since 02/12, 2011
 * 
 * 05/19, 2011 - Added default serialVersionUID <br/>
 */
public class ScoredPrefixSelectorFactory<E extends Element> implements SelectorFactory<E> {
  private static final long serialVersionUID = 1L;
  
  @Override
  public Selector<E> createSelector(String... terms) {
    return new ScoredPrefixSelector<E>(terms);
  }
}
