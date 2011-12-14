package cleo.search.filter;

import cleo.search.Element;
import krati.util.Fnv1Hash32;

/**
 * FnvBloomFilterLong - A bloom filter based on 32-Bit FNV-1a Hash.
 * 
 * @author jwu
 * @since 01/21, 2011
 */
public class FnvBloomFilterLong implements BloomFilter<Long> {
  public final static int NUM_BITS = 64; 
  
  private final int prefixLength;
  
  public FnvBloomFilterLong(int prefixLength) {
    this.prefixLength = prefixLength;
  }
  
  @Override
  public final int getNumBits() {
    return NUM_BITS;
  }
  
  @Override
  public Long computeIndexFilter(Element element) {
    long filter = 0;
    for(String s : element.getTerms()) {
      if(s != null) {
        filter |= computeBloomFilter(s, prefixLength);
      }
    }
    
    return filter;
  }
  
  @Override
  public Long computeQueryFilter(String value) {
    return value == null ? 0 : computeBloomFilter(value, prefixLength);
  }
  
  @Override
  public Long computeQueryFilter(String... values) {
    long filter = 0;
    for(String s : values) {
      if(s != null) {
        filter |= computeBloomFilter(s, prefixLength);
      }
    }
    
    return filter;
  }
  
  public static final long computeBloomFilter(String s, int prefixLength) {
    int cnt = Math.min(prefixLength, s.length());
    if (cnt <= 0) return 0;
    
    long filter = 0;
    int bitpos = 0;
    
    long hash = Fnv1Hash32.FNV_BASIS;
    for(int i = 0; i < cnt; i++) {
      char c = s.charAt(i);
      
      hash ^= 0xFF & c;
      hash *= Fnv1Hash32.FNV_PRIME;
      hash &= Fnv1Hash32.BITS_MASK;
      
      hash ^= 0xFF & (c >> 8);
      hash *= Fnv1Hash32.FNV_PRIME;
      hash &= Fnv1Hash32.BITS_MASK;
      
      bitpos = (int)(hash % NUM_BITS);
      if(bitpos < 0) bitpos += NUM_BITS;
      filter |= 1L << bitpos;
    }
    
    return filter;
  }
}
