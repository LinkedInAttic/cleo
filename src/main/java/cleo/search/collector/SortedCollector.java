package cleo.search.collector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import cleo.search.Element;
import cleo.search.Hit;
import cleo.search.ElementHit;
import cleo.search.network.Proximity;

/**
 * SortedCollector - collect top N elements based on element scores.  
 * 
 * @author jwu
 * @since 02/06, 2011
 * 
 * @param <E> Element to collect.
 */
public class SortedCollector<E extends Element> implements Collector<E> {
  private static final long serialVersionUID = 1L;
  
  protected final int capacity;
  protected final int stopSize;
  protected final SortedSet<Hit<E>> sortedSet;
  protected int stopCounter = 0;
  
  /**
   * Create an unlimited SortedCollector.
   */
  public SortedCollector() {
    this.capacity = Integer.MAX_VALUE;
    this.stopSize = Integer.MAX_VALUE;
    this.sortedSet = new TreeSet<Hit<E>>(new HitCmpDsc<E>());
  }
  
  /**
   * Create a limited SortedCollector with a non-stop size.
   *  
   * @param capacity - the capacity of collector (e.g. 10), no smaller than 1.
   */
  public SortedCollector(int capacity) {
    this.capacity = Math.max(1, capacity);
    this.stopSize = Integer.MAX_VALUE;
    this.sortedSet = new TreeSet<Hit<E>>(new HitCmpDsc<E>());
  }
  
  /**
   * Create a limited SortedCollector.
   *  
   * @param capacity - the capacity of collector (e.g. 10), no smaller than 1.
   * @param stopSize - the stop size of collector (e.g. 1000), no smaller than capacity.
   *                   Above the stop size the collector can stop collecting new elements
   *                   if its capacity is filled up.
   */
  public SortedCollector(int capacity, int stopSize) {
    this.capacity = Math.max(1, capacity);
    this.stopSize = Math.max(capacity, stopSize);
    this.sortedSet = new TreeSet<Hit<E>>(new HitCmpDsc<E>());
  }
  
  /**
   * Create a limited SortedCollector.
   *  
   * @param capacity - the capacity of collector (e.g. 10), no smaller than 1.
   * @param stopSize - the stop size of collector (e.g. 1000), no smaller than capacity.
   *                   Above the stop size the collector can stop collecting new elements
   *                   if its capacity is filled up.
   * @param comparator - the comparator for sorting element hits.
   */
  protected SortedCollector(int capacity, int stopSize, Comparator<Hit<E>> comparator) {
    this.capacity = Math.max(1, capacity);
    this.stopSize = Math.max(capacity, stopSize);
    this.sortedSet = new TreeSet<Hit<E>>(comparator);
  }
  
  @Override
  public boolean add(E element, double score, String source) {
    stopCounter++;
    
    if(sortedSet.size() >= capacity) {
      if(sortedSet.last().getScore() < score) {
        Hit<E> hit = sortedSet.last();
        sortedSet.remove(hit);
        
        hit.clear();
        hit.setScore(score);
        hit.setSource(source);
        hit.setElement(element);
        return sortedSet.add(hit);
      } else {
        return false;
      }
    } else {
      return sortedSet.add(new ElementHit<E>(element, score, source));
    }
  }
  
  @Override
  public boolean add(E element, double score, String source, Proximity proximity) {
    stopCounter++;
    
    if(sortedSet.size() >= capacity) {
      if(sortedSet.last().getScore() < score) {
        Hit<E> hit = sortedSet.last();
        sortedSet.remove(hit);
        
        hit.clear();
        hit.setScore(score);
        hit.setSource(source);
        hit.setElement(element);
        hit.setProximity(proximity);
        return sortedSet.add(hit);
      } else {
        return false;
      }
    } else {
      return sortedSet.add(new ElementHit<E>(element, score, source, proximity));
    }
  }
  
  @Override
  public boolean add(Hit<E> hit) {
    stopCounter++;
    
    if(sortedSet.size() >= capacity) {
      if(sortedSet.last().getScore() < hit.getScore()) {
        sortedSet.remove(sortedSet.last());
        return sortedSet.add(hit);
      } else {
        return false;
      }
    } else {
      return sortedSet.add(hit);
    }
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
    return capacity;
  }
  
  @Override
  public int stopSize() {
    return stopSize;
  }
  
  @Override
  public void clear() {
    stopCounter = 0;
    sortedSet.clear();
  }
  
  @Override
  public List<E> elements() {
    List<E> results = new ArrayList<E>(size());
    for(Hit<E> s : sortedSet) {
      results.add(s.getElement());
    }
    return results;
  }
  
  @Override
  public List<Hit<E>> hits() {
    List<Hit<E>> results = new ArrayList<Hit<E>>(size());
    results.addAll(sortedSet);
    return results;
  }
  
  @Override
  public boolean isEmpty() {
    return sortedSet.isEmpty();
  }
  
  @Override
  public boolean canStop() {
    return stopCounter >= stopSize && sortedSet.size() >= capacity;
  }
  
  @Override
  public int size() {
    return sortedSet.size();
  }
  
  static final class HitCmpDsc<E extends Element> implements Comparator<Hit<E>>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Hit<E> h1, Hit<E> h2) {
      // Use descending order
      return h2.compareTo(h1);
    }
  }
  
  @Override
  public Collector<E> newInstance() {
    return new SortedCollector<E>(capacity, stopSize);
  }
}
