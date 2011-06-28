package org.modelmapper;

import org.testng.annotations.BeforeMethod;

/**
 * @author Jonathan Halterman
 */
public abstract class AbstractTest {
  protected ModelMapper modelMapper;

  @BeforeMethod
  protected void initContext() {
    modelMapper = Fixtures.createModelMapper();
  }
}
