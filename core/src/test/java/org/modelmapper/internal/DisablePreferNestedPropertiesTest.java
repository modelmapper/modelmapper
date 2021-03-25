package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.TypeMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class DisablePreferNestedPropertiesTest extends AbstractTest {

  @BeforeMethod
  public void setUp() {
    modelMapper.getConfiguration().setPreferNestedProperties(false);
  }

  public void shouldFlattenWhileDisablePreferNestedProperties() {
    TypeMap<Order, OrderDto> typeMap = modelMapper.typeMap(Order.class, OrderDto.class);
    typeMap.validate();
    assertEquals(typeMap.getMappings().size(), 4);

    OrderDto destination = modelMapper.map(Order.newInstance(), OrderDto.class);
    OrderDto expected = OrderDto.newInstance();
    assertEquals(destination.customerFirstName, expected.customerFirstName);
    assertEquals(destination.customerLastName, expected.customerLastName);
    assertEquals(destination.billingCity, expected.billingCity);
    assertEquals(destination.billingStreet, expected.billingStreet);
  }

  public void shouldMapFromFlatten() {
    TypeMap<OrderDto, Order> typeMap = modelMapper.typeMap(OrderDto.class, Order.class);
    typeMap.validate();
    assertEquals(typeMap.getMappings().size(), 4);

    Order destination = modelMapper.map(OrderDto.newInstance(), Order.class);
    Order expected = Order.newInstance();
    assertEquals(destination.customer.firstName, expected.customer.firstName);
    assertEquals(destination.customer.lastName, expected.customer.lastName);
    assertEquals(destination.billing.city, expected.billing.city);
    assertEquals(destination.billing.street, expected.billing.street);
  }

  public void shouldMapToFlattenParentId() {
    TypeMap<Category, CategoryDto> typeMap = modelMapper.typeMap(Category.class, CategoryDto.class);
    typeMap.validate();
    assertEquals(typeMap.getMappings().size(), 2);

    CategoryDto destination = modelMapper.map(Category.newInstance(), CategoryDto.class);
    assertEquals(destination.id, 2);
    assertEquals(destination.parentId, 1);
  }

  private static class Order {
    Customer customer;
    Address billing;

    private static Order newInstance() {
      Order order = new Order();
      order.customer = new Customer();
      order.customer.firstName = "First";
      order.customer.lastName = "Last";
      order.billing = new Address();
      order.billing.city = "City";
      order.billing.street = "Street";
      return order;
    }
  }

  private static class Customer {
    String firstName;
    String lastName;
  }

  private static class Address {
    String street;
    String city;
  }

  private static class OrderDto {
    String customerFirstName;
    String customerLastName;
    String billingStreet;
    String billingCity;

    private static OrderDto newInstance() {
      OrderDto order = new OrderDto();
      order.customerFirstName = "First";
      order.customerLastName = "Last";
      order.billingCity = "City";
      order.billingStreet = "Street";
      return order;
    }
  }

  private static class Category {
    long id;
    Category parent;

    public static Category newInstance() {
      Category category = new Category();
      category.id = 2;
      category.parent = new Category();
      category.parent.id = 1;
      return category;
    }
  }

  private static class CategoryDto {
    long id;
    long parentId;

    public static CategoryDto newInstance() {
      CategoryDto category = new CategoryDto();
      category.id = 2;
      category.parentId = 1;
      return category;
    }
  }
}
