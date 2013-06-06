package org.modelmapper.jooq;

import static org.testng.Assert.assertEquals;

import java.sql.DriverManager;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.modelmapper.ModelMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class RecordValueReaderTest {
  private DSLContext ctx;

  @BeforeClass
  protected void beforeClass() throws Exception {
    Class.forName("org.h2.Driver");
    ctx = DSL.using(DriverManager.getConnection("jdbc:h2:mem:test"), SQLDialect.H2);
    ctx.execute("CREATE TABLE orders (customer_street_address varchar(25), customer_address_city varchar(25))");
  }

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

  public void shouldMapFromRecord() throws Exception {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration()
        .setFieldMatchingEnabled(true)
        .addValueReader(new RecordValueReader());

    ctx.execute("INSERT INTO orders values ('123 Main Street', 'SF')");
    Record record = ctx.fetch("select * from orders").get(0);

    Order o = modelMapper.map(record, Order.class);

    assertEquals(o.customer.address.street, "123 Main Street");
    assertEquals(o.customer.address.city, "SF");
  }
}
