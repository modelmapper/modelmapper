package org.modelmapper.performance;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;

import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.Assert;

/**
 * Semi-useless micro-benchmark comparing object mapping performance in ModelMapper to Dozer and
 * Orika. Based on Bob Lee's Guice micro-benchmark.
 * 
 * @author Jonathan Halterman
 */
public class Comparison {
  static final DecimalFormat format = new DecimalFormat();

  interface OrderMapper {
    OrderDTO map(Order source);
  }

  public static void main(String... args) throws Exception {
    Order order = buildOrder();

    // Warm up to initialize mapper internals
    validate(modelMapper, order);
    validate(dozerMapper, order);
    validate(orikaMapper, order);

    for (int i2 = 0; i2 < 10; i2++) {
      iterate(modelMapper, "ModelMapper:  ", order);
      iterate(dozerMapper, "Dozer:   ", order);
      iterate(orikaMapper, "Orika:   ", order);
      System.out.println();
    }

    System.out.println("Concurrent:");

    for (int i2 = 0; i2 < 10; i2++) {
      concurrentlyIterate(modelMapper, "ModelMapper:  ", order);
      concurrentlyIterate(dozerMapper, "Dozer:   ", order);
      concurrentlyIterate(orikaMapper, "Orika:   ", order);
      System.out.println();
    }
  }

  static final OrderMapper modelMapper = new OrderMapper() {
    ModelMapper modelMapper;
    {
      modelMapper = new ModelMapper();
      modelMapper.addMappings(new PropertyMap<Order, OrderDTO>() {
        @Override
        protected void configure() {
          map().setBillingStreetAddress(source.getCustomer().getBillingAddress().getStreet());
          map().setBillingCity(source.getCustomer().getBillingAddress().getCity());
          map().setShippingStreetAddress(source.getCustomer().getShippingAddress().getStreet());
          map().setShippingCity(source.getCustomer().getShippingAddress().getCity());
        }
      });
    }

    public OrderDTO map(Order source) {
      return modelMapper.map(source, OrderDTO.class);
    }
  };

  static final OrderMapper dozerMapper = new OrderMapper() {
    final DozerBeanMapper beanMapper;
    {
      beanMapper = new DozerBeanMapper();
      beanMapper.setMappingFiles(Arrays.asList("org/modelmapper/performance/dozerComparison.xml"));
    }

    public OrderDTO map(Order source) {
      return beanMapper.map(source, OrderDTO.class);
    }
  };

  static final OrderMapper orikaMapper = new OrderMapper() {
    final MapperFacade facade;
    {
      MapperFactory factory = new DefaultMapperFactory.Builder().build();
      factory.registerClassMap(ClassMapBuilder.map(Order.class, OrderDTO.class)
                                              .field("customer.name", "customerName")
                                              .field("customer.billingAddress.street",
                                                  "billingStreetAddress")
                                              .field("customer.billingAddress.city", "billingCity")
                                              .field("customer.shippingAddress.street",
                                                  "shippingStreetAddress")
                                              .field("customer.shippingAddress.city",
                                                  "shippingCity")
                                              .field("products", "products")
                                              .toClassMap());
      factory.build();
      facade = factory.getMapperFacade();
    }

    public OrderDTO map(Order source) {
      return facade.map(source, OrderDTO.class);
    }
  };

  static void iterate(OrderMapper orderMapper, String label, Order order) {
    int count = 100000;

    long time = System.currentTimeMillis();

    for (int i = 0; i < count; i++) {
      try {
        orderMapper.map(order);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    time = System.currentTimeMillis() - time;

    System.out.println(label + format.format(count * 1000 / time) + " map operations / second");
  }

  static void concurrentlyIterate(final OrderMapper orderMapper, String label, final Order order) {
    int threadCount = 10;
    final int count = 10000;

    Thread[] threads = new Thread[threadCount];

    for (int i = 0; i < threadCount; i++) {
      threads[i] = new Thread() {
        public void run() {
          for (int i = 0; i < count; i++) {
            try {
              validate(orderMapper, order);
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        }
      };
    }

    long time = System.currentTimeMillis();

    for (int i = 0; i < threadCount; i++) {
      threads[i].start();
    }

    for (int i = 0; i < threadCount; i++) {
      try {
        threads[i].join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    time = System.currentTimeMillis() - time;

    System.out.println(label + format.format(count * 1000 / time) + " map operations / second");
  }

  static Order buildOrder() {
    Order order = new Order();
    order.customer = new Customer();
    order.customer.name = "Joe Smith";
    order.customer.billingAddress = new Address();
    order.customer.billingAddress.street = "1234 Market Street";
    order.customer.billingAddress.city = "San Fran";
    order.customer.shippingAddress = new Address();
    order.customer.shippingAddress.street = "1234 West Townsend";
    order.customer.shippingAddress.city = "Boston";
    order.products = new ArrayList<Product>();
    order.products.add(new Product("socks"));
    order.products.add(new Product("shoes"));
    return order;
  }

  static void validate(OrderMapper orderMapper, Order source) throws Exception {
    OrderDTO dto = orderMapper.map(source);
    Assert.assertEquals(dto.customerName, "Joe Smith");
    Assert.assertEquals(dto.billingStreetAddress, "1234 Market Street");
    Assert.assertEquals(dto.billingCity, "San Fran");
    Assert.assertEquals(dto.shippingStreetAddress, "1234 West Townsend");
    Assert.assertEquals(dto.shippingCity, "Boston");
    Assert.assertEquals(dto.products.get(0).name, "socks");
    Assert.assertEquals(dto.products.get(1).name, "shoes");
  }

  public static class Order {
    private Customer customer;
    private List<Product> products;

    public Customer getCustomer() {
      return customer;
    }

    public void setCustomer(Customer customer) {
      this.customer = customer;
    }

    public List<Product> getProducts() {
      return products;
    }

    public void setProducts(List<Product> products) {
      this.products = products;
    }
  }

  public static class Customer {
    private String name;
    private Address shippingAddress;
    private Address billingAddress;

    public Address getShippingAddress() {
      return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
      this.shippingAddress = shippingAddress;
    }

    public Address getBillingAddress() {
      return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
      this.billingAddress = billingAddress;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class Product {
    private String name;

    Product(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class Address {
    private String street;
    private String city;

    public String getStreet() {
      return street;
    }

    public void setStreet(String street) {
      this.street = street;
    }

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
    }
  }

  public static class OrderDTO {
    private List<ProductDTO> products;
    private String customerName;
    private String shippingStreetAddress;
    private String shippingCity;
    private String billingStreetAddress;
    private String billingCity;

    public String getCustomerName() {
      return customerName;
    }

    public void setCustomerName(String customerName) {
      this.customerName = customerName;
    }

    public String getShippingStreetAddress() {
      return shippingStreetAddress;
    }

    public void setShippingStreetAddress(String shippingStreetAddress) {
      this.shippingStreetAddress = shippingStreetAddress;
    }

    public String getShippingCity() {
      return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
      this.shippingCity = shippingCity;
    }

    public String getBillingStreetAddress() {
      return billingStreetAddress;
    }

    public void setBillingStreetAddress(String billingStreetAddress) {
      this.billingStreetAddress = billingStreetAddress;
    }

    public String getBillingCity() {
      return billingCity;
    }

    public void setBillingCity(String billingCity) {
      this.billingCity = billingCity;
    }

    public List<ProductDTO> getProducts() {
      return products;
    }

    public void setProducts(List<ProductDTO> products) {
      this.products = products;
    }
  }

  public static class ProductDTO {
    private String name;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
