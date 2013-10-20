package org.modelmapper;

import org.modelmapper.config.Configuration;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.convention.MatchingStrategies;

/**
 * Utilities for constructing test fixtures.
 * 
 * @author Jonathan Halterman
 */
public final class Fixtures {
  private Fixtures() {
  }

  static class ImmutableModelMapper extends ModelMapper {
    private static final String ERROR_MSG = "ModelMapper for functional tests cannot be configured";

    @Override
    public Configuration getConfiguration() {
      throw new IllegalStateException(ERROR_MSG);
    }

    @Override
    public <S, D> TypeMap<S, D> addMappings(PropertyMap<S, D> propertyMap) {
      throw new IllegalStateException(ERROR_MSG);
    }

    private Configuration getConfig() {
      return super.getConfiguration();
    }
  }

  /**
   * Creates an immutable ModelMapper with field matching enabled and package private field and
   * method access levels.
   */
  public static ImmutableModelMapper createImmutableModelMapper() {
    ImmutableModelMapper modelMapper = new ImmutableModelMapper();
    modelMapper.getConfig()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE)
        .setMethodAccessLevel(AccessLevel.PACKAGE_PRIVATE);
    return modelMapper;
  }

  /**
   * Creates an immutable ModelMapper with field matching enabled, package private field and method
   * access levels, and the loose matching strategy configured.
   */
  public static ImmutableModelMapper createImmutableLooseModelMapper() {
    ImmutableModelMapper modelMapper = createImmutableModelMapper();
    modelMapper.getConfig().setMatchingStrategy(MatchingStrategies.LOOSE);
    return modelMapper;
  }

  /**
   * Creates a ModelMapper with field matching enabled and package private field and method access
   * levels.
   */
  public static ModelMapper createModelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setFieldAccessLevel(AccessLevel.PACKAGE_PRIVATE)
        .setMethodAccessLevel(AccessLevel.PACKAGE_PRIVATE);

    return modelMapper;
  }
}
