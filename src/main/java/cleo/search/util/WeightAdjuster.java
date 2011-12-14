package cleo.search.util;

/**
 * WeightAdjuster
 * 
 * @author jwu
 * @since 04/19, 2011
 */
public interface WeightAdjuster {
  
  public int adjust(int weight1, int weight2);

  public float adjust(float weight1, float weight2);
  
  public double adjust(double weight1, double weight2);
}
