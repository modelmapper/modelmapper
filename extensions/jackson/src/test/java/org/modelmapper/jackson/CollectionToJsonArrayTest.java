package org.modelmapper.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test
public class CollectionToJsonArrayTest {
  static class Dest {
    private String target;

    public Dest(String target) {
      this.target = target;
    }

    public String getTarget() {
      return target;
    }

    public void setTarget(String target) {
      this.target = target;
    }
  }

  private ModelMapper modelMapper;

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new JsonNodeValueReader());
    modelMapper.getConfiguration().setProvider(new Provider<Object>() {
      @Override
      public Object get(ProvisionRequest<Object> request) {
        if (request.getRequestedType().equals(ObjectNode.class)) {
          return new ObjectMapper().createObjectNode();
        }
        return null;
      }
    });
    modelMapper.getConfiguration().getConverters().add(0, new PrimitiveJsonNodeConverter());
    modelMapper.getConfiguration().getConverters().add(0, new CollectionToArrayNodeConverter(new ObjectMapper()));
  }

  public void shouldMapObject() throws IOException, ParseException {
    List<Dest> source = Arrays.asList(
        new Dest("target1"),
        new Dest("target2"));
    ArrayNode destination = modelMapper.map(source, ArrayNode.class);
    assertEquals(destination.size(), 2);
    assertEquals(destination.get(0).path("target").asText(), "target1");
    assertEquals(destination.get(1).path("target").asText(), "target2");
  }

  public void shouldMapPrimitive() throws IOException, ParseException {
    ArrayNode destination = modelMapper.map(Arrays.asList(1, 2, 3), ArrayNode.class);
    assertEquals(destination.size(), 3);
    assertEquals(destination.get(0), new IntNode(1));
    assertEquals(destination.get(1), new IntNode(2));
    assertEquals(destination.get(2), new IntNode(3));
  }

  public void shouldMapString() throws IOException, ParseException {
    ArrayNode destination = modelMapper.map(Arrays.asList(1, 2, 3), ArrayNode.class);
    assertEquals(destination.size(), 3);
    assertEquals(destination.get(0), new IntNode(1));
    assertEquals(destination.get(1), new IntNode(2));
    assertEquals(destination.get(2), new IntNode(3));
  }
}
