package org.modelmapper;

/**
 * Simple interface for extensions that can be registered with {@link ModelMapper}
 * to provide some extensions.
 *
 *  @author Chun Han Hsiao
 */
public interface Module {
  /**
   * Setup the ModelMapper for external functionality
   *
   * @param modelMapper a {@link ModelMapper} instance
   */
  void setupModule(ModelMapper modelMapper);
}
