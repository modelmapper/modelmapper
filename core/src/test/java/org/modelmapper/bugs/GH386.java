package org.modelmapper.bugs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class GH386 extends AbstractTest {
  static class Outer {
    String name;
    List<Inner> inners;

    public Outer(String name, List<Inner> inners) {
      this.name = name;
      this.inners = inners;
    }
  }

  static class Inner {
    String description;
    String name;

    public Inner(String description, String name) {
      this.description = description;
      this.name = name;
    }
  }

  static class OuterDto {
    private Long id;
    String name;
    List<InnerDto> inners;

    public OuterDto() {
    }

    public OuterDto(Long id, String name, List<InnerDto> inners) {
      this.id = id;
      this.name = name;
      this.inners = inners;
    }
  }

  static class InnerDto {
    private Long id;
    String description;

    public InnerDto() {
    }

    String name;

    public InnerDto(Long id, String description, String name) {
      this.id = id;
      this.description = description;
      this.name = name;
    }
  }

  public void map() {
    Inner inner = new Inner("domain", "domain");
    Outer outer = new Outer("domain", Collections.singletonList(inner));

    InnerDto innerDto = new InnerDto(1L, "dto", "dto");
    @SuppressWarnings("all")
    OuterDto outerDto = new OuterDto(2L,"dto", Arrays.asList(innerDto));

    modelMapper.map(outer, outerDto);
    assertEquals(2L, (long) outerDto.id);
    assertEquals(1, outerDto.inners.size());
    assertEquals(1L, (long) outerDto.inners.get(0).id);
    assertEquals("domain", outerDto.inners.get(0).description);
    assertEquals("domain", outerDto.inners.get(0).name);
  }
}
