package cleo.search.store;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import krati.Persistable;
import krati.array.Array;

/**
 * ArrayStoreElement
 * 
 * @author jwu
 * @since 01/19, 2011
 * 
 * <p>
 * 05/27, 2011 - Added methods getElmentBytes/setElementBytes <br/>
 */
public interface ArrayStoreElement<E extends Element> extends Persistable, Array {
  
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
  
}
