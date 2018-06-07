package org.modelmapper.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test
public class JsonArrayToCollectionTest {
  static class Dest {
    private String target;
    private Date date;

    public String getTarget() {
      return target;
    }

    public void setTarget(String target) {
      this.target = target;
    }

    public Date getDate() {
      return date;
    }

    public void setDate(Date date) {
      this.date = date;
    }
  }

  private ModelMapper modelMapper;

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().addValueReader(new JsonNodeValueReader());
    modelMapper.getConfiguration().getConverters().add(0, new PrimitiveJsonNodeConverter());
    modelMapper.getConfiguration().getConverters().add(0, new ArrayNodeToCollectionConverter());
  }

  public void shouldMapObject() throws IOException, ParseException {
    final Converter<String, Date> dateConverter = new Converter<String, Date>() {
      @Override
      public Date convert(MappingContext<String, Date> context) {
        try {
          return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(context.getSource());
        } catch (ParseException e) {
          return null;
        }
      }
    };
    modelMapper.addMappings(new PropertyMap<ObjectNode, Dest>() {
      @Override
      protected void configure() {
        map(source("source")).setTarget(null);
        using(dateConverter).map(source("date")).setDate(null);
      }
    });

    JsonNode source = new ObjectMapper().readTree(
        "[{\"source\":\"a\",\"date\":\"2018-06-18 08:00:00\"},{\"source\":\"a1\",\"date\":\"2018-06-18 08:00:00\"}]");
    @SuppressWarnings("unchecked")
    List<Dest> destination = modelMapper.map(source, new TypeToken<List<Dest>>() {}.getType());
    assertEquals(destination.size(), 2);
    assertEquals(destination.get(0).date, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        .parse("2018-06-18 08:00:00"));
    assertEquals(destination.get(0).target, "a");
    assertEquals(destination.get(1).date, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        .parse("2018-06-18 08:00:00"));
    assertEquals(destination.get(1).target, "a1");
  }

  public void shouldMapPrimitive() {
    JsonNode source = new ObjectMapper().createArrayNode()
        .add(1)
        .add(2)
        .add(3);
    List<Integer> destination = modelMapper.map(source, new TypeToken<List<Integer>>() {}.getType());
    assertEquals(destination.size(), 3);
    assertEquals(destination, Arrays.asList(1, 2, 3));
  }

  public void shouldMapString() {
    JsonNode source = new ObjectMapper().createArrayNode()
        .add(new TextNode("test1"))
        .add(new TextNode("test2"))
        .add(new TextNode("test3"));
    List<String> destination = modelMapper.map(source, new TypeToken<List<String>>() {}.getType());
    assertEquals(destination.size(), 3);
    assertEquals(destination, Arrays.asList("test1", "test2", "test3"));
  }

  public void shouldMapStringToNumber() {
    JsonNode source = new ObjectMapper().createArrayNode()
        .add(new TextNode("1"))
        .add(new TextNode("2"))
        .add(new TextNode("3"));
    List<Integer> destination = modelMapper.map(source, new TypeToken<List<Integer>>() {}.getType());
    assertEquals(destination.size(), 3);
    assertEquals(destination, Arrays.asList(1, 2, 3));
  }

  public void shouldMapNumberToString() {
    JsonNode source = new ObjectMapper().createArrayNode()
        .add(new IntNode(1))
        .add(new IntNode(2))
        .add(new IntNode(3));
    List<String> destination = modelMapper.map(source, new TypeToken<List<String>>() {}.getType());
    assertEquals(destination.size(), 3);
    assertEquals(destination, Arrays.asList("1", "2", "3"));
  }
}
