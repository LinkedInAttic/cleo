package cleo.search.selector;

import java.io.Serializable;

import cleo.search.Score;

/**
 * SelectorContext
 * 
 * @author jwu
 * @since 02/11, 2011
 */
public class SelectorContext implements Score, Serializable {
  private static final long serialVersionUID = 1L;
  
  /**
   * Element score.
   */
  private double score;
  
  @Override
  public final double getScore() {
    return score;
  }
  
  @Override
  public final void setScore(double score) {
    this.score = score;
  }
  
  public void clear() {
    score = 0;
  }
}
