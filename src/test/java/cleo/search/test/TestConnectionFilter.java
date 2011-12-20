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

package cleo.search.test;

import java.util.Random;

import cleo.search.connection.Connection;
import cleo.search.connection.ConnectionFilter;
import cleo.search.connection.RandomConnectionFilter;
import cleo.search.connection.SimpleConnection;
import cleo.search.connection.SourcePartitionConnectionFilter;
import cleo.search.connection.TargetPartitionConnectionFilter;
import cleo.search.connection.TransitivePartitionConnectionFilter;
import cleo.search.util.Range;
import junit.framework.TestCase;

/**
 * TestConnectionFilter
 * 
 * @author jwu
 * @since 04/20, 2011
 * 
 * <p>
 * 05/27, 2011 - Added testEquals <br/>
 */
public class TestConnectionFilter extends TestCase {
  private final Random rand = new Random();
  
  public void testSourcePartitionConnectionFilter() {
    int start = rand.nextInt(10000) + 100;
    int count = rand.nextInt(10000) + 100;
    Range sourceRange = new Range(start, count);
    ConnectionFilter connFilter = new SourcePartitionConnectionFilter(sourceRange);
    
    Connection conn = new SimpleConnection(0, rand.nextInt(), true);
    assertEquals(false, connFilter.accept(conn));
    
    conn = new SimpleConnection(rand.nextInt(start), rand.nextInt(), true);
    assertEquals(false, connFilter.accept(conn));
    
    conn = new SimpleConnection(start + rand.nextInt(count), rand.nextInt(), true);
    assertEquals(true, connFilter.accept(conn));
    
    conn = new SimpleConnection(sourceRange.getEnd(), rand.nextInt(), true);
    assertEquals(false, connFilter.accept(conn));
    
    conn = new SimpleConnection(sourceRange.getEnd() + rand.nextInt(count), rand.nextInt(), true);
    assertEquals(false, connFilter.accept(conn));
  }
  
  public void testTargetPartitionConnectionFilter() {
    int start = rand.nextInt(10000) + 100;
    int count = rand.nextInt(10000) + 100;
    Range targetRange = new Range(start, count);
    ConnectionFilter connFilter = new TargetPartitionConnectionFilter(targetRange);
    
    Connection conn = new SimpleConnection(rand.nextInt(), 0, true);
    assertEquals(false, connFilter.accept(conn));
    
    conn = new SimpleConnection(rand.nextInt(), rand.nextInt(start), true);
    assertEquals(false, connFilter.accept(conn));
    
    conn = new SimpleConnection(rand.nextInt(), start + rand.nextInt(count), true);
    assertEquals(true, connFilter.accept(conn));
    
    conn = new SimpleConnection(rand.nextInt(), targetRange.getEnd(), true);
    assertEquals(false, connFilter.accept(conn));
    
    conn = new SimpleConnection(rand.nextInt(), targetRange.getEnd() + rand.nextInt(count), true);
    assertEquals(false, connFilter.accept(conn));
  }
  
  public void testTransitivePartitionConnectionFilter() {
    Range range = new Range(3000, 1000); // [3000, 4000)
    ConnectionFilter connFilter = new TransitivePartitionConnectionFilter(range);
    Connection conn;
    
    // Source in range
    conn = new SimpleConnection(3100, 1, true);
    assertEquals(true, connFilter.accept(conn));
    
    conn = new SimpleConnection(3100, 3200, true);
    assertEquals(true, connFilter.accept(conn));

    conn = new SimpleConnection(3100, 5000, true);
    assertEquals(true, connFilter.accept(conn));
    
    // Target in range
    conn = new SimpleConnection(1, 3100, true);
    assertEquals(true, connFilter.accept(conn));

    conn = new SimpleConnection(3200, 3100, true);
    assertEquals(true, connFilter.accept(conn));
    
    conn = new SimpleConnection(5000, 3100, true);
    assertEquals(true, connFilter.accept(conn));
    
    // Neither source nor target in range
    conn = new SimpleConnection(1, 1, true);
    assertEquals(false, connFilter.accept(conn));
    
    conn = new SimpleConnection(1, 5000, true);
    assertEquals(false, connFilter.accept(conn));
  }
  
  public void testTransitivePartitionConnectionFilterRandom() {
    int source;
    int target;
    
    int start = rand.nextInt(100000) + 100;
    int count = rand.nextInt(100000) + 100;
    Range range = new Range(start, count);
    
    Connection conn;
    ConnectionFilter connFilter = new TransitivePartitionConnectionFilter(range);
    
    source = range.getStart();
    target = range.getStart();
    conn = new SimpleConnection(source, target, true);
    assertEquals(true, connFilter.accept(conn));

    source = range.getStart();
    target = range.getStart() - rand.nextInt(range.getStart());
    conn = new SimpleConnection(source, target, true);
    assertEquals(true, connFilter.accept(conn));
    
    source = range.getStart();
    target = range.getEnd();
    conn = new SimpleConnection(source, target, true);
    assertEquals(true, connFilter.accept(conn));
    
    source = range.getStart();
    target = range.getEnd() + rand.nextInt(range.getEnd());
    conn = new SimpleConnection(source, target, true);
    assertEquals(true, connFilter.accept(conn));
    
    for(int i = 0, cnt = rand.nextInt(100); i < cnt; i++) {
      source = range.getStart() + rand.nextInt(range.getCount());
      target = Math.abs(rand.nextInt());
      conn = new SimpleConnection(source, target, true);
      assertEquals(true, connFilter.accept(conn));

      source = Math.abs(rand.nextInt());
      target = range.getStart() + rand.nextInt(range.getCount());
      conn = new SimpleConnection(source, target, true);
      assertEquals(true, connFilter.accept(conn));
    }
    
    for(int i = 0, cnt = rand.nextInt(100); i < cnt; i++) {
      source = range.getStart() - rand.nextInt(range.getStart()) - 1;
      target = range.getStart() - rand.nextInt(range.getStart()) - 1;
      conn = new SimpleConnection(source, target, true);
      assertEquals(false, connFilter.accept(conn));
      
      source = range.getStart() - rand.nextInt(range.getStart()) - 1;
      target = range.getEnd() + rand.nextInt(range.getEnd());
      conn = new SimpleConnection(source, target, true);
      assertEquals(false, connFilter.accept(conn));
      
      source = range.getEnd() + rand.nextInt(range.getEnd());
      target = range.getStart() - rand.nextInt(range.getStart()) - 1;
      conn = new SimpleConnection(source, target, true);
      assertEquals(false, connFilter.accept(conn));
      
      source = range.getEnd() + rand.nextInt(range.getEnd());
      target = range.getEnd() + rand.nextInt(range.getEnd());
      conn = new SimpleConnection(source, target, true);
      assertEquals(false, connFilter.accept(conn));
    }
  }
  
  public void testRandomConnectionFilter() {
    int source = rand.nextInt(100000);
    int target = rand.nextInt(100000);
    Connection conn = new SimpleConnection(source, target, true);
    ConnectionFilter connFilter = new RandomConnectionFilter(); 
    
    boolean rejected = false;
    boolean accepted = false;
    
    while(!rejected) {
      rejected = !connFilter.accept(conn);
    }
    
    while(!accepted) {
      accepted = connFilter.accept(conn);
    }
    
    assertEquals(true, rejected);
    assertEquals(true, accepted);
  }
  
  public void testEquals() {
    ConnectionFilter cf1, cf2, cf3;
    Range range = new Range(rand.nextInt(10000), rand.nextInt(10000) + 100);
    
    // Test SourcePartionConnectionFilter
    cf1 = new SourcePartitionConnectionFilter(range);
    cf2 = null;
    assertFalse(cf1.equals(cf2));
    
    cf2 = new SourcePartitionConnectionFilter(range);
    assertTrue(cf1.equals(cf2));
    assertTrue(cf2.equals(cf1));
    
    assertTrue(cf1.equals(cf1));
    assertTrue(cf2.equals(cf2));
    
    assertEquals(cf1.hashCode(), cf2.hashCode());
    
    // Test TargetPartionConnectionFilter
    cf1 = new TargetPartitionConnectionFilter(range);
    cf2 = null;
    assertFalse(cf1.equals(cf2));
    
    cf2 = new TargetPartitionConnectionFilter(range);
    assertTrue(cf1.equals(cf2));
    assertTrue(cf2.equals(cf1));
    
    assertTrue(cf1.equals(cf1));
    assertTrue(cf2.equals(cf2));
    
    assertEquals(cf1.hashCode(), cf2.hashCode());
    
    // Test TransitivePartionConnectionFilter
    cf1 = new TransitivePartitionConnectionFilter(range);
    cf2 = null;
    assertFalse(cf1.equals(cf2));
    
    cf2 = new TransitivePartitionConnectionFilter(range);
    assertTrue(cf1.equals(cf2));
    assertTrue(cf2.equals(cf1));
    
    assertTrue(cf1.equals(cf1));
    assertTrue(cf2.equals(cf2));
    
    assertEquals(cf1.hashCode(), cf2.hashCode());
    
    // Test cross comparison
    cf1 = new SourcePartitionConnectionFilter(range);
    cf2 = new TargetPartitionConnectionFilter(range);
    cf3 = new TransitivePartitionConnectionFilter(range);
    
    assertFalse(cf1.equals(cf2));
    assertFalse(cf1.equals(cf3));
    assertFalse(cf2.equals(cf1));
    assertFalse(cf2.equals(cf3));
    assertFalse(cf3.equals(cf1));
    assertFalse(cf3.equals(cf2));
  }
}
