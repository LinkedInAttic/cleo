package cleo.search.selector;

import cleo.search.Element;

/**
 * ScoredElementSelectorFactory
 * 
 * @author jwu
 * @since 05/04, 2011
 * 
 * <p>
 * 05/19, 2011 - Added default serialVersionUID <br/>
 */
public class ScoredElementSelectorFactory<E extends Element> implements SelectorFactory<E> {
  private static final long serialVersionUID = 1L;
  
  @Override
  public Selector<E> createSelector(String... terms) {
    return new ScoredElementSelector<E>(terms);
  }
}
