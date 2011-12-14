package cleo.search;

import java.io.IOException;

/**
 * Indexer
 * 
 * @author jwu
 * @since 01/12, 2011
 * 
 * @param <E> Element
 */
public interface Indexer<E extends Element> {
  
  /**
   * Indexes an element.
   * 
   * @param element - element to index
   * @return <code>true</code> if the element successfully indexed.
   *         Otherwise, <code>false</code>.
   * @throws Exception - NullPointerException is thrown upon a <code>null</code> element. 
   */
  public boolean index(E element) throws Exception;
  
  /**
   * Flushes indexes.
   * 
   * @throws IOException
   */
  public void flush() throws IOException;
}
