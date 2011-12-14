package cleo.search.test.typeahead;

/**
 * TestGenericTypeaheadRollingDisabled
 * 
 * @author jwu
 * @since 03/07, 2011
 */
public class TestGenericTypeaheadRollingDisabled extends TestGenericTypeahead {
  
  @Override
  protected boolean dumpEnabled() {
    return false;
  }
  
  @Override
  protected void setUp() {
    super.setUp();
    if(typeahead != null) {
      typeahead.setRollingEnabled(false);
    }
  }
}
