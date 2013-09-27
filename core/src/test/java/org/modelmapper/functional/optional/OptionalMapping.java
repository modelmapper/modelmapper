/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modelmapper.functional.optional;

import com.google.common.base.Optional;
import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test(groups = "functional")
public class OptionalMapping extends AbstractTest {

  static class Source {

    String value;
  }

  static class SourceOpt {

    Optional<String> value;
  }

  static class Dest {

    String value;
  }

  static class DestOpt {

    Optional<String> value;
  }

  static class DestOptInt {

    Optional<Integer> value;
  }

  public void shouldUseTypeMapPropertyConverter() {
    modelMapper.createTypeMap(Source.class, DestOpt.class).setPropertyConverter(
            new Converter<Object, Optional<Object>>() {
              public Optional<Object> convert(MappingContext<Object, Optional<Object>> context) {
                return Optional.<Object>of("test");
              }
            });

    Source source = new Source();
    source.value = "dummy";
    DestOpt dest = modelMapper.map(source, DestOpt.class);
    Assert.assertNotNull(dest.value);
    Assert.assertTrue(dest.value.isPresent());
    assertEquals("test", dest.value.get());
  }

  public void shouldUseTypeMapPropertyConverterEvenIfSourceIsNull() {
    modelMapper.createTypeMap(Source.class, DestOpt.class).setPropertyConverter(
            new Converter<Object, Optional<Object>>() {
              public Optional<Object> convert(MappingContext<Object, Optional<Object>> context) {
                return Optional.absent();
              }

            });

    Source source = new Source();
    DestOpt dest = modelMapper.map(source, DestOpt.class);
    Assert.assertNotNull(dest.value);
    Assert.assertFalse(dest.value.isPresent());
  }

  public void shouldMapToOptionalAbsent() {
    Source source = new Source();
    source.value = null;
    DestOpt dest = modelMapper.map(source, DestOpt.class);
    modelMapper.validate();
    Assert.assertNotNull(dest.value);
    Assert.assertFalse(dest.value.isPresent());
  }

  public void shouldMapToOptionalAbsentDifferentType() {
    Source source = new Source();
    source.value = null;
    DestOptInt dest = modelMapper.map(source, DestOptInt.class);
    modelMapper.validate();
    Assert.assertNotNull(dest.value);
    Assert.assertFalse(dest.value.isPresent());
  }

  public void shouldMapToOptionalPresent() {
    Source source = new Source();
    source.value = "name";
    DestOpt dest = modelMapper.map(source, DestOpt.class);
    modelMapper.validate();
    Assert.assertNotNull(dest.value);
    Assert.assertTrue(dest.value.isPresent());
    Assert.assertEquals(source.value, dest.value.get());
  }

  public void shouldMapToOptionalDifferentType() {
    Source source = new Source();
    source.value = "100";
    DestOptInt dest = modelMapper.map(source, DestOptInt.class);
    modelMapper.validate();
    Assert.assertNotNull(dest.value);
    Assert.assertTrue(dest.value.isPresent());
    Assert.assertEquals(Integer.class, dest.value.get().getClass());
    Assert.assertEquals(Integer.valueOf(source.value), dest.value.get());
  }

}
