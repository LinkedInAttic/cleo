package cleo.search.store;

/**
 * KratiArrayStoreConnections
 * 
 * @author jwu
 * @since 02/01, 2011
 * 
 * <p>
 * 09/18, 2011 - Added readBytes to support partial reads <br/>
 */
public final class KratiArrayStoreConnections extends KratiArrayStoreInts implements ArrayStoreConnections {
  
  public KratiArrayStoreConnections(KratiArrayStore store) {
    this(store, 0);
  }
  
  public KratiArrayStoreConnections(KratiArrayStore store, int indexStart) {
    super(store);
  }
  
  @Override
  public int[] getConnections(int source) {
    return get(source);
  }
  
  @Override
  public void setConnections(int source, int[] connections, long scn) throws Exception {
    set(source, connections, scn);
  }
  
  @Override
  public void deleteConnections(int source, long scn) throws Exception {
    delete(source, scn);
  }
  
  @Override
  public void addConnection(int source, int connection, long scn) throws Exception {
    add(source, connection, scn);
  }
  
  @Override
  public void removeConnection(int source, int connection, long scn) throws Exception {
    remove(source, connection, scn);
  }

  @Override
  public byte[] getBytes(int index) {
    return store.get(index);
  }

  @Override
  public int getBytes(int index, byte[] dst) {
    return store.get(index, dst);
  }

  @Override
  public int getBytes(int index, byte[] dst, int offset) {
    return store.get(index, dst, offset);
  }

  @Override
  public int readBytes(int index, byte[] dst) {
    return store.read(index, dst);
  }

  @Override
  public int readBytes(int index, int offset, byte[] dst) {
    return store.read(index, offset, dst);
  }

  @Override
  public int getLength(int index) {
    return store.getLength(index);
  }
}
