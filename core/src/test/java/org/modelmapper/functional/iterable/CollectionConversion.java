package org.modelmapper.functional.iterable;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class CollectionConversion extends AbstractTest {
  static class Order {
    List<Product> products = new ArrayList<Product>();
  }

  static class Product {
    String product;

    Product(String product) {
      this.product = product;
    }
  }

  static class OrderDTO {
    List<String> products;
  }

  public void shouldPerformCollectionConversion() {
    Order order = new Order();
    order.products.add(new Product("socks"));
    order.products.add(new Product("shoes"));
    
    OrderDTO dto = modelMapper.map(order, OrderDTO.class);
    int i = 0;
  }
}
