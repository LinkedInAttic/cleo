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

package cleo.search.typeahead;

import java.io.File;

import krati.core.segment.MappedSegmentFactory;
import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;

import cleo.search.Element;
import cleo.search.ElementSerializer;
import cleo.search.connection.ConnectionFilter;
import cleo.search.selector.PrefixSelectorFactory;
import cleo.search.selector.SelectorFactory;

/**
 * NetworkTypeaheadConfig
 * 
 * @author jwu
 * @since 01/25, 2011
 */
public class NetworkTypeaheadConfig<E extends Element> {
  private String name;
  
  // Partition
  private int partitionStart;
  private int partitionCount;
  
  // elementStore 
  private File elementStoreDir;
  private int elementStoreIndexStart;
  private int elementStoreCapacity;
  private int elementStoreSegmentMB;
  private SegmentFactory elementStoreSegmentFactory = new MappedSegmentFactory();
  
  // cache elementStore in memory
  private boolean elementStoreCached = true;
  
  // connectionsStore
  private File connectionsStoreDir;
  private int connectionsStoreIndexStart;
  private int connectionsStoreCapacity;
  private int connectionsStoreSegmentMB;
  private SegmentFactory connectionsStoreSegmentFactory = new MemorySegmentFactory();
  
  // connectionFilter
  private ConnectionFilter connectionFilter;
  
  // BloomFilter prefixLength
  private int filterPrefixLength;
  
  // elementSerializer
  private ElementSerializer<E> elementSerializer;
  
  // elementSelectorFactory
  private SelectorFactory<E> selectorFactory = new PrefixSelectorFactory<E>();
  
  // searchTimeoutMillis
  private long searchTimeoutMillis = 15;
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void setPartitionStart(int partitionStart) {
    this.partitionStart = partitionStart;
  }
  
  public int getPartitionStart() {
    return partitionStart;
  }
  
  public void setPartitionCount(int partitionCount) {
    this.partitionCount = partitionCount;
  }
  
  public int getPartitionCount() {
    return partitionCount;
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
  
  public void setConnectionsStoreIndexStart(int connectionsStoreIndexStart) {
    this.connectionsStoreIndexStart = connectionsStoreIndexStart;
  }
  
  public int getConnectionsStoreIndexStart() {
    return connectionsStoreIndexStart;
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
  
  public int getFilterPrefixLength() {
    return filterPrefixLength;
  }
  
  public void setFilterPrefixLength(int filterPrefixLength) {
    this.filterPrefixLength = filterPrefixLength;
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
  
  public void setConnectionFilter(ConnectionFilter connectionFilter) {
    this.connectionFilter = connectionFilter;
  }
  
  public ConnectionFilter getConnectionFilter() {
    return connectionFilter;
  }
  
  public void setSearchTimeoutMillis(long searchTimeoutMillis) {
    this.searchTimeoutMillis = searchTimeoutMillis;
  }
  
  public long getSearchTimeoutMillis() {
    return searchTimeoutMillis;
  }
}
