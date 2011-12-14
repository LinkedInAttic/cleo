package cleo.search.test.store;

import java.util.Random;

import cleo.search.store.StaticIntArrayPartition;
import cleo.search.store.StaticLongArrayPartition;

import junit.framework.TestCase;

/**
 * TestArrayPartition
 * 
 * @author jwu
 * @since 02/03, 2011
 */
public class TestArrayPartition extends TestCase {
  private final Random rand = new Random();
  
  public TestArrayPartition(String name) {
    super(name);
  }
  
  public void testStaticIntArrayPartition() {
    int indexStart = rand.nextInt(10000);
    int capacity = Math.max(1000, rand.nextInt(10000));
    int indexEnd = indexStart + capacity;
    
    StaticIntArrayPartition p = new StaticIntArrayPartition(indexStart, capacity);

    assertEquals(indexStart, p.getIndexStart());
    assertEquals(indexEnd, p.getIndexEnd());
    assertEquals(capacity, p.capacity());
    
    assertTrue(p.hasIndex(p.getIndexStart()));
    assertFalse(p.hasIndex(p.getIndexStart() - 1));
    
    assertTrue(p.hasIndex(p.getIndexEnd() - 1));
    assertFalse(p.hasIndex(p.getIndexEnd()));
    
    for(int i = 0, cnt = rand.nextInt(p.capacity()); i < cnt; i++) {
      int index = indexStart + rand.nextInt(capacity);
      int value = rand.nextInt();
      
      p.set(index, value);
      assertEquals(value, p.get(index));
    }
    
    try {
      p.set(indexStart - 1, rand.nextInt());
    } catch(Exception e) {
      assertTrue(e.getClass() == ArrayIndexOutOfBoundsException.class);
    }
    
    try {
      p.set(indexEnd, rand.nextInt());
    } catch(Exception e) {
      assertTrue(e.getClass() == ArrayIndexOutOfBoundsException.class);
    }
    
    p.clear();
    for(int i = indexStart; i < indexEnd; i++) {
      assertEquals(0, p.get(i));
    }
  }
  
  public void testStaticLongArrayPartition() {
    int indexStart = rand.nextInt(10000);
    int capacity = Math.max(1000, rand.nextInt(10000));
    int indexEnd = indexStart + capacity;
    
    StaticLongArrayPartition p = new StaticLongArrayPartition(indexStart, capacity);

    assertEquals(indexStart, p.getIndexStart());
    assertEquals(indexEnd, p.getIndexEnd());
    assertEquals(capacity, p.capacity());
    
    assertTrue(p.hasIndex(p.getIndexStart()));
    assertFalse(p.hasIndex(p.getIndexStart() - 1));
    
    assertTrue(p.hasIndex(p.getIndexEnd() - 1));
    assertFalse(p.hasIndex(p.getIndexEnd()));
    
    for(int i = 0, cnt = rand.nextInt(p.capacity()); i < cnt; i++) {
      int index = indexStart + rand.nextInt(capacity);
      long value = rand.nextLong();
      
      p.set(index, value);
      assertEquals(value, p.get(index));
    }
    
    try {
      p.set(indexStart - 1, rand.nextLong());
    } catch(Exception e) {
      assertTrue(e.getClass() == ArrayIndexOutOfBoundsException.class);
    }
    
    try {
      p.set(indexEnd, rand.nextLong());
    } catch(Exception e) {
      assertTrue(e.getClass() == ArrayIndexOutOfBoundsException.class);
    }
    
    p.clear();
    for(int i = indexStart; i < indexEnd; i++) {
      assertEquals(0, p.get(i));
    }
  }
}
