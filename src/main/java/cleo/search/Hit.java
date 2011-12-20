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

import java.io.Serializable;

import cleo.search.network.Proximity;

/**
 * Hit
 * 
 * @author jwu
 * @since 02/11, 2011
 * 
 * @param <E> Element
 */
public interface Hit<E extends Element> extends Score, Serializable, Comparable<Hit<? super E>> {
  
  public void clear();

  public String getSource();
  
  public void setSource(String source);
  
  public E getElement();
  
  public void setElement(E element);
  
  public Proximity getProximity();
  
  public void setProximity(Proximity proximity); 
}
