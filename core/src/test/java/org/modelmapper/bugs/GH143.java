package org.modelmapper.bugs;

import static org.testng.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.modelmapper.convention.NameTokenizers;
import org.testng.annotations.Test;

/**
 * @author Andrei Zinca
 */
@Test
public class GH143 extends AbstractTest {

  static class Dummy {
    public void setX(String x) { // do not remove
    }
  }

  public void testMapping() {

    modelMapper.getConfiguration().setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

    Map<String, String> m = new HashMap<String, String>();
    m.put("__foo", "bar");

    try {
      modelMapper.map(m, Dummy.class);
    } catch (Exception e) {
      fail("should not throw exception", e);
    }
  }

}
