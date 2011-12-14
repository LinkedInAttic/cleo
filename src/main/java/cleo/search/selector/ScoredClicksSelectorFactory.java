package cleo.search.selector;

import cleo.search.Element;

/**
 * ScoredClicksSelectorFactory
 * 
 * @author jwu
 * @since 02/18, 2011
 * 
 * <p>
 * 05/19, 2011 - Added default serialVersionUID <br/>
 */
public class ScoredClicksSelectorFactory<E extends Element> implements SelectorFactory<E> {
  private static final long serialVersionUID = 1L;

  @Override
  public Selector<E> createSelector(String... terms) {
    return new ScoredClicksSelector<E>(terms);
  }
}
