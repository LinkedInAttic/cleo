/*
 * Copyright (c) 2012 LinkedIn, Inc
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

import krati.io.SerializationException;
import krati.io.serializer.JavaSerializer;

import cleo.search.ElementSerializationException;
import cleo.search.ElementSerializer;

/**
 * ElementJavaSerializer
 * 
 * @author jwu
 * @since 01/17, 2012
 */
public class ElementJavaSerializer<E extends Element> implements ElementSerializer<E> {
  private final JavaSerializer<E> serializer = new JavaSerializer<E>();
  
  @Override
  public E deserialize(byte[] bytes) throws ElementSerializationException {
    try {
      return serializer.deserialize(bytes);
    } catch(SerializationException e) {
      throw new ElementSerializationException(e);
    }
  }
  
  @Override
  public byte[] serialize(E object) throws ElementSerializationException {
    try {
      return serializer.serialize(object);
    } catch(SerializationException e) {
      throw new ElementSerializationException(e);
    }
  }
}
