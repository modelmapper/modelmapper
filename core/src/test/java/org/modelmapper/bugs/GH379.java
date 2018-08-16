package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import org.modelmapper.AbstractTest;
import org.modelmapper.TypeToken;
import org.testng.annotations.Test;

@Test
public class GH379 extends AbstractTest {
  static class PageModel<T> {
    List<T> items;
  }

  static class SubjectModel {
    String name;
  }

  static class Page<T> {
    List<T> items;

    public Page(List<T> items) {
      this.items = items;
    }
  }

  static class Subject {
    String name;

    public Subject(String name) {
      this.name = name;
    }
  }

  public void shouldMapGeneric() {
    List<Subject> items = Arrays.asList(new Subject("foo"), new Subject("bar"));
    Page<Subject> page = new Page<Subject>(items);

    Type destinationType = new TypeToken<PageModel<SubjectModel>>(){}.getType();
    PageModel<SubjectModel> destination = modelMapper.map(page, destinationType);
    assertEquals(2, destination.items.size());
    assertEquals(SubjectModel.class, destination.items.get(0).getClass());
  }
}
