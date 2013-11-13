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

package cleo.search;

import java.util.Collection;

/**
 * IndexRoller - Update indexes by rolling a batch of elements all together.
 * 
 * @author jwu
 * @since 03/02, 2011
 */
public interface IndexRoller<E extends Element> {
  
  /**
   * Update the indexes by rolling a batch of elements all together.
   * 
   * @param elements
   * @return <code>true</code> if the batch of elements are indexed successfully. Otherwise, <code>false</code>.
   */
  public boolean roll(Collection<E> elements);
  
}
