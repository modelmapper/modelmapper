package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.modelmapper.TypeToken;
import org.testng.annotations.Test;

@Test
public class GH547 extends AbstractTest {

  public void shouldMap() {
    Map<Integer, List<Product>> source = Collections.singletonMap(
        1, Collections.singletonList(new Product()));
    Type destType = new TypeToken<Map<Integer, List<ProductVo>>>(){}.getType();
    Map<Integer, List<ProductVo>> result = modelMapper.map(source, destType);
    assertEquals(result.get(1).get(0).getClass(), ProductVo.class);
    assertEquals(result.get(1).get(0).id, 10);
  }

  private static class Product {
    long id = 10;
  }

  private static class ProductVo {
    long id;
  }
}
