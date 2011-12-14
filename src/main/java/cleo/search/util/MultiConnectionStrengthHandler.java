package cleo.search.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MultiConnectionStrengthHandler
 * 
 * @author jwu
 * @since 06/13, 2011
 */
public class MultiConnectionStrengthHandler implements ConnectionStrengthHandler {
  private final List<ConnectionStrengthHandler> handlerList = new ArrayList<ConnectionStrengthHandler>();
  
  public boolean add(ConnectionStrengthHandler h) {
    if(h != null && h != this) {
      handlerList.add(h);
      return true;
    }
    
    return false;
  }
  
  public int size() {
    return handlerList.size();
  }
  
  public void clear() {
    handlerList.clear();
  }
  
  public boolean isEmpty() {
    return handlerList.isEmpty();
  }
  
  public Iterator<ConnectionStrengthHandler> iterator() {
    return handlerList.iterator();
  }
  
  @Override
  public void handle(int source, int[] connections, int[] strengths) throws Exception {
    for(ConnectionStrengthHandler h : handlerList) {
      h.handle(source, connections, strengths);
    }
  }
}
