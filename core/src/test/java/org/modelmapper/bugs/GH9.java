package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/9
 * 
 * Reported by andy-m.
 */
@Test
public class GH9 extends AbstractTest {
  public static class TestItem {
    TestItemConfiguration testItemConfiguration;
  }

  static class TestItemConfiguration {
    SomeConfiguration someConfiguration;
  }

  static class SomeConfiguration {
    String someProperty;
    boolean someOtherProperty;
  }

  static class TestItemDTO {
    TestItemConfigurationDTO testItemConfiguration;
  }

  static class TestItemConfigurationDTO {
    SomeConfigurationDTO someConfiguration;
  }

  static class SomeConfigurationDTO {
    String someProperty;
  }

  public void testValuesInDestinationRemainUnchangedUnlessTheyArePresentInSource() {
    String expectedValue = "someValue";
    TestItemDTO source = new TestItemDTO();
    source.testItemConfiguration = new TestItemConfigurationDTO();
    source.testItemConfiguration.someConfiguration = new SomeConfigurationDTO();
    source.testItemConfiguration.someConfiguration.someProperty = "someValue";

    TestItem dest = new TestItem();
    TestItemConfiguration destItemConfig = new TestItemConfiguration();
    SomeConfiguration destSomeConfig = new SomeConfiguration();
    dest.testItemConfiguration = destItemConfig;
    dest.testItemConfiguration.someConfiguration = destSomeConfig;
    dest.testItemConfiguration.someConfiguration.someProperty = "someValueX";
    dest.testItemConfiguration.someConfiguration.someOtherProperty = true;

    modelMapper.map(source, dest);

    assertEquals(expectedValue, dest.testItemConfiguration.someConfiguration.someProperty);
    assertEquals(destItemConfig, dest.testItemConfiguration);
    assertEquals(destSomeConfig, dest.testItemConfiguration.someConfiguration);
    assertTrue(dest.testItemConfiguration.someConfiguration.someOtherProperty);
  }
}
