package cleo.search.util;

/**
 * ScoreHandler
 * 
 * @author jwu
 * @since 02/17, 2011
 */
public interface ScoreHandler {

  public double handle(int elementId, double elementScore);
  
}
