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

package cleo.search.store;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import krati.Persistable;
import krati.array.Array;

import java.io.Closeable;
import java.io.IOException;

/**
 * ArrayStoreElement
 * 
 * @author jwu
 * @since 01/19, 2011
 * 
 * <p>
 * 05/27, 2011 - Added methods getElmentBytes/setElementBytes <br/>
 */
public interface ArrayStoreElement<E extends Element> extends Persistable, Array, Closeable {
  
  /**
   * @return the capacity of this ArrayStoreElement.
   */
  public int capacity();
  
  /**
   * @return the start index of this ArrayStoreElement.
   */
  public int getIndexStart();
  
  /**
   * Gets the element at an index.
   * 
   * @param index
   * @return the element at an index
   * 
   * @throws ElementSerializationException if the raw bytes read from underlying store cannot be de-serialized into an Element.
   */
  public E getElement(int index);
  
  /**
   * Sets an element at an index.
   * 
   * @param index
   * @param element
   * @param scn
   * @throws Exception
   */
  public void setElement(int index, E element, long scn) throws Exception;
  
  /**
   * Deletes an element at an index.
   * 
   * @param index
   * @param scn
   * @throws Exception
   */
  public void deleteElement(int index, long scn) throws Exception;
  
  /**
   * Gets the element at an index in the form of byte array.
   * 
   * @param index
   * @return the element at an index
   * 
   * @throws ElementSerializationException if the raw bytes read from underlying store cannot be de-serialized into an Element.
   */
  public byte[] getElementBytes(int index);
  
  /**
   * Sets an element at an index in the form of byte array.
   * 
   * @param index
   * @param elementBytes
   * @param scn
   * @throws Exception
   */
  public void setElementBytes(int index, byte[] elementBytes, long scn) throws Exception;
  
  /**
   * @return the element serializer.
   */
  public ElementSerializer<E> getElementSerializer();

  /**
   * closes the store
   * @throws IOException
   */
  public void close() throws IOException;
  
}
