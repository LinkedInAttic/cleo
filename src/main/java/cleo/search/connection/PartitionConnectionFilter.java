package cleo.search.connection;

import cleo.search.util.Range;

/**
 * PartitionConnectionFilter
 * 
 * @author jwu
 * @since 04/22, 2011
 */
public interface PartitionConnectionFilter extends ConnectionFilter {
  
  /**
   * @return the partition range.
   */
  public Range getPartitionRange();
}
