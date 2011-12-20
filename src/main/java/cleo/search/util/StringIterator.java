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
