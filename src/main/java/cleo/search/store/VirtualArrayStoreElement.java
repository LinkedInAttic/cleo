package cleo.search.store;

import java.io.IOException;

import krati.array.Array;
import krati.util.SourceWaterMarks;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.util.ScnFactory;

/**
 * VirtualArrayStoreElement
 * 
 * @author jwu
 * @since 05/10, 2011
 */
public class VirtualArrayStoreElement<E extends Element> implements ArrayStoreElement<E> {
  protected final String source;
  protected final ScnFactory scnFactory;
  protected final ArrayStoreElement<E> elementStore;
  protected final SourceWaterMarks sourceWaterMarks;
  protected volatile long hwMark;
  protected volatile long lwMark;
  
  /**
   * Creates a new instance of VirtualArrayStoreElement for a user specified source.
   * 
   * @param source       - Source where new updates originate
   * @param scnFactory   - System change number (SCN) factory
   * @param elementStore - Underlying element store
   * @param waterMarks   - Source water marks for maintaining progress from a source.
   */
  public VirtualArrayStoreElement(String source, ScnFactory scnFactory, ArrayStoreElement<E> elementStore, SourceWaterMarks waterMarks) {
    this.source = source;
    this.scnFactory = scnFactory;
    this.elementStore = elementStore;
    this.sourceWaterMarks = waterMarks;
    
    // Initialize source water marks
    this.lwMark = waterMarks.getLWMScn(source);
    this.hwMark = waterMarks.getHWMScn(source);
  }
  
  /**
   * @return the source where new updates originate.
   */
  public final String getSource() {
    return source;
  }
  
  /**
   * @return the system change number (SCN) factory.
   */
  public final ScnFactory getScnFactory() {
    return scnFactory;
  }
  
  /**
   * @return the underlying element store.
   */
  public final ArrayStoreElement<E> getElementStore() {
    return elementStore;
  }
  
  /**
   * @return the source water marks where progress from the source is maintained.
   */
  public final SourceWaterMarks getSourceWaterMarks() {
    return sourceWaterMarks;
  }
  
  @Override
  public int length() {
    return elementStore.length();
  }
  
  @Override
  public int capacity() {
    return elementStore.capacity();
  }
  
  @Override
  public int getIndexStart() {
    return elementStore.getIndexStart();
  }
  
  @Override
  public boolean hasIndex(int index) {
    return elementStore.hasIndex(index);
  }
  
  @Override
  public E getElement(int index) {
    return elementStore.getElement(index);
  }
  
  @Override
  public synchronized void deleteElement(int index, long sourceScn) throws Exception {
    long internalScn = scnFactory.next();
    elementStore.deleteElement(index, internalScn);
    
    hwMark = Math.max(sourceScn, hwMark);
    if(elementStore.getHWMark() == elementStore.getLWMark()) {
      syncWaterMarksInternal();
    }
  }
  
  @Override
  public synchronized void setElement(int index, E element, long sourceScn) throws Exception {
    long internalScn = scnFactory.next();
    elementStore.setElement(index, element, internalScn);
    
    hwMark = Math.max(sourceScn, hwMark);
    if(elementStore.getHWMark() == elementStore.getLWMark()) {
      syncWaterMarksInternal();
    }
  }
  
  @Override
  public long getHWMark() {
    return hwMark;
  }
  
  @Override
  public long getLWMark() {
    return lwMark;
  }
  
  @Override
  public synchronized void saveHWMark(long endOfPeriod) throws Exception {
    hwMark = endOfPeriod;
  }
  
  @Override
  public synchronized void persist() throws IOException {
    elementStore.persist();
    syncWaterMarksInternal();
  }
  
  @Override
  public synchronized void sync() throws IOException {
    elementStore.sync();
    syncWaterMarksInternal();
  }
  
  @Override
  public synchronized void clear() {
    elementStore.clear();
    lwMark = hwMark = 0;
    synchronized(sourceWaterMarks) {
      sourceWaterMarks.clear();
      syncWaterMarksInternal();
    }
  }
  
  protected void syncWaterMarksInternal() {
    lwMark = hwMark;
    synchronized(sourceWaterMarks) {
      sourceWaterMarks.syncWaterMarks(source, lwMark, hwMark);
    }
  }
  
  @Override
  public byte[] getElementBytes(int index) {
    return elementStore.getElementBytes(index);
  }
  
  @Override
  public synchronized void setElementBytes(int index, byte[] elementBytes, long scn) throws Exception {
    elementStore.setElementBytes(index, elementBytes, scn);
  }
  
  @Override
  public ElementSerializer<E> getElementSerializer() {
    return elementStore.getElementSerializer();
  }
  
  @Override
  public Array.Type getType() {
    return elementStore.getType();
  }
}
