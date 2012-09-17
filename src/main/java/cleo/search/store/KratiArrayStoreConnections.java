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

import java.io.IOException;

/**
 * KratiArrayStoreConnections
 * 
 * @author jwu
 * @since 02/01, 2011
 * 
 * <p>
 * 09/18, 2011 - Added readBytes to support partial reads <br/>
 */
public final class KratiArrayStoreConnections implements ArrayStoreConnections {
  /**
   * The underlying ArrayStore for list of integers.
   */
  protected final KratiArrayStoreInts storeInts;
  
  public KratiArrayStoreConnections(KratiArrayStoreInts storeInts) {
    this.storeInts = storeInts;
  }
  
  @Override
  public int[] getConnections(int source) {
    return storeInts.get(source);
  }
  
  @Override
  public void setConnections(int source, int[] connections, long scn) throws Exception {
    storeInts.set(source, connections, scn);
  }
  
  @Override
  public void deleteConnections(int source, long scn) throws Exception {
    storeInts.delete(source, scn);
  }
  
  @Override
  public void addConnection(int source, int connection, long scn) throws Exception {
    storeInts.add(source, connection, scn);
  }
  
  @Override
  public void removeConnection(int source, int connection, long scn) throws Exception {
    storeInts.remove(source, connection, scn);
  }

  @Override
  public byte[] getBytes(int index) {
    return storeInts.getUnderlyingStore().get(index);
  }

  @Override
  public int getBytes(int index, byte[] dst) {
    return storeInts.getUnderlyingStore().get(index, dst);
  }

  @Override
  public int getBytes(int index, byte[] dst, int offset) {
    return storeInts.getUnderlyingStore().get(index, dst, offset);
  }

  @Override
  public int readBytes(int index, byte[] dst) {
    return storeInts.getUnderlyingStore().read(index, dst);
  }

  @Override
  public int readBytes(int index, int offset, byte[] dst) {
    return storeInts.getUnderlyingStore().read(index, offset, dst);
  }

  @Override
  public int getLength(int index) {
    return storeInts.getUnderlyingStore().getLength(index);
  }

  @Override
  public void sync() throws IOException {
    storeInts.sync();
  }

  @Override
  public void persist() throws IOException {
    storeInts.persist();
  }

  @Override
  public long getLWMark() {
    return storeInts.getLWMark();
  }

  @Override
  public long getHWMark() {
    return storeInts.getHWMark();
  }

  @Override
  public void saveHWMark(long endOfPeriod) throws Exception {
    storeInts.saveHWMark(endOfPeriod);
  }

  @Override
  public void clear() {
    storeInts.clear();
  }

  @Override
  public int length() {
    return storeInts.length();
  }

  @Override
  public boolean hasIndex(int index) {
    return storeInts.hasIndex(index);
  }

  @Override
  public Type getType() {
    return storeInts.getType();
  }
}
