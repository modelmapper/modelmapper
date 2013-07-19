package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.modelmapper.config.Configuration.AccessLevel;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class InheritingConfigurationTest {
  public void testHashCode() {
    InheritingConfiguration config1 = new InheritingConfiguration();
    InheritingConfiguration config2 = new InheritingConfiguration();

    assertEquals(config1.hashCode(), config2.hashCode());
    config1.setFieldMatchingEnabled(true);
    assertFalse(config1.hashCode() == config2.hashCode());
    config2.setFieldMatchingEnabled(true);
    assertEquals(config1.hashCode(), config2.hashCode());
    config2.setMethodAccessLevel(AccessLevel.PRIVATE);
    assertFalse(config1.hashCode() == config2.hashCode());
  }

  // public void shouldMergeInheritedConfiguration() {
  // ModelMapper modelMapper = new ModelMapper();
  // MatchingConfiguration modelMapperConfig = modelMapper.getConfiguration();
  // MatchingConfiguration personMapConfig = modelMapper.getTypeMap(Object.class, String.class)
  // .getConfiguration();
  //
  // modelMapperConfig.setFieldAccessLevel(AccessLevel.Private);
  // assertEquals(modelMapperConfig.getFieldAccessLevel(), personMapConfig.getFieldAccessLevel());
  //
  // personMapConfig.setFieldAccessLevel(AccessLevel.Public);
  // assertFalse(modelMapperConfig.getFieldAccessLevel().equals(
  // personMapConfig.getFieldAccessLevel()));
  // }

  public void testEquals() {
    InheritingConfiguration config1 = new InheritingConfiguration();
    InheritingConfiguration config2 = new InheritingConfiguration();

    assertEquals(config1, config2);
    config1.setFieldMatchingEnabled(true);
    assertFalse(config1.equals(config2));
    config2.setFieldMatchingEnabled(true);
    assertEquals(config1, config2);
    config2.setMethodAccessLevel(AccessLevel.PRIVATE);
    assertFalse(config1.equals(config2));
  }
  
  public void testFullMatchingRequiredDefualtsToFalse() {
	  InheritingConfiguration config = new InheritingConfiguration();
	  assertFalse(config.isFullTypeMatchingRequired());
  }
  
  public void testFullMatchingRequiredIsInherited() {
	  InheritingConfiguration originConfig = new InheritingConfiguration();
	  originConfig.setFullTypeMatchingRequired(true);
	  InheritingConfiguration inheritingConfig = new InheritingConfiguration(originConfig, true);
	  assertTrue(inheritingConfig.isFullTypeMatchingRequired());
  }
  
}
