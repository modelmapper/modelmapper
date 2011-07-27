package org.modelmapper.functional.iterable;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Maps collections with elements whose types vary. Consider supporting...
 * 
 * Taken from http://stackoverflow.com/questions/1916786/
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class DifferentTypedElements extends AbstractTest {
  static class Order {
    List<Product> products = new ArrayList<Product>();
  }

  static class Product {
    String name;

    Product(String name) {
      this.name = name;
    }
  }

  static class OrderDTO {
    List<String> products;
  }

  /**
   * <pre>
   * Order/products/name -> OrderDTO products[]
   * </pre>
   */
  public void shouldPerformCollectionConversion() {
    Order order = new Order();
    order.products.add(new Product("socks"));
    order.products.add(new Product("shoes"));
  }
}
