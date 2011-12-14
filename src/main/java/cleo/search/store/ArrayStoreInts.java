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
