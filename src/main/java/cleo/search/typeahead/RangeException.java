package cleo.search.typeahead;

/**
 * RangeException
 * 
 * @author jwu
 * @since 03/23, 2011
 */
public class RangeException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  /**
   * Creates a new RangeException.
   * 
   * @param rangeStart
   *          the start of the illegal range.
   * @param rangeEnd
   *          the end of the illegal range.
   */
  public RangeException(int rangeStart, int rangeEnd) {
    super(String.format("Illegal range [%d, %d)", rangeStart, rangeEnd));
  }
  
  /**
   * Creates a new RangeException.
   * 
   * @param rangeStart
   *          the start of the illegal range.
   * @param rangeEnd
   *          the end of the illegal range.
   * @param baseRangeStart
   *          the start of the base range.
   * @param baseRangeEnd
   *          the end of the base range.
   */
  public RangeException(int rangeStart, int rangeEnd, int baseRangeStart, int baseRangeEnd) {
    super(String.format("Illegal range [%d, %d) on [%d, %d)", rangeStart, rangeEnd, baseRangeStart, baseRangeEnd));
  }
}
