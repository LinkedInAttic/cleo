package cleo.search.store;

/**
 * Stores - Store static utilities. 
 * 
 * @author jwu
 * @since 05/16, 2011
 * 
 * <p>
 * 05/16, 2011 - Added max/min utility methods for Int/Float/DoubleArrayPartition <br/>
 */
public class Stores {
  
  /**
   * Finds the maximum integer value from an integer array partition.
   * 
   * @param p - An integer array partition.
   * @return the maximum integer value.
   */
  public static int max(IntArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    int max = Integer.MIN_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      int val = p.get(i);
      if(val > max) {
        max = val; 
      }
    }
    
    return max;
  }
  
  /**
   * Finds the minimum integer value from an integer array partition.
   * 
   * @param p - An integer array partition.
   * @return the minimum integer value.
   */
  public static int min(IntArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    int min = Integer.MAX_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      int val = p.get(i);
      if(val < min) {
        min = val; 
      }
    }
    
    return min;
  }
  
  /**
   * Finds the maximum float value from an float array partition.
   * 
   * @param p - A float array partition.
   * @return the maximum float value.
   */
  public static float max(FloatArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    float max = Float.MIN_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      float val = p.get(i);
      if(val > max) {
        max = val; 
      }
    }
    
    return max;
  }
  
  /**
   * Finds the minimum float value from an float array partition.
   * 
   * @param p - A float array partition.
   * @return the minimum float value.
   */
  public static float min(FloatArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    float min = Float.MAX_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      float val = p.get(i);
      if(val < min) {
        min = val; 
      }
    }
    
    return min;
  }
  
  /**
   * Finds the maximum double value from an double array partition.
   * 
   * @param p - A double array partition.
   * @return the maximum double value.
   */
  public static double max(DoubleArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    double max = Double.MIN_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      double val = p.get(i);
      if(val > max) {
        max = val; 
      }
    }
    
    return max;
  }
  
  /**
   * Finds the minimum double value from an double array partition.
   * 
   * @param p - A double array partition.
   * @return the minimum double value.
   */
  public static double min(DoubleArrayPartition p) {
    if(p == null || p.capacity() == 0) {
      return 0;
    }
    
    double min = Double.MAX_VALUE;
    
    for(int i = p.getIndexStart(), end = p.getIndexEnd(); i < end; i++) {
      double val = p.get(i);
      if(val < min) {
        min = val; 
      }
    }
    
    return min;
  }
}
