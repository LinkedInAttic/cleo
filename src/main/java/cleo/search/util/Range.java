package cleo.search.util;

import java.io.Serializable;

/**
 * Range
 * 
 * @author jwu
 * @since 04/20, 2011
 */
public class Range implements Serializable {
  private final static long serialVersionUID = 1L;
  private final int start;
  private final int count;
  private final int end;
  
  public Range(int start, int count) {
    this.start = start;
    this.count = count;
    this.end = start + count;
  }
  
  public final boolean has(int num) {
    return (start <= num && num < end);
  }
  
  public final int getCount() {
    return count;
  }
  
  public final int getStart() {
    return start;
  }
  
  public final int getEnd() {
    return end;
  }
  
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder().append('[').append(start).append(',').append(end).append(')');
    return b.toString();
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj == null) return false;
    if(obj == this) return true;
    Range range = (Range)obj;
    return (start == range.start) && (start == range.count);
  }
  
  @Override
  public int hashCode() {
    return start + (count % 23); 
  }
}
