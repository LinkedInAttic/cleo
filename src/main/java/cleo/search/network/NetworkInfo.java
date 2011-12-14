package cleo.search.network;

import java.io.Serializable;

/**
 * NetworkInfo
 * 
 * @author jwu
 * @since 04/28, 2011
 */
public class NetworkInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  private int source;
  private int[] targets;
  private int[] strengths;
  private Proximity proximity;
  
  public int getSource() {
    return source;
  }
  
  public int[] getTargets() {
    return targets;
  }
  
  public int[] getStrengths() {
    return strengths;
  }
  
  public Proximity getProximity() {
    return proximity;
  }
  
  public void setSource(int source) {
    this.source = source;
  }
  
  public void setTargets(int[] targets) {
    this.targets = targets;
  }
  
  public void setStrengths(int[] strengths) {
    this.strengths = strengths;
  }
  
  public void setProximity(Proximity proximity) {
    this.proximity = proximity;
  }
}
