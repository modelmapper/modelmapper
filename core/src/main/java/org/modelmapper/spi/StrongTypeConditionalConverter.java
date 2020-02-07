package org.modelmapper.spi;

import org.modelmapper.Converter;

public class StrongTypeConditionalConverter<S, D> implements ConditionalConverter<S, D> {
  public static <S, D> ConditionalConverter<S, D> wrap(Class<S> sourceType,
      Class<D> destinationType, Converter<S, D> converter) {
    return new StrongTypeConditionalConverter<S, D>(sourceType, destinationType, converter);
  }

  private Class<S> sourceType;
  private Class<D> destinationType;
  private Converter<S, D> converter;

  public StrongTypeConditionalConverter(Class<S> sourceType, Class<D> destinationType,
      Converter<S, D> converter) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
    this.converter = converter;
  }

  @Override
  public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
    if (sourceType == this.sourceType && destinationType == this.destinationType)
      return MatchResult.FULL;
    return MatchResult.NONE;
  }

  @Override
  public D convert(MappingContext<S, D> context) {
    return converter.convert(context);
  }
}
