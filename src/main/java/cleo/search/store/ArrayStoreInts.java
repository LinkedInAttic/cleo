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

import java.io.File;

import krati.Persistable;
import krati.array.Array;

/**
 * ArrayStoreInts
 * 
 * @author jwu
 * @since 01/04, 2011
 *
 */
public interface ArrayStoreInts extends Persistable, Array {
  
  public String getStatus();
  
  public File getStoreHome();
  
  public int capacity();
  
  public int getIndexStart();
  
  public int getCount(int index);
  
  public int[] get(int index);
  
  public void set(int index, int[] elemIds, long scn) throws Exception;
  
  public void delete(int index, long scn) throws Exception;
  
  public void add(int index, int elemId, long scn) throws Exception;
  
  public void remove(int index, int elemId, long scn) throws Exception;
  
}
