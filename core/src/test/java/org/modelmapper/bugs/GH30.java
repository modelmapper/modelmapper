package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

/**
 * Cannot proxy types without default constructor.
 * 
 * https://github.com/jhalterman/modelmapper/issues/30
 */
public class GH30 extends AbstractTest {
  public static class Customer {
    private final String firstName;
    private final String lastName;
    private String homePhone;

    public Customer(final String firstName, final String lastName) {
      this.firstName = firstName;
      this.lastName = lastName;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public String getHomePhone() {
      return homePhone;
    }

    public void setHomePhone(final String homePhone) {
      this.homePhone = homePhone;
    }
  }

  public static class CustomerDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public String getFirstName() {
      return firstName;
    }

    public void setFirstName(final String firstName) {
      this.firstName = firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public void setLastName(final String lastName) {
      this.lastName = lastName;
    }

    public String getPhoneNumber() {
      return phoneNumber;
    }

    public void setPhoneNumber(final String phoneNumber) {
      this.phoneNumber = phoneNumber;
    }
  }

  @Test
  public void testMappingWithConstructorCustomPropertyMap() throws Exception {
    modelMapper.createTypeMap(CustomerDto.class, Customer.class)
        .setProvider(new Provider<Customer>() {
          public Customer get(final ProvisionRequest<Customer> request) {
            CustomerDto dto = (CustomerDto) request.getSource();
            return new Customer(dto.getFirstName(), dto.getLastName());
          }
        })
        .addMappings(new PropertyMap<CustomerDto, Customer>() {
          protected void configure() {
            map().setHomePhone(source.getPhoneNumber());
          }
        });

    CustomerDto customerDto = new CustomerDto();
    customerDto.setFirstName("Joe");
    customerDto.setLastName("Blow");
    customerDto.setPhoneNumber("4158675309");

    final Customer customer = modelMapper.map(customerDto, Customer.class);
    assertEquals(customer.getFirstName(), customerDto.getFirstName());
    assertEquals(customer.getLastName(), customerDto.getLastName());
    assertEquals(customer.getHomePhone(), customerDto.getPhoneNumber());
  }
}
