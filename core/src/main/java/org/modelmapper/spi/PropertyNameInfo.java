package org.modelmapper.spi;

import java.util.List;

/**
 * Encapsulates property name information to be use for determining whether a hierarchy of source
 * and destination properties match.
 *
 *  @author Jonathan Halterman
 */
public interface PropertyNameInfo {
  /**
   * Returns the destination properties.
   */
  List<PropertyInfo> getDestinationProperties();

  /**
   * Returns transformed name tokens for the destination property.
   */
  List<String[]> getDestinationPropertyTokens();

  /**
   * Returns transformed name tokens for the source's declaring class.
   */
  String[] getSourceClassTokens();

  /**
   * Returns the source properties.
   */
  List<PropertyInfo> getSourceProperties();

  /**
   * Returns transformed name tokens for the source property.
   */
  List<String[]> getSourcePropertyTokens();

  /**
   * Returns transformed name tokens for each source property type.
   */
  List<String[]> getSourcePropertyTypeTokens();
}
