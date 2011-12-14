package cleo.search.util;

import java.util.Iterator;

/**
 * WeightIterator
 * 
 * @author jwu
 * @since 04/30, 2011
 */
public interface WeightIterator extends Iterator<Weight> {
  
  /**
   * Get the next weight and fill in the user supplied weight object.
   * 
   * @param weight
   */
  public void next(Weight weight);
}
