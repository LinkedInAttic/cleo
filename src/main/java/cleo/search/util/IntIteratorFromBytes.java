/*
 * Copyright (c) 2011 LinkedIn, Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
