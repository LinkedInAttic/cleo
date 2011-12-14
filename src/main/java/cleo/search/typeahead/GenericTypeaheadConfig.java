package cleo.search.typeahead;

import java.io.File;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;

import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;

/**
 * GenericTypeaheadConfig
 * 
 * @author jwu
 * @since 02/09, 2011
 */
public class GenericTypeaheadConfig<E extends Element> {
  private String name;
  
  // elementStore 
  private File elementStoreDir;
  private int elementStoreIndexStart;
  private int elementStoreCapacity;
  private int elementStoreSegmentMB;
  private SegmentFactory elementStoreSegmentFactory = new MemorySegmentFactory();
  
  // cache elementStore in memory
  private boolean elementStoreCached = true;
  
  // connectionsStore
  private File connectionsStoreDir;
  private int connectionsStoreCapacity;
  private int connectionsStoreSegmentMB;
  private SegmentFactory connectionsStoreSegmentFactory = new MemorySegmentFactory();
  private int connectionsStoreIndexSegmentMB;
  private SegmentFactory connectionsStoreIndexSegmentFactory = new MemorySegmentFactory();
  
  // BloomFilter prefixLength
  private int filterPrefixLength = 2;
  
  // max length of String key 
  private int maxKeyLength = 7;
  
  // elementSerializer
  private ElementSerializer<E> elementSerializer;
  
  // elementSelectorFactory
  private SelectorFactory<E> selectorFactory = new PrefixSelectorFactory<E>();
  
  // elementScoreFile
  private File elementScoreFile;
  
  // searchTimeoutMillis
  private long searchTimeoutMillis = 15;
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void setElementStoreDir(File elementStoreDir) {
    this.elementStoreDir = elementStoreDir;
  }
  
  public File getElementStoreDir() {
    return elementStoreDir;
  }
  
  public void setElementStoreIndexStart(int elementStoreIndexStart) {
    this.elementStoreIndexStart = elementStoreIndexStart;
  }
  
  public int getElementStoreIndexStart() {
    return elementStoreIndexStart;
  }
  
  public void setElementStoreCapacity(int elementStoreCapacity) {
    this.elementStoreCapacity = elementStoreCapacity;
  }
  
  public int getElementStoreCapacity() {
    return elementStoreCapacity;
  }
  
  public void setElementStoreSegmentMB(int elementStoreSegmentMB) {
    this.elementStoreSegmentMB = elementStoreSegmentMB;
  }
  
  public int getElementStoreSegmentMB() {
    return elementStoreSegmentMB;
  }
  
  public void setElementStoreSegmentFactory(SegmentFactory elementStoreSegmentFactory) {
    this.elementStoreSegmentFactory = elementStoreSegmentFactory;
  }
  
  public SegmentFactory getElementStoreSegmentFactory() {
    return elementStoreSegmentFactory;
  }
  
  public void setElementStoreCached(boolean elementStoreCached) {
    this.elementStoreCached = elementStoreCached;
  }
  
  public boolean getElementStoreCached() {
    return elementStoreCached;
  }
  
  public boolean isElementStoreCached() {
    return elementStoreCached;
  }
  
  public void setConnectionsStoreDir(File connectionsStoreDir) {
    this.connectionsStoreDir = connectionsStoreDir;
  }
  
  public File getConnectionsStoreDir() {
    return connectionsStoreDir;
  }
  
  public void setConnectionsStoreCapacity(int connectionsStoreCapacity) {
    this.connectionsStoreCapacity = connectionsStoreCapacity;
  }
  
  public int getConnectionsStoreCapacity() {
    return connectionsStoreCapacity;
  }
  
  public void setConnectionsStoreSegmentMB(int connectionsStoreSegmentMB) {
    this.connectionsStoreSegmentMB = connectionsStoreSegmentMB;
  }
  
  public int getConnectionsStoreSegmentMB() {
    return connectionsStoreSegmentMB;
  }
  
  public void setConnectionsStoreSegmentFactory(SegmentFactory connectionsStoreSegmentFactory) {
    this.connectionsStoreSegmentFactory = connectionsStoreSegmentFactory;
  }
  
  public SegmentFactory getConnectionsStoreSegmentFactory() {
    return connectionsStoreSegmentFactory;
  }
  
  public void setConnectionsStoreIndexSegmentMB(int connectionsStoreIndexSegmentMB) {
    this.connectionsStoreIndexSegmentMB = connectionsStoreIndexSegmentMB;
  }
  
  public int getConnectionsStoreIndexSegmentMB() {
    return connectionsStoreIndexSegmentMB;
  }
  
  public void setConnectionsStoreIndexSegmentFactory(SegmentFactory connectionsStoreIndexSegmentFactory) {
    this.connectionsStoreIndexSegmentFactory = connectionsStoreIndexSegmentFactory;
  }
  
  public SegmentFactory getConnectionsStoreIndexSegmentFactory() {
    return connectionsStoreIndexSegmentFactory;
  }
  
  public int getFilterPrefixLength() {
    return filterPrefixLength;
  }
  
  public void setFilterPrefixLength(int filterPrefixLength) {
    this.filterPrefixLength = filterPrefixLength;
  }
  
  public void setMaxKeyLength(int maxKeyLength) {
    this.maxKeyLength = maxKeyLength;
  }
  
  public int getMaxKeyLength() {
    return maxKeyLength;
  }
  
  public void setElementSerializer(ElementSerializer<E> elementSerializer) {
    this.elementSerializer = elementSerializer;
  }
  
  public ElementSerializer<E> getElementSerializer() {
    return elementSerializer;
  }
  
  public void setSelectorFactory(SelectorFactory<E> selectorFactory) {
    this.selectorFactory = selectorFactory;
  }
  
  public SelectorFactory<E> getSelectorFactory() {
    return selectorFactory;
  }

  public void setElementScoreFile(File elementScoreFile) {
    this.elementScoreFile = elementScoreFile;
  }
  
  public File getElementScoreFile() {
    return elementScoreFile;
  }
  
  public void setSearchTimeoutMillis(long searchTimeoutMillis) {
    this.searchTimeoutMillis = searchTimeoutMillis;
  }
  
  public long getSearchTimeoutMillis() {
    return searchTimeoutMillis;
  }
}
