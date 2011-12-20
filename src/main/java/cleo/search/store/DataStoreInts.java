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
import java.util.Iterator;

import krati.Persistable;

/**
 * DataStoreInts
 * 
 * @author jwu
 * @since 01/29, 2011
 */
public interface DataStoreInts extends Persistable {

  public String getStatus();
  
  public File getStoreHome();
  
  public int[] get(String key);
  
  public boolean put(String key, int[] elemIds, long scn) throws Exception;
  
  public boolean delete(String key, long scn) throws Exception;
  
  public void add(String key, int elemId, long scn) throws Exception;
  
  public void remove(String key, int elemId, long scn) throws Exception;
  
  public Iterator<String> keyIterator();
}
