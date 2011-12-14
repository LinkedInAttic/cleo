package cleo.search.util;

/**
 * IntIterator
 * 
 * @author jwu
 * @since 05/06, 2011
 */
public interface IntIterator {
  
  /**
   * @return <code>true</code> if there is a next integer. Otherwise, <code>false</code>.
   */
  public boolean hasNext();
  
  /**
   * @return the next integer.
   */
  public int next();
}
