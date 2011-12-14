package cleo.search.util;

import java.io.Serializable;

/**
 * Weight
 * 
 * @author jwu
 * @since 04/26, 2011
 */
public final class Weight implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final int ELEMENT_ID_NUM_BYTES = 4;     // Number of integer bytes
  public static final int ELEMENT_WEIGHT_NUM_BYTES = 4; // Number of integer bytes
  
  public int elementId;
  public int elementWeight;
  
  public Weight(int elementId, int elementWeight) {
    this.elementId = elementId;
    this.elementWeight = elementWeight;
  }
  
  @Override
  public boolean equals(Object o) {
    if(o == this) return true;
    if(o == null) return false;
    return o.getClass() == Weight.class ? (elementId == ((Weight)o).elementId) : false;
  }
  
  @Override
  public int hashCode() {
    return elementId;
  }
  
  @Override
  public String toString() {
    return elementId + ":" + elementWeight;
  }
}
