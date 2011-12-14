package cleo.search;

import java.io.Serializable;

import cleo.search.network.Proximity;

/**
 * Hit
 * 
 * @author jwu
 * @since 02/11, 2011
 * 
 * @param <E> Element
 */
public interface Hit<E extends Element> extends Score, Serializable, Comparable<Hit<? super E>> {
  
  public void clear();

  public String getSource();
  
  public void setSource(String source);
  
  public E getElement();
  
  public void setElement(E element);
  
  public Proximity getProximity();
  
  public void setProximity(Proximity proximity); 
}
