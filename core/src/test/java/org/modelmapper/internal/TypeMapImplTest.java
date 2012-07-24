package org.modelmapper.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.internal.PropertyInfoImpl.FieldPropertyInfo;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * @author Jonathan Halterman
 */
@Test
public class TypeMapImplTest {
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
    TypeMapImpl<Object, Object> map = new TypeMapImpl<Object, Object>(null, null,
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
}
