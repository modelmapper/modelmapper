package org.modelmapper.jackson;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Test
public class JsonNodeValueReaderTest {
  private final JsonNodeValueReader valueReader = new JsonNodeValueReader();

  public static class Order {
    public int id;
    public Customer customer;
  }

  public static class Customer {
    public int id;
    public Address address;
  }

  public static class Address {
    public String street;
    public String city;
  }

  public void shouldMapFromJsonNode() throws Exception {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setMatchingStrategy(MatchingStrategies.LOOSE)
        .addValueReader(valueReader);

    String orderJson = "{\"id\":456, \"customer\":{\"id\":789, \"streetAddress\":\"123 Main Street\", \"customerCity\":\"SF\"}}";
    JsonNode node = new ObjectMapper().readTree(orderJson);
    Order order = modelMapper.map(node, Order.class);

    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "123 Main Street");
    assertEquals(order.customer.address.city, "SF");
  }

  public void shouldGetElements() throws Exception {
    String json = "{\"object\":{\"subkey\":\"subvalue\"}, \"array\":[\"elem1\", \"elem2\"], \"boolean\":true, \"number\":55, \"string\":\"foo\", \"null\": null}";
    JsonNode node = new ObjectMapper().readTree(json);

    ObjectNode objElem = (ObjectNode) valueReader.get(node, "object");
    assertEquals(objElem.get("subkey").asText(), "subvalue");

    ArrayNode arrayElem = (ArrayNode) valueReader.get(node, "array");
    assertEquals(Arrays.asList(arrayElem.get(0).asText(), arrayElem.get(1).asText()),
        Arrays.asList("elem1", "elem2"));

    assertEquals(valueReader.get(node, "boolean"), true);
    assertEquals(((Number) valueReader.get(node, "number")).intValue(), 55);
    assertEquals(valueReader.get(node, "string"), "foo");
    assertNull(valueReader.get(node, "null"));
  }
}
