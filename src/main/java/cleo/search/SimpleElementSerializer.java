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
