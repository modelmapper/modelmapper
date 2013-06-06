package org.modelmapper.jackson;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Test
public class JsonNodeValueReaderTest {
  String json = "{\"customer\":{\"streetAddress\":\"123 Main Street\", \"customerCity\": \"SF\"}}";

  public static class Order {
    public Customer customer;
  }

  public static class Customer {
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
        .addValueReader(new JsonNodeValueReader());

    JsonNode node = new ObjectMapper().readTree(json);
    Order o = modelMapper.map(node, Order.class);

    assertEquals(o.customer.address.street, "123 Main Street");
    assertEquals(o.customer.address.city, "SF");
  }
}
