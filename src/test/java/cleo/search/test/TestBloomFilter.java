package cleo.search.test;

import cleo.search.Element;
import cleo.search.SimpleElement;
import cleo.search.filter.BloomFilter;
import cleo.search.filter.FnvBloomFilter;
import cleo.search.filter.FnvBloomFilterLong;
import junit.framework.TestCase;

/**
 * TestBloomFilter
 * 
 * @author jwu
 * @since 01/14, 2011
 */
public class TestBloomFilter extends TestCase {
  
  public TestBloomFilter(String name) {
    super(name);
  }
  
  protected Element createElement(int id, String... terms) {
    Element elem = new SimpleElement(id);
    elem.setTimestamp(System.currentTimeMillis());
    elem.setTerms(terms);
    return elem;
  }
  
  protected BloomFilter<Integer> createBloomFilter(int prefixLength) {
    return new FnvBloomFilter(prefixLength);
  }
  
  private void doBasicOperations(int prefixLength) {
    String term;
    Element elem;
    int indexFilter, queryFilter;
    
    BloomFilter<Integer> bf = createBloomFilter(prefixLength);
    
    term = "Bloom";
    elem = createElement(1, term);
    indexFilter = bf.computeIndexFilter(elem);
    queryFilter = bf.computeQueryFilter(term);
    assertEquals(queryFilter, (indexFilter & queryFilter));
    
    term = "Filter";
    elem = createElement(1, term);
    indexFilter = bf.computeIndexFilter(elem);
    queryFilter = bf.computeQueryFilter(term);
    assertEquals(queryFilter, (indexFilter & queryFilter));
    
    String[] terms = { "Bloom", "Filter" };
    elem = createElement(2, terms);
    indexFilter = bf.computeIndexFilter(elem);
    queryFilter = bf.computeQueryFilter(terms);
    assertEquals(queryFilter, (indexFilter & queryFilter));
  }
  
  private void doFiltering(int prefixLength, String[] indexTerms, String[] queryTerms) {
    BloomFilter<Integer> bf = createBloomFilter(prefixLength);
    
    Element elem = createElement(2, indexTerms);
    int indexFilter = bf.computeIndexFilter(elem);
    int queryFilter = bf.computeQueryFilter(queryTerms);
    assertEquals(queryFilter, (indexFilter & queryFilter));
  }
  
  public void testBasicOperations() {
    doBasicOperations(0);
    doBasicOperations(1);
    doBasicOperations(2);
    doBasicOperations(3);
    doBasicOperations(4);
    doBasicOperations(5);
    doBasicOperations(10);
  }
  
  public void testNameFiltering() {
    String[] indexTerms, queryTerms = null;
    
    indexTerms = new String[] {"jingwei", "wu", "linkedin"};
    queryTerms = new String[] {"j", "wu" };
    doFiltering(3, indexTerms, queryTerms);
    
    queryTerms = new String[] {"jing", "wu" };
    doFiltering(3, indexTerms, queryTerms);
    
    queryTerms = new String[] {"wu", "ji" };
    doFiltering(3, indexTerms, queryTerms);
    
    queryTerms = new String[] {"jingwei", "linked" };
    doFiltering(3, indexTerms, queryTerms);
    
    indexTerms = new String[] {"jiong", "wang", "linkedin"};
    queryTerms = new String[] {"j", "wa" };
    doFiltering(3, indexTerms, queryTerms);
    
    queryTerms = new String[] {"jiong", "w" };
    doFiltering(3, indexTerms, queryTerms);
    
    queryTerms = new String[] {"jion", "wan" };
    doFiltering(3, indexTerms, queryTerms);
    
    queryTerms = new String[] {"wang", "linkedin" };
    doFiltering(3, indexTerms, queryTerms);
  }
  
  public void testFnvBloomFilter() {
    int indexFilter, queryFilter;
    BloomFilter<Integer> filter = new FnvBloomFilter(2);
    
    Element element = createElement(1, new String[] {"professional", "social", "network"});
    indexFilter = filter.computeIndexFilter(element);
    
    queryFilter = filter.computeQueryFilter(new String[]{"professional", "social", "network"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"social", "network"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"professional", "network"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"professional", "social"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"pro", "net"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"p", "n"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
  }
  
  public void testFnvBloomFilterLong() {
    long indexFilter, queryFilter;
    BloomFilter<Long> filter = new FnvBloomFilterLong(2);
    
    Element element = createElement(1, new String[] {"professional", "social", "network"});
    indexFilter = filter.computeIndexFilter(element);
    
    queryFilter = filter.computeQueryFilter(new String[]{"professional", "social", "network"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"social", "network"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"professional", "network"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"professional", "social"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"pro", "net"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
    
    queryFilter = filter.computeQueryFilter(new String[]{"p", "n"});
    assertEquals(queryFilter, (queryFilter & indexFilter));
  }
}
