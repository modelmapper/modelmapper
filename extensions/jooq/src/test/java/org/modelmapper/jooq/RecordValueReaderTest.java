package org.modelmapper.jooq;

import static org.testng.Assert.assertEquals;

import java.sql.DriverManager;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.NameTokenizers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class RecordValueReaderTest {
  private DSLContext ctx;
  private ModelMapper modelMapper;

  @BeforeClass
  protected void beforeClass() throws Exception {
    Class.forName("org.h2.Driver");
    ctx = DSL.using(DriverManager.getConnection("jdbc:h2:mem:test"), SQLDialect.H2);
    ctx.execute("CREATE TABLE orders (id int(11), customer_id int(11), customer_street_address varchar(25), customer_address_city varchar(25), customer_address_zip varchar(10))");
    ctx.execute("INSERT INTO orders values (456, 789, '123 Main Street', 'SF', null)");
  }

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
    public String zip;

    public void setStreet(String street) {
      this.street = street;
    }
  }

  @BeforeMethod
  protected void beforeMethod() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .setSourceNameTokenizer(NameTokenizers.UNDERSCORE)
        .addValueReader(new RecordValueReader());
  }

  public void shouldMapFromRecord() throws Exception {
    Record record = ctx.fetch("select * from orders").get(0);

    Order order = modelMapper.map(record, Order.class);

    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "123 Main Street");
    assertEquals(order.customer.address.city, "SF");
    assertEquals(order.customer.address.zip, null);
  }

  public void shouldMapWithExplicitMapping() throws Exception {
    Record record = ctx.fetch("select * from orders").get(0);

    modelMapper.createTypeMap(record, Order.class).addMappings(new PropertyMap<Record, Order>() {
      @Override
      protected void configure() {
        map(source("customer_street_address")).getCustomer().getAddress().setStreet(null);
      }
    });

    Order order = modelMapper.map(record, Order.class);

    assertEquals(order.id, 456);
    assertEquals(order.customer.id, 789);
    assertEquals(order.customer.address.street, "123 Main Street");
    assertEquals(order.customer.address.city, "SF");
    assertEquals(order.customer.address.zip, null);
  }
}
