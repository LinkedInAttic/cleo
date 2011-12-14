package cleo.search;

/**
 * ElementSerializer
 * 
 * @author jwu
 * @since 01/12, 2011
 * 
 * @param <E> Element
 */
public interface ElementSerializer<E extends Element> {

  public byte[] serialize(E element) throws ElementSerializationException;
  
  public E deserialize(byte[] bytes) throws ElementSerializationException;
  
}
