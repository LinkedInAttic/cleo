package cleo.search;

/**
 * Score
 * 
 * @author jwu
 * @since 02/11, 2011
 * 
 * <p>
 * 05/18, 2011 - Added MIN_SCORE_FLOAT and MIN_SCORE_DOUBLE <br/>
 */
public interface Score {
  public float  MIN_SCORE_FLOAT  = 0.0000001F;
  public double MIN_SCORE_DOUBLE = 0.0000001D;
  
  /**
   * Gets the score.
   * 
   * @return score
   */
  public double getScore();
  
  /**
   * Sets the score.
   * 
   * @param score
   */
  public void setScore(double score);
  
}
