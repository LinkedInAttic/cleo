package cleo.search.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MultiConnectionsHandler
 * 
 * @author jwu
 * @since 06/13, 2011
 */
public class MultiConnectionsHandler implements ConnectionsHandler {
  private final List<ConnectionsHandler> handlerList = new ArrayList<ConnectionsHandler>();
  
  public boolean add(ConnectionsHandler h) {
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
  
  public Iterator<ConnectionsHandler> iterator() {
    return handlerList.iterator();
  }
  
  @Override
  public int[] handle(int source, int[] connections) throws Exception {
    for(ConnectionsHandler h : handlerList) {
      h.handle(source, connections);
    }
    
    return connections;
  }
}
