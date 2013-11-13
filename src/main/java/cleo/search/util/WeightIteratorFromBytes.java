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
 * WeightIteratorFromBytes
 * 
 * @author jwu
 * @since 04/30, 2011
 */
public class WeightIteratorFromBytes implements WeightIterator {
  private final ByteBuffer bb;
  private final int count;
  private int index = 0;
  
  public WeightIteratorFromBytes(byte[] bytes, int offset, int length) {
    if(offset < 0 || length < 0 || bytes.length < (offset + length)) {
      throw new IllegalArgumentException("offset and length");
    }
    this.bb = ByteBuffer.wrap(bytes, offset, length);
    this.count = length / (Weight.ELEMENT_ID_NUM_BYTES + Weight.ELEMENT_WEIGHT_NUM_BYTES);
  }
  
  @Override
  public void next(Weight weight) {
    index++;
    weight.elementId = bb.getInt();
    weight.elementWeight = bb.getInt();
  }

  @Override
  public boolean hasNext() {
    return index < count;
  }

  @Override
  public Weight next() {
    index++;
    return new Weight(bb.getInt(), bb.getInt());
  }
  
  @Override
  public void remove() {}
  
  /**
   * @return the byte array that backs this iterator.
   */
  public byte[] array() {
    return bb.array();
  }
}
