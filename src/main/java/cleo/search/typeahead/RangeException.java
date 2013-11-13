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
