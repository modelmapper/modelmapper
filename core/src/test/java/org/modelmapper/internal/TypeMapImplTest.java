package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.modelmapper.Asserts;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.modelmapper.internal.PropertyInfoImpl.FieldPropertyInfo;
import org.modelmapper.internal.util.JavaVersions;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class TypeMapImplTest {
  enum Color {
    Red, Blue
  }

  MappingImpl mapping(final String arg) throws Exception {
    Mutator mutator = new FieldPropertyInfo(ArrayList.class,
        ArrayList.class.getDeclaredField("size"), arg) {
    };
    return new MappingImpl(Collections.singletonList(mutator)) {
      public String toString() {
        return arg;
      }

      @Override
      public InternalMapping createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
          List<? extends PropertyInfo> mergedMutators) {
        return null;
      }

      @Override
      public Class<?> getSourceType() {
        return null;
      }
    };
  }

  @BeforeMethod
  public void setup() {
    if (JavaVersions.getMajorVersion() >= 16) {
      throw new SkipException("Required java < 16");
    }
  }

  public void shouldSortMappings() throws Exception {
    TypeMapImpl<Object, Object> map = new TypeMapImpl<Object, Object>(null, null, null,
        new InheritingConfiguration(), null);
    map.addMapping(mapping("a.x"));
    map.addMapping(mapping("a.b.x.e.f"));
    map.addMapping(mapping("a.g.r"));
    map.addMapping(mapping("a.b.x.e"));
    map.addMapping(mapping("a.b.x"));

    List<Mapping> m = map.getMappings();
    assertEquals(m.get(0).toString(), "a.b.x");
    assertEquals(m.get(1).toString(), "a.b.x.e");
    assertEquals(m.get(2).toString(), "a.b.x.e.f");
    assertEquals(m.get(3).toString(), "a.g.r");
    assertEquals(m.get(4).toString(), "a.x");
  }

  public void shouldThrowWhenSourceTypeIsEnum() {
    try {
      TypeMapImpl<Color, String> map = new TypeMapImpl<Color, String>(Color.class, String.class,
          null, new InheritingConfiguration(), null);
      map.addMappings(new PropertyMap<Color, String>() {
        protected void configure() {
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) Cannot create mapping for enum.");
      return;
    }

    fail();
  }

  public void shouldThrowWhenDestinationTypeIsEnum() {
    try {
      TypeMapImpl<String, Color> map = new TypeMapImpl<String, Color>(String.class, Color.class,
          null, new InheritingConfiguration(), null);
      map.addMappings(new PropertyMap<String, Color>() {
        protected void configure() {
        }
      });
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "1) Cannot create mapping for enum.");
      return;
    }

    fail();
  }
}
