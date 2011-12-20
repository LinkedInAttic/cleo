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

package cleo.search.test.term;

import java.util.Arrays;

import cleo.search.util.TermsDedup;
import junit.framework.TestCase;

/**
 * TestTermsHandler
 * 
 * @author jwu
 * @since 02/17, 2011
 */
public class TestTermsHandler extends TestCase {

  public void testTermsDedup() throws Exception {
    TermsDedup dedup = new TermsDedup();
    
    int source = 0;
    String[] terms;
    String[] results;
    String[] expected;
    
    terms = new String[] { };
    results = dedup.handle(source, terms);
    assertTrue(Arrays.equals(terms, results));
    
    terms = new String[] { "ibm" };
    results = dedup.handle(source, terms);
    assertTrue(Arrays.equals(terms, results));
    
    terms = new String[] { "ibm", "global" };
    results = dedup.handle(source, terms);
    assertTrue(Arrays.equals(terms, results));
    
    expected = new String[] { "ibm", "global" };
    terms = new String[] { "ibm", "global", "ibm" };
    results = dedup.handle(source, terms);
    assertTrue(Arrays.equals(expected, results));
    
    expected = new String[] { "ibm" };
    terms = new String[] { "ibm", "ibm" };
    results = dedup.handle(source, terms);
    assertTrue(Arrays.equals(expected, results));
  }
}
