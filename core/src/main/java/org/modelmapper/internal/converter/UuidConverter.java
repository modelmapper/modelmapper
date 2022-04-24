package org.modelmapper.internal.converter;

import java.util.UUID;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Converts objects to UUID.
 */
class UuidConverter implements ConditionalConverter<Object, UUID> {

  public UUID convert(MappingContext<Object, UUID> context) {
    Object source = context.getSource();
    if (source == null) {
        return null;
    }

    Class<?> sourceType = context.getSourceType();
    if (isCharArray(sourceType)) {
      return UUID.fromString(new String((char[]) source));
    }
    return UUID.fromString(source.toString());
  }

  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    boolean destMatch = destinationType == UUID.class;
    return destMatch ? isCharArray(sourceType) || sourceType == String.class
        ? MatchResult.FULL
        : MatchResult.PARTIAL
        : MatchResult.NONE;
  }

  private boolean isCharArray(Class<?> sourceType) {
    return sourceType.isArray() && (sourceType.getComponentType() == Character.TYPE
        || sourceType.getComponentType() == Character.class);
  }
}