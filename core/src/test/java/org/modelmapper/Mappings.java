package org.modelmapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.modelmapper.spi.Mapping;

/**
 * Mapping related test utilities.
 * 
 * @author Jonathan Halterman
 */
public final class Mappings {
  /**
   * Groups mappings by their last destination member name.
   */
  public static <T extends Mapping> Map<String, T> groupByLastMemberName(Collection<T> mappings) {
    Map<String, T> result = new HashMap<String, T>();
    for (T mapping : mappings)
      result.put(mapping.getLastDestinationProperty().getMember().getName(), mapping);
    return result;
  }
}
