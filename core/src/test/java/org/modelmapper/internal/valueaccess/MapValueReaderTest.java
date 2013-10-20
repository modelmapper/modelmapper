package org.modelmapper.internal.valueaccess;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * Tests the mapping of a Map to a POJO and visa versa.
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class MapValueReaderTest extends AbstractTest {
  public static class Order {
    int id;
    Customer customer;
  }

  public static class Customer {
    int id;
    Address address;
  }

  public static class Address {
    String street;
    String city;
  }

  public void shouldMapMapToBean() {
    Map<String, Object> orderMap = new HashMap<String, Object>();
    Map<String, Object> customerMap = new HashMap<String, Object>();
    orderMap.put("id", 456);
    orderMap.put("customer", customerMap);
    customerMap.put("id", 789);
    customerMap.put("streetAddress", "1234 Main Street");
    customerMap.put("addressCity", "Seattle");

    Order order = modelMapper.map(orderMap, Order.class);

    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "1234 Main Street");
    assertEquals(order.customer.address.city, "Seattle");

    orderMap.clear();
    orderMap.put("id", 444);
    orderMap.put("customerId", 555);
    orderMap.put("customerStreetAddress", "1234 Main Street");
    orderMap.put("customerAddressCity", "LA");

    order = modelMapper.map(orderMap, Order.class, "flat");

    assertEquals(order.id, 444);
    assertEquals(order.customer.id, 555);
    assertEquals(order.customer.address.street, "1234 Main Street");
    assertEquals(order.customer.address.city, "LA");
  }

  /**
   * Demonstrates that structural information (accessors/mutators) for generic types such as maps is
   * not cached.
   */
  public void shouldMapAnotherMapToBean() {
    Map<String, Object> orderMap = new HashMap<String, Object>();
    orderMap.put("customerStreetAddress", "1234 Main Street");
    orderMap.put("customerCity", "Seattle");

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    Order order = modelMapper.map(orderMap, Order.class);

    assertEquals(order.customer.address.street, "1234 Main Street");
    assertEquals(order.customer.address.city, "Seattle");
  }

  // Disabled until support is added for mapping TO generic types
  @Test(enabled = false)
  public void shouldMapBeanToMap() {
    Order order = new Order();
    order.customer = new Customer();
    order.customer.address = new Address();
    order.customer.address.city = "Seattle";
    order.customer.address.street = "1234 Main Street";

    @SuppressWarnings("unchecked")
    Map<String, Map<String, Map<String, String>>> map = modelMapper.map(order, LinkedHashMap.class);

    modelMapper.validate();
    assertEquals(map.get("customer").get("address").get("city"), order.customer.address.city);
    assertEquals(map.get("customer").get("address").get("street"), order.customer.address.street);
  }
}
