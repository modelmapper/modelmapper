package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class GH408 extends AbstractTest {
  static class Category {
    Long id;
    Category parent;
  }

  static class CategoryDto {
    Long id;
    Long parentId;
  }

  public void shouldMap() {
    Category parent = new Category();
    parent.id = 1L;
    Category src = new Category();
    src.id = 2L;
    src.parent = parent;

    CategoryDto dest = modelMapper.map(src, CategoryDto.class);
    assertEquals(dest.id, Long.valueOf(2L));
    assertEquals(dest.parentId, Long.valueOf(1L));
  }
}
