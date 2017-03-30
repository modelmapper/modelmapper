package org.modelmapper.jackson;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.NameTokenizers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Test
public class JsonNodeValueReaderTest {
  private final JsonNodeValueReader valueReader = new JsonNodeValueReader();

  private ModelMapper modelMapper;
  private ObjectMapper objectMapper;

  public static class Order {
    public int id;
    public Customer customer;

    public Customer getCustomer() {
      return customer;
    }
  }

  public static class Customer {
    public int id;
    public Address address;

    public Address getAddress() {
      return address;
    }
  }

  public static class Address {
    public String street;
    public String city;

    public void setStreet(String street) {
      this.street = street;
    }

    public void setCity(String city) {
      this.city = city;
    }
  }

  @BeforeMethod
  protected void beforeMethod() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
        .addValueReader(valueReader);

    objectMapper = new ObjectMapper();
  }

  public void shouldMapFromJsonNode() throws Exception {
    String orderJson = "{\"id\":456, \"customer\":{\"id\":789, \"street_address\":\"123 Main Street\", \"address_city\":\"SF\"}}";
    JsonNode node = objectMapper.readTree(orderJson);

    Order order = modelMapper.map(node, Order.class);

    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "123 Main Street");
    assertEquals(order.customer.address.city, "SF");

    String flatOrderJson = "{\"id\":222, \"customer_id\":333, \"customer_street_address\":\"444 Main Street\", \"customer_address_city\":\"LA\"}";
    node = objectMapper.readTree(flatOrderJson);

    order = modelMapper.map(node, Order.class, "flat");

    assertEquals(order.id, 222);
    assertEquals(order.customer.id, 333);
    assertEquals(order.customer.address.street, "444 Main Street");
    assertEquals(order.customer.address.city, "LA");

    assertNotNull(modelMapper.getTypeMap(ObjectNode.class, Order.class));
    modelMapper.validate();
  }

  public void shouldMapWithExplicitMapping() throws Exception {
    String orderJson = "{\"id\":456, \"customer\":{\"id\":789, \"strt\":\"123 Main Street\", \"cty\":\"SF\"}}";
    JsonNode node = objectMapper.readTree(orderJson);

    modelMapper.createTypeMap(node, Order.class).addMappings(new PropertyMap<JsonNode, Order>() {
      @Override
      protected void configure() {
        map(source("customer.strt")).getCustomer().getAddress().setStreet(null);
        map().getCustomer().getAddress().setCity(this.<String>source("customer.cty"));
      }
    });

    Order order = modelMapper.map(node, Order.class);

    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "123 Main Street");
    assertEquals(order.customer.address.city, "SF");

    assertNotNull(modelMapper.getTypeMap(ObjectNode.class, Order.class));
    modelMapper.validate();
  }

  public void shouldGetElements() throws Exception {
    String json = "{\"object\":{\"subkey\":\"subvalue\"}, \"array\":[\"elem1\", \"elem2\"], \"boolean\":true, \"number\":55, \"string\":\"foo\", \"null\": null}";
    JsonNode node = objectMapper.readTree(json);

    ObjectNode objElem = (ObjectNode) valueReader.get(node, "object");
    assertEquals(objElem.get("subkey").asText(), "subvalue");

    ArrayNode arrayElem = (ArrayNode) valueReader.get(node, "array");
    assertEquals(Arrays.asList(arrayElem.get(0).asText(), arrayElem.get(1).asText()),
        Arrays.asList("elem1", "elem2"));

    assertEquals(valueReader.get(node, "boolean"), Boolean.TRUE);
    assertEquals(((Number) valueReader.get(node, "number")).intValue(), 55);
    assertEquals(valueReader.get(node, "string"), "foo");
    assertNull(valueReader.get(node, "null"));
  }

  public void shouldMapAfterMapNull() throws Exception {
    String orderJson = "{\"id\":456, \"customer\":null}";
    JsonNode node = objectMapper.readTree(orderJson);
    Order order = modelMapper.map(node, Order.class);
    assertEquals(order.id, 456);
    assertNull(order.customer);
    assertNull(modelMapper.getTypeMap(ObjectNode.class, Order.class));

    orderJson = "{\"id\":456, \"customer\":{\"id\":789, \"street_address\":\"123 Main Street\", \"address_city\":null}}";
    node = objectMapper.readTree(orderJson);
    order = modelMapper.map(node, Order.class);
    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "123 Main Street");
    assertNull(order.customer.address.city);
    assertNull(modelMapper.getTypeMap(ObjectNode.class, Order.class));

    orderJson = "{\"id\":456, \"customer\":{\"id\":789, \"street_address\":\"123 Main Street\", \"address_city\":\"LA\"}}";
    node = objectMapper.readTree(orderJson);
    order = modelMapper.map(node, Order.class);
    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "123 Main Street");
    assertEquals(order.customer.address.city, "LA");
    assertNotNull(modelMapper.getTypeMap(ObjectNode.class, Order.class));

    modelMapper.validate();
  }
}
