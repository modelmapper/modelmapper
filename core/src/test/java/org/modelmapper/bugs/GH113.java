package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/113
 */
@Test
public class GH113 extends AbstractTest {
  static class Client {
    private String clientFName;

    private String clientLName;

    String getClientFName() {
      return clientFName;
    }

    void setClientFName(String clientFName) {
      this.clientFName = clientFName;
    }

    String getClientLName() {
      return clientLName;
    }

    void setClientLName(String clientLName) {
      this.clientLName = clientLName;
    }

    @Override
    public int hashCode() {
      int result = clientFName != null ? clientFName.hashCode() : 0;
      result = 31 * result + (clientLName != null ? clientLName.hashCode() : 0);
      return result;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      Client client = (Client) o;

      if (clientFName != null ? !clientFName.equals(client.clientFName)
          : client.clientFName != null)
        return false;
      if (clientLName != null ? !clientLName.equals(client.clientLName)
          : client.clientLName != null)
        return false;

      return true;
    }
  }

  static class Employee {
    private String employeeFName;
    private String employeeLName;

    String getEmployeeFName() {
      return employeeFName;
    }

    void setEmployeeFName(String employeeFName) {
      this.employeeFName = employeeFName;
    }

    String getEmployeeLName() {
      return employeeLName;
    }

    void setEmployeeLName(String employeeLName) {
      this.employeeLName = employeeLName;
    }

    @Override
    public int hashCode() {
      int result = employeeFName != null ? employeeFName.hashCode() : 0;
      result = 31 * result + (employeeLName != null ? employeeLName.hashCode() : 0);
      return result;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;

      Employee employee = (Employee) o;

      if (employeeFName != null ? !employeeFName.equals(employee.employeeFName)
          : employee.employeeFName != null)
        return false;
      if (employeeLName != null ? !employeeLName.equals(employee.employeeLName)
          : employee.employeeLName != null)
        return false;

      return true;
    }
  }

  public void shouldMapWithHashCode() {
    PropertyMap<Client, Employee> personMap = new PropertyMap<Client, Employee>() {
      protected void configure() {
        map().setEmployeeFName(source.getClientFName());
        map().setEmployeeLName(source.getClientLName());
      }
    };

    modelMapper.addMappings(personMap);

    Client customer = new Client();
    customer.setClientFName("testFName");
    customer.setClientLName("testLName");

    Employee employee = modelMapper.map(customer, Employee.class);

    assert employee.getEmployeeFName().equals(customer.getClientFName());
    assert employee.getEmployeeLName().equals(customer.getClientLName());
  }
}
