package cleo.search;

import java.util.Collection;

/**
 * IndexRoller - Update indexes by rolling a batch of elements all together.
 * 
 * @author jwu
 * @since 03/02, 2011
 */
public interface IndexRoller<E extends Element> {
  
  /**
   * Update the indexes by rolling a batch of elements all together.
   * 
   * @param elements
   * @return <code>true</code> if the batch of elements are indexed successfully. Otherwise, <code>false</code>.
   */
  public boolean roll(Collection<E> elements);
  
}
