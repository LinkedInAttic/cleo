package cleo.search;

/**
 * ElementFactory
 * 
 * @author jwu
 * @since 01/20, 2011
 * 
 * @param <E> Element
 */
public interface ElementFactory<E extends Element> {

  /**
   * Creates a new element based on a user supplied Element.
   * 
   * @param element - User supplied Element.
   * @return a new Element
   */
  public E createElement(Element element);
  
  /**
   * Creates a new element based on user supplied information.
   * @param elementId - Element Id
   * @param timestamp - Element timestamp
   * @param terms     - Element terms
   * @return a new Element
   */
  public E createElement(int elementId, long timestamp, String... terms);
  
}
