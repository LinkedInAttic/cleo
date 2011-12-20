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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * SimpleElementSerializer
 * 
 * @author jwu
 * @since 01/20, 2011
 */
public class SimpleElementSerializer implements ElementSerializer<SimpleElement> {
  
  @Override
  public SimpleElement deserialize(byte[] bytes) throws ElementSerializationException {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bais);
      return (SimpleElement)ois.readObject();
    } catch (Exception e) {
      throw new ElementSerializationException("Failed to deserialize from bytes");
    }
  }
  
  @Override
  public byte[] serialize(SimpleElement element) throws ElementSerializationException {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
      ObjectOutputStream out = new ObjectOutputStream(baos);
      out.writeObject(element);
      return baos.toByteArray();
    } catch (Exception e) {
      throw new ElementSerializationException("Failed to serialize element: " + element);
    }
  }

}
