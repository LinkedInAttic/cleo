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
