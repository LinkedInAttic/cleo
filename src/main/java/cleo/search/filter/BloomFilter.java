package cleo.search.filter;

import cleo.search.Element;

/**
 * BloomFilter
 * 
 * @author jwu
 * @since 01/12, 2011
 */
public interface BloomFilter<T> {
  
  /**
   * @return the total number of bits of this BloomFilter. 
   */
  public int getNumBits();
  
  /**
   * Computes a bloom filter for querying based on a string.
   * 
   * @param value
   * @return query bloom filter
   */
  public T computeQueryFilter(String value);
  
  /**
   * Computes a bloom filter for querying based on a list of strings.
   * 
   * @param values
   * @return query bloom filter
   */
  public T computeQueryFilter(String... values);
  
  /**
   * Computes a bloom filter for indexing based on the terms of an Element.
   * 
   * @param element
   * @return index bloom filter
   */
  public T computeIndexFilter(Element element);
}
