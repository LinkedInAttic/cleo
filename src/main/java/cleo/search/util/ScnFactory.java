package cleo.search.util;

/**
 * ScnFactory
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public interface ScnFactory {
  
  /**
   * Get next system change number (SCN)
   * @return the next scn.
   */
  public long next();
}
