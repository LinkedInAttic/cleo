package cleo.search.util;

import java.util.Iterator;

/**
 * StringIterator
 * 
 * @author jwu
 * @since 02/04, 2011
 */
public final class StringIterator implements Iterator<String> {
  private final Iterator<byte[]> iterator;
  
  public StringIterator(Iterator<byte[]> iterator) {
    this.iterator = iterator;
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public String next() {
    return new String(iterator.next());
  }
  
  @Override
  public void remove() {
    iterator.remove();
  }
}
