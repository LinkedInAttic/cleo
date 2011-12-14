package cleo.search.collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cleo.search.Element;
import cleo.search.Hit;
import cleo.search.network.Proximity;

/**
 * MultiSourceCollector
 * 
 * @author jwu
 * @since 02/10, 2011
 * 
 * @param <E> Element
 */
public class MultiSourceCollector<E extends Element> implements MultiCollector<E> {
  private static final long serialVersionUID = 1L;
  
  private Map<String, Collector<E>> map;
  
  public MultiSourceCollector() {
    map = new HashMap<String, Collector<E>>();
  }
  
  @Override
  public Collection<String> sources() {
    return map.keySet();
  }
  
  @Override
  public Collection<Collector<E>> collectors() {
    return map.values();
  }
  
  @Override
  public Collector<E> getCollector(String source) {
    return map.get(source);
  }

  @Override
  public boolean putCollector(String source, Collector<E> collector) {
    if(source == null || collector == null || collector == this) {
      return false;
    }
    
    map.put(source, collector);
    return true;
  }
  
  @Override
  public boolean add(E element, double score, String source) {
    Collector<E> c = getCollector(source); 
    return (c == null) ? false : c.add(element, score, source);
  }
  
  @Override
  public boolean add(E element, double score, String source, Proximity proximity) {
    Collector<E> c = getCollector(source); 
    return (c == null) ? false : c.add(element, score, source, proximity);
  }
  
  public boolean add(Hit<E> hit) {
    String source = hit.getSource();
    if(source == null) return false;
    
    Collector<E> c = getCollector(source); 
    return (c == null) ? false : c.add(hit);
  }
  
  @Override
  public boolean add(Collector<E> collector) {
    boolean b = false; 
    for(Hit<E> h : collector.hits()) {
      if(add(h)) {
        b = true;
      }
    }
    return b;
  }
  
  @Override
  public int capacity() {
    int capacity = 0;
    for(Collector<E> c : collectors()) {
      capacity += c.capacity();
      if(capacity == Integer.MAX_VALUE) break;
    }
    return capacity;
  }
  
  @Override
  public void clear() {
    map.clear();
  }
  
  @Override
  public List<Hit<E>> hits() {
    List<Hit<E>> results = new ArrayList<Hit<E>>(size());
    for(Collector<E> c : collectors()) {
      results.addAll(c.hits());
    }
    
    Collections.sort(results, new HitCmpDsc<E>());
    return results;
  }
  
  @Override
  public List<E> elements() {
    List<Hit<E>> hits = hits();
    List<E> results = new ArrayList<E>(hits.size());
    for(Hit<E> h : hits) {
      results.add(h.getElement());
    }
    return results;
  }
  
  @Override
  public boolean isEmpty() {
    for(Collector<E> c : collectors()) {
      if(!c.isEmpty()) return false;
    }
    return true;
  }
  
  @Override
  public Collector<E> newInstance() {
    MultiSourceCollector<E> result = new MultiSourceCollector<E>();
    for(String source : map.keySet()) {
      Collector<E> c = getCollector(source);
      if(c != null) {
        result.putCollector(source, c.newInstance());
      }
    }
    
    return result;
  }
  
  @Override
  public int size() {
    int size = 0;
    for(Collector<E> c : collectors()) {
      size += c.size();
    }
    return size;
  }
  
  @Override
  public int stopSize() {
    return Integer.MAX_VALUE;
  }
  
  @Override
  public boolean canStop() {
    return false;
  }
  
  static final class HitCmpDsc<E extends Element> implements Comparator<Hit<E>>, Serializable {
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(Hit<E> h1, Hit<E> h2) {
      return h2.compareTo(h1);
    }
  }
}
