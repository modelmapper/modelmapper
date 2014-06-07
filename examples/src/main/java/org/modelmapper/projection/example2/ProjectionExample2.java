package org.modelmapper.projection.example2;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;

public class ProjectionExample2 {
  public static void main(String... args) {
    OrderDTO orderDTO = new OrderDTO();
    orderDTO.setStreet("1234 Pike Street");
    orderDTO.setCity("Seattle");

    // Option 1
    ModelMapper modelMapper = new ModelMapper();
    PropertyMap<OrderDTO, Order> orderMap = new PropertyMap<OrderDTO, Order>() {
      protected void configure() {
        map().getAddress().setStreet(source.getStreet());
        map().address.setCity(source.city);
      }
    };

    modelMapper.addMappings(orderMap);
    Order order = modelMapper.map(orderDTO, Order.class);

    assertEquals(order.getAddress().getStreet(), orderDTO.getStreet());
    assertEquals(order.getAddress().getCity(), orderDTO.getCity());

    // Option 2
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    order = modelMapper.map(orderDTO, Order.class);

    assertEquals(order.getAddress().getStreet(), orderDTO.getStreet());
    assertEquals(order.getAddress().getCity(), orderDTO.getCity());
  }
}
