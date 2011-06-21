package org.modelmapper;

import org.testng.annotations.BeforeMethod;

/**
 * @author Jonathan Halterman
 */
public abstract class AbstractTest {
  protected ModelMapper modelMapper;
//  protected ModelMapper looseModelMapper;
//  public static ModelMapper sharedModelMapper;
//  public static ModelMapper sharedLooseModelMapper;
//
//  static {
//    sharedModelMapper = Fixtures.createImmutableModelMapper();
//    sharedLooseModelMapper = Fixtures.createImmutableLooseModelMapper();
//  }

  @BeforeMethod
  protected void initContext() {
    modelMapper = Fixtures.createModelMapper();
    //looseModelMapper = Fixtures.createImmutableLooseModelMapper();
  }
}
