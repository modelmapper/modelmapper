package org.modelmapper.internal.converter;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;

import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class ConverterStoreTest {
  private ConditionalConverter<?, ?> noneMatchConverter;
  private ConditionalConverter<?, ?> partialMatchConverter;
  private ConditionalConverter<?, ?> fullMatchConverter;

  private ConverterStore store;

  @BeforeMethod
  public void setUp() {
    noneMatchConverter = new ConditionalConverter<Object, Object>() {
      public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        return MatchResult.NONE;
      }

      public Object convert(MappingContext<Object, Object> context) {
        return null;
      }
    };

    partialMatchConverter = new ConditionalConverter<Object, Object>() {
      public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        return MatchResult.PARTIAL;
      }

      public Object convert(MappingContext<Object, Object> context) {
        return null;
      }
    };

    fullMatchConverter = new ConditionalConverter<Object, Object>() {
      public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        return MatchResult.FULL;
      }

      public Object convert(MappingContext<Object, Object> context) {
        return null;
      }
    };
  }

  public void shouldSelectPartialMatchConverter() {
    store = new ConverterStore(Arrays.asList(noneMatchConverter, partialMatchConverter));
    assertSame(partialMatchConverter, store.getFirstSupported(Object.class, Object.class));

    store = new ConverterStore(Arrays.asList(partialMatchConverter, noneMatchConverter));
    assertSame(partialMatchConverter, store.getFirstSupported(Object.class, Object.class));
  }

  public void shouldSelectFirstPartialMatchConverter() {
    ConditionalConverter<?, ?> anotherPartial = new ConditionalConverter<Object, Object>() {
      public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        return MatchResult.PARTIAL;
      }

      public Object convert(MappingContext<Object, Object> context) {
        return null;
      }
    };

    store = new ConverterStore(Arrays.asList(noneMatchConverter, partialMatchConverter, anotherPartial));
    assertSame(partialMatchConverter, store.getFirstSupported(Object.class, Object.class));

    store = new ConverterStore(Arrays.asList(noneMatchConverter, anotherPartial, partialMatchConverter));
    assertSame(anotherPartial, store.getFirstSupported(Object.class, Object.class));
  }

  public void shouldSelectFullMatchConverter() {
    store = new ConverterStore(Arrays.asList(
        noneMatchConverter, partialMatchConverter, fullMatchConverter));
    assertSame(fullMatchConverter, store.getFirstSupported(Object.class, Object.class));

    store = new ConverterStore(Arrays.asList(
        noneMatchConverter, fullMatchConverter, partialMatchConverter));
    assertSame(fullMatchConverter, store.getFirstSupported(Object.class, Object.class));
  }

  public void shouldSelectFirstFullMatchConverter() {
    ConditionalConverter<?, ?> anotherFull = new ConditionalConverter<Object, Object>() {
      public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        return MatchResult.FULL;
      }

      public Object convert(MappingContext<Object, Object> context) {
        return null;
      }
    };

    store = new ConverterStore(Arrays.asList(
        noneMatchConverter, partialMatchConverter, fullMatchConverter, anotherFull));
    assertSame(fullMatchConverter, store.getFirstSupported(Object.class, Object.class));

    store = new ConverterStore(Arrays.asList(
        noneMatchConverter, anotherFull, partialMatchConverter, fullMatchConverter));
    assertSame(anotherFull, store.getFirstSupported(Object.class, Object.class));
  }

  public void shouldSelectNull() {
    store = new ConverterStore(Collections.<ConditionalConverter<?, ?>>singletonList(noneMatchConverter));
    assertNull(store.getFirstSupported(Object.class, Object.class));
  }
}
