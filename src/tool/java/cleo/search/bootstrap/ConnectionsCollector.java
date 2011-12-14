package cleo.search.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import cleo.search.Element;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.ConnectionsStore;

/**
 * ConnectionsCollector
 * 
 * @author jwu
 * @since 02/06, 2011
 */
public class ConnectionsCollector {
  private HashMap<String, ArrayList<Element>> map;
  
  public ConnectionsCollector(int initialCapacity) {
    map = new HashMap<String, ArrayList<Element>>(initialCapacity);
  }
  
  public synchronized void clear() {
    map.clear();
  }
  
  public synchronized <E extends Element> void collect(ArrayStoreElement<E> elementStore, int maxKeyLength) {
    int index;
    int indexStart = elementStore.getIndexStart();
    
    for(int i = 0, cnt = elementStore.capacity(); i < cnt; i++) {
      index = indexStart + i;
      E element = elementStore.getElement(index);
      if(element != null) {
        for(String term : element.getTerms()) {
          int len = Math.min(term.length(), maxKeyLength);
          for(int k = 1; k <= len; k++) {
            addConnection(term.substring(0, k), element);
          }
        }
      }
    }
  }
  
  public synchronized void store(ConnectionsStore<String> connectionsStore) throws Exception {
    int keyCnt=0;
    int maxCnt=0;
    int minCnt=Integer.MAX_VALUE;
    
    ElementScoreCmp cmpDesc = new ElementScoreCmp();
    
    for(String key : map.keySet()) {
      ArrayList<Element> list = map.get(key);
      Collections.sort(list, cmpDesc);
      int[] connections = new int[list.size()];
      for(int i = 0, cnt = connections.length; i < cnt; i++) {
        connections[i] = list.get(i).getElementId();
      }
      connectionsStore.putConnections(key, connections, System.currentTimeMillis());
      
      maxCnt = Math.max(maxCnt, connections.length);
      minCnt = Math.min(minCnt, connections.length);
      keyCnt++;
    }
    
    connectionsStore.sync();
    
    System.out.printf("#keys=%d connectionsMaxCnt=%d connectionsMinCnt=%d%n", keyCnt, maxCnt, minCnt);
  }
  
  private void addConnection(String source, Element connection) {
    ArrayList<Element> list = map.get(source);
    if(list == null) {
      list = new ArrayList<Element>(500);
      map.put(source, list);
    }
    list.add(connection);
  }
  
  static class ElementScoreCmp implements Comparator<Element> {
    @Override
    public int compare(Element e0, Element e1) {
      float score0 = e0.getScore();
      float score1 = e1.getScore();
      
      // Descending order
      return score0 < score1 ? 1 : (score0 == score1 ? (e0.getElementId() - e1.getElementId()) : -1);
    }
  }
}
