package cleo.search.util;

/**
 * ConnectionStrengthAdjuster
 * 
 * @author jwu
 * @since 04/19, 2011
 */
public class ConnectionStrengthAdjuster implements WeightAdjuster {
  private int base = 1;
  
  @Override
  public int adjust(int weight1, int weight2) {
    return (int)(weight1 * (weight2 / (weight1 + weight2 + (float)base)));
  }
  
  @Override
  public float adjust(float weight1, float weight2) {
    return (weight1 * (weight2 / (weight1 + weight2 + base)));
  }
  
  @Override
  public double adjust(double weight1, double weight2) {
    return (weight1 * (weight2 / (weight1 + weight2 + base)));
  }
  
  public final void setBase(int base) {
    this.base = Math.max(1, base);
  }
  
  public final int getBase() {
    return base;
  }
}
