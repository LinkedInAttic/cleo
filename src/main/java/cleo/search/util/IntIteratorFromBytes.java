package cleo.search.util;

import java.nio.ByteBuffer;

/**
 * IntIteratorFromBytes
 * 
 * @author jwu
 * @since 05/06, 2011
 */
public class IntIteratorFromBytes implements IntIterator {
  private final ByteBuffer bb;
  private final int count;
  private int index = 0;
  
  public IntIteratorFromBytes(byte[] bytes, int offset, int length) {
    if(offset < 0 || length < 0 || bytes.length < (offset + length)) {
      throw new IllegalArgumentException("offset and length");
    }
    this.bb = ByteBuffer.wrap(bytes, offset, length);
    this.count = length / 4;
  }
  
  @Override
  public boolean hasNext() {
    return index < count;
  }
  
  @Override
  public int next() {
    index++;
    return bb.getInt();
  }
  
  /**
   * @return the byte array that backs this iterator.
   */
  public byte[] array() {
    return bb.array();
  }
}
