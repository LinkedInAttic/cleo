package cleo.search.test.typeahead;

/**
 * TestGenericTypeaheadRollingEnabled
 * 
 * @author jwu
 * @since 03/07, 2011
 */
public class TestGenericTypeaheadRollingEnabled extends TestGenericTypeahead {
  
  @Override
  protected boolean dumpEnabled() {
    return false;
  }
  
  @Override
  protected void setUp() {
    super.setUp();
    if(typeahead != null) {
      typeahead.setRollingEnabled(true);
    }
  }
}
