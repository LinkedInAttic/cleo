package cleo.search.connection;

import java.io.Serializable;

import cleo.search.util.Range;

/**
 * TargetPartitionConnectionFilter
 * 
 * @author jwu
 * @since 04/22, 2011
 */
public class TargetPartitionConnectionFilter implements PartitionConnectionFilter, Serializable {
  private static final long serialVersionUID = 1L;
  private final Range targetRange;
  
  public TargetPartitionConnectionFilter(Range targetRange) {
    if(targetRange == null) {
      throw new NullPointerException("targetRange is null");
    }
    this.targetRange = targetRange;
  }
  
  @Override
  public Range getPartitionRange() {
    return targetRange;
  }
  
  @Override
  public boolean accept(Connection conn) {
    return conn == null ? false : targetRange.has(conn.target());
  }
  
  @Override
  public boolean accept(int source, int target, boolean active) {
    return targetRange.has(target);
  }
  
  @Override
  public String toString() {
    return getClass().getName() + targetRange;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == this) return true;
    if(obj == null) return false;
    
    if(obj.getClass() == TargetPartitionConnectionFilter.class) {
      return targetRange.equals(((TargetPartitionConnectionFilter)obj).targetRange);
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    return targetRange.hashCode();
  }
}
