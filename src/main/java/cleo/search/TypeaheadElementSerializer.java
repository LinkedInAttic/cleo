package cleo.search;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * TypeaheadElementSerializer
 * 
 * @author jwu
 * @since 02/05, 2011
 */
public class TypeaheadElementSerializer implements ElementSerializer<TypeaheadElement> {

  @Override
  public TypeaheadElement deserialize(byte[] bytes) throws ElementSerializationException {
    try {
      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      ObjectInputStream ois = new ObjectInputStream(bais);
      return (TypeaheadElement)ois.readObject();
    } catch (Exception e) {
      throw new ElementSerializationException("Failed to deserialize from bytes");
    }
  }

  @Override
  public byte[] serialize(TypeaheadElement element) throws ElementSerializationException {
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
