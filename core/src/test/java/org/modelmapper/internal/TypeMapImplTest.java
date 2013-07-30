package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.Asserts;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.modelmapper.internal.PropertyInfoImpl.FieldPropertyInfo;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;
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
    return new MappingImpl(Arrays.asList(mutator)) {
      public String toString() {
        return arg;
      }

      @Override
      MappingImpl createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
          List<? extends PropertyInfo> mergedMutators) {
        return null;
      }
    };
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
