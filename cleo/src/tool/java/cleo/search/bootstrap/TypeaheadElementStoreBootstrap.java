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

package cleo.search.bootstrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cleo.search.ElementFactory;
import cleo.search.ElementSerializer;
import cleo.search.SimpleTypeaheadElementFactory;
import cleo.search.TypeaheadElement;
import cleo.search.TypeaheadElementSerializer;
import cleo.search.store.ArrayStoreElement;
import cleo.search.store.StoreFactory;
import cleo.search.util.CompositeTermsHandler;
import cleo.search.util.TermsDedup;
import cleo.search.util.TermsScanner;

import krati.core.segment.MemorySegmentFactory;
import krati.core.segment.SegmentFactory;
import krati.util.Chronos;

/**
 * TypeaheadElementStoreBootstrap
 * 
 * @author jwu
 * @since 02/05, 2011
 */
public class TypeaheadElementStoreBootstrap extends ArrayStoreElementBootstrap<TypeaheadElement> {
  private final int indexStart;
  private final int indexEnd;
  private String[] line1Data;
  private String[] line2Data;
  private String[] line3Data;
  private String[] mediaData;
  
  public TypeaheadElementStoreBootstrap(ArrayStoreElement<TypeaheadElement> elementStore,
                                        ElementFactory<TypeaheadElement> elementFactory) throws Exception {
    super(elementStore, elementFactory);
    indexStart = elementStore.getIndexStart();
    indexEnd = indexStart + elementStore.capacity();
    line1Data = new String[elementStore.capacity()];
    line2Data = new String[elementStore.capacity()];
    line3Data = new String[elementStore.capacity()];
    mediaData = new String[elementStore.capacity()];
  }
  
  protected void loadData(File dataDir, String[] dataArray) {
    Chronos c = new Chronos();
    for(File f : dataDir.listFiles()) {
      if(f.isFile()) {
        try {
          loadDataFile(f, dataArray);
          System.out.printf("%s loaded in %d ms%n", f.getAbsolutePath(), c.tick());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  protected void loadDataFile(File dataFile, String[] dataArray) throws IOException {
    final BufferedReader r = new BufferedReader(new FileReader(dataFile.getAbsolutePath()));
    
    String line = null;
    while((line = r.readLine()) != null) {
      line = line.trim().replaceAll("\\s+", " ");
      int ind = line.indexOf(' ');
      if(ind > 0) {
        try {
          int source = Integer.parseInt(line.substring(0, ind));
          if(indexStart <= source && source < indexEnd) {
            String rest = line.substring(ind + 1).trim();
            if(rest.length() > 0) {
              dataArray[source - indexStart] = rest;
            }
          }
        } catch(Exception e) {
          System.err.println(line);
          e.printStackTrace();
        }
      }
    }
    
    r.close();
  }
  
  public void loadLine1(File dataDir) throws IOException {
    if(dataDir.exists()) {
      loadData(dataDir, line1Data);
    }
  }
  
  public void loadLine2(File dataDir) throws IOException {
    if(dataDir.exists()) {
      loadData(dataDir, line2Data);
    }
  }

  public void loadLine3(File dataDir) throws IOException {
    if(dataDir.exists()) {
      loadData(dataDir, line3Data);
    }
  }
  
  public void loadMedia(File dataDir) throws IOException {
    if(dataDir.exists()) {
      loadData(dataDir, mediaData);
    }
  }
  
  @Override
  protected void customize(TypeaheadElement e) {
    int index = e.getElementId();
    if(indexStart <= index && index < indexEnd) {
      e.setLine1(line1Data[index - indexStart]);
      e.setLine2(line2Data[index - indexStart]);
      e.setLine3(line3Data[index - indexStart]);
      e.setMedia(mediaData[index - indexStart]);
    }
  }
  
  /**
   * <pre>
   * java TypeaheadElementStoreBootstrap -server -Xms2G -Xmx8G \
   *      elementStorePath idStart idCount elementStoreSegmentSizeMB \
   *      termsDir line1Dir line2Dir line3Dir mediaDir
   *      
   * java TypeaheadElementStoreBootstrap -server -Xms2G -Xmx8G \
   *      bootstrap/i001/question/typeahead/element-store 0 5000000 32 \
   *      bootstrap/i001/question/terms \
   *      bootstrap/i001/question/line1 \
   *      bootstrap/i001/question/line2 \
   *      bootstrap/i001/question/line3 \
   *      bootstrap/i001/question/media
   * </pre>
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    File elementStoreDir = new File(args[0]);
    int idStart = Integer.parseInt(args[1]);
    int idCount = Integer.parseInt(args[2]);
    int elementStoreSegMB = Integer.parseInt(args[3]);
    File termsDir = new File(args[4]);
    File line1Dir = new File(args[5]);
    File line2Dir = new File(args[6]);
    File line3Dir = new File(args[7]);
    File mediaDir = new File(args[8]);
    
    Chronos c = new Chronos();
    
    SegmentFactory elementStoreSegFactory = new MemorySegmentFactory();
    ElementFactory<TypeaheadElement> elementFactory = new SimpleTypeaheadElementFactory();
    ElementSerializer<TypeaheadElement> elementSerializer = new TypeaheadElementSerializer();
    ArrayStoreElement<TypeaheadElement> elementStore =
      StoreFactory.createElementStorePartition(elementStoreDir, idStart, idCount, elementStoreSegFactory, elementStoreSegMB, elementSerializer);
    TypeaheadElementStoreBootstrap elementStoreBootstrap =
      new TypeaheadElementStoreBootstrap(elementStore, elementFactory);
    
    elementStoreBootstrap.loadLine1(line1Dir);
    elementStoreBootstrap.loadLine2(line2Dir);
    elementStoreBootstrap.loadLine3(line3Dir);
    elementStoreBootstrap.loadMedia(mediaDir);
    
    TermsScanner scanner = new TermsScanner(termsDir);
    scanner.scan(new CompositeTermsHandler().add(new TermsDedup()).add(elementStoreBootstrap));
    elementStore.sync();
    
    System.out.printf("Bootstrap done in %d ms%n", c.tick());
  }
}
