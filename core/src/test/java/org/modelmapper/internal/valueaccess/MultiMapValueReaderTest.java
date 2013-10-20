package org.modelmapper.internal.valueaccess;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.NameTokenizers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests mapping of MultiMap style objects, such as JDBI query results.
 * 
 * @author Jonathan Halterman
 */
@Test
public class MultiMapValueReaderTest {
  private ModelMapper modelMapper;
  private List<Map<String, Object>> orderRecords;

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

  @BeforeMethod
  protected void beforeMethod() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setSourceNameTokenizer(NameTokenizers.UNDERSCORE);

    orderRecords = new ArrayList<Map<String, Object>>();
    Map<String, Object> orderRecord = new HashMap<String, Object>();
    orderRecord.put("id", 456);
    orderRecord.put("customer_id", 789);
    orderRecord.put("customer_street_address", "123 Main Street");
    orderRecord.put("customer_address_city", "SF");
    orderRecords.add(orderRecord);
  }

  public void shouldMapFromMultiMap() throws Exception {
    List<Order> orders = modelMapper.map(orderRecords, new TypeToken<List<Order>>() {
    }.getType());
    Order order = orders.get(0);

    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "123 Main Street");
    assertEquals(order.customer.address.city, "SF");
  }
}
