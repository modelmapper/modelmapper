package org.modelmapper.gettingstarted;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;

public class GettingStartedExample {
  public static void main(String... args) throws Exception {
    Order order = new Order();
    Customer customer = new Customer();
    Name name = new Name();
    name.setFirstName("Joe");
    name.setLastName("Pascal");
    customer.setName(name);
    order.setCustomer(customer);
    Address address = new Address();
    address.setStreet("100 Pike Street");
    address.setCity("Seattle");
    order.setBillingAddress(address);

    ModelMapper modelMapper = new ModelMapper();
    OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

    Assert.isTrue(orderDTO.getCustomerFirstName().equals(
        order.getCustomer().getName().getFirstName()));
    Assert.isTrue(orderDTO.getCustomerLastName()
        .equals(order.getCustomer().getName().getLastName()));
    Assert.isTrue(orderDTO.getBillingStreet().equals(order.getBillingAddress().getStreet()));
    Assert.isTrue(orderDTO.getBillingCity().equals(order.getBillingAddress().getCity()));
  }
}
