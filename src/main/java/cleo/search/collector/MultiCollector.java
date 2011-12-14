package cleo.search.collector;

import java.util.Collection;

import cleo.search.Element;

/**
 * MultiCollector
 * 
 * @author jwu
 * @since 02/10, 2011
 * 
 * @param <E> Element
 */
public interface MultiCollector<E extends Element> extends Collector<E> {

  /**
   * @return the Collection view of sources in this MultiCollector.
   */
  public Collection<String> sources();
  
  /**
   * @return the Collection view of collectors in this MulitCollector.
   */
  public Collection<Collector<E>> collectors();
  
  /**
   * Gets the collector for a specific source.
   * 
   * @param source - collector source.
   * @return the collector for a specific source.
   */
  public Collector<E> getCollector(String source);
  
  /**
   * Puts a collector for a specific source.
   *  
   * @param source
   * @param collector
   * @return <code>true</code> if the collector associated with a source is added successfully. Otherwise, <code>false</code>.
   */
  public boolean putCollector(String source, Collector<E> collector);
  
}
