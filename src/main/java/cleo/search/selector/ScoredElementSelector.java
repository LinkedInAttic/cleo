package cleo.search.selector;

import cleo.search.Element;

/**
 * ScoredElementSelector
 * 
 * @author jwu
 * @since 05/04, 2011
 */
public class ScoredElementSelector<E extends Element> extends ScoredPrefixSelector<E> {
  private static final long serialVersionUID = 1L;
  
  public ScoredElementSelector(String... queryTerms) {
    super(queryTerms);
  }
  
  @Override
  public boolean select(E element, SelectorContext ctx) {
    boolean b = super.select(element, ctx);
    if(b) {
      ctx.setScore(ctx.getScore() * element.getScore());
    }
    return b;
  }
}
