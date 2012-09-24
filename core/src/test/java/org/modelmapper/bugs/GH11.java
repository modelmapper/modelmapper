package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/pull/11
 * 
 * @author by Kay Schubert.
 */
@Test
public class GH11 extends AbstractTest {
  public static class Source {
    String item;
    String itemX;
  }

  public static class Destination {
    String item;
    String itemX;
  }

  public void test() {
    String expectedNameXValue = "someValue";
    Source source = new Source();
    source.itemX = expectedNameXValue;

    Destination result = modelMapper.map(source, Destination.class);

    assertEquals(result.itemX, expectedNameXValue);
  }
}
