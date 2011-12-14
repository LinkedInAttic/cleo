package cleo.search.collector;

import java.io.Serializable;
import java.util.Comparator;

import cleo.search.Element;
import cleo.search.ElementHit;
import cleo.search.Hit;
import cleo.search.network.Proximity;

/**
 * NetworkSortedCollector. Network degree has higher priority than hit score. 
 * 
 * @author jwu
 * @since 07/26, 2011
 * 
 * @param <E> Element
 */
public class NetworkSortedCollector<E extends Element> extends SortedCollector<E> {
  /**
   * Default serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  
  /**
   * Create an unlimited NetworkSortedCollector.
   */
  public NetworkSortedCollector() {
    super(Integer.MAX_VALUE, Integer.MAX_VALUE, new NetworkHitCmpDsc<E>());
  }
  
  /**
   * Create a limited NetowrkSortedCollector with a non-stop size.
   *  
   * @param capacity - the capacity of collector (e.g. 10), no smaller than 1.
   */
  public NetworkSortedCollector(int capacity) {
    super(capacity, Integer.MAX_VALUE, new NetworkHitCmpDsc<E>());
  }
  
  /**
   * Create a limited NetworkSortedCollector.
   *  
   * @param capacity - the capacity of collector (e.g. 10), no smaller than 1.
   * @param stopSize - the stop size of collector (e.g. 1000), no smaller than capacity.
   *                   Above the stop size the collector can stop collecting new elements
   *                   if its capacity is filled up.
   */
  public NetworkSortedCollector(int capacity, int stopSize) {
    super(capacity, stopSize, new NetworkHitCmpDsc<E>());
  }
  
  @Override
  public boolean add(E element, double score, String source, Proximity proximity) {
    stopCounter++;
    
    if(sortedSet.size() >= capacity) {
      Hit<E> last = sortedSet.last();
      if(last.getProximity().ordinal() > proximity.ordinal() || last.getScore() < score) {
        sortedSet.remove(last);
        
        last.clear();
        last.setScore(score);
        last.setSource(source);
        last.setElement(element);
        last.setProximity(proximity);
        return sortedSet.add(last);
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
      Hit<E> last = sortedSet.last();
      
      if(last.getProximity().ordinal() > hit.getProximity().ordinal() || last.getScore() < hit.getScore()) {
        sortedSet.remove(last);
        return sortedSet.add(hit);
      } else {
        return false;
      }
    } else {
      return sortedSet.add(hit);
    }
  }
  
  static final class NetworkHitCmpDsc<E extends Element> implements Comparator<Hit<E>>, Serializable {
    private static final long serialVersionUID = 1L;
    
    @Override
    public int compare(Hit<E> h1, Hit<E> h2) {
      // Use descending order
      int cmp = h1.getProximity().ordinal() - h2.getProximity().ordinal();
      return (cmp == 0) ? h2.compareTo(h1) : cmp;
    }
  }
  
  @Override
  public Collector<E> newInstance() {
    return new NetworkSortedCollector<E>(capacity, stopSize);
  }
}
