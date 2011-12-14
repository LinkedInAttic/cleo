package cleo.search.selector;

import cleo.search.Element;

/**
 * ScoredClicksSelector
 * 
 * @author jwu
 * @since 02/18, 2011
 */
public class ScoredClicksSelector<E extends Element> extends ScoredPrefixSelector<E> {
  private static final long serialVersionUID = 1L;
  
  public ScoredClicksSelector(String... queryTerms) {
    super(queryTerms);
  }
  
  @Override
  public boolean select(E element, SelectorContext ctx) {
    boolean b = super.select(element, ctx);
    if(b) {
      float clicks = element.getScore() + 1;
      ctx.setScore(ctx.getScore() * clicks);
    }
    return b;
  }
}
