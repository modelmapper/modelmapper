package org.modelmapper.functional.loosematching;

import org.modelmapper.AbstractTest;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.MatchingStrategyTestSupport;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests a scenario where the destination members are fulfilled from various parts of the source
 * object graph. Requires the loose matching strategy.
 * 
 * <pre>
 * Order
 *   OrderSourceInfo sourceInfo;
 *     String company;
 *   OrderDestinationInfo destinationInfo;
 *     Address customerAddress;
 *   int companyId;
 *     
 * OrderDTO
 *   OrderMailingData data;
 *     Company company;
 *       int companyId;
 *       String company;
 *     Address customerAddress;
 *   
 * Order=>OrderDTO
 *   comapnyId->data.company.companyId
 *   sourceInfo.company->data.company.company
 *   destinationInfo.customerAddress->data.customerAddress
 * </pre>
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class LooseMappingTest1 extends AbstractTest {
  static class Order {
    OrderSourceInfo sourceInfo;
    OrderDestinationInfo destinationInfo;
    int companyId;
  }

  static class OrderSourceInfo {
    String company;
  }

  static class OrderDestinationInfo {
    Address customerAddress;
  }

  static class OrderDTO {
    OrderMailingData data;
  }

  static class OrderMailingData {
    Company company;
    Address customerAddress;
  }

  static class Company {
    int companyId;
    String company;
  }

  static class Address {
    String value;
  }

  @Override
  @BeforeMethod
  protected void initContext() {
    super.initContext();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
  }

  public void assertMatches() {
    MatchingStrategyTestSupport tester = new MatchingStrategyTestSupport(MatchingStrategies.LOOSE);
    tester.match(Integer.TYPE, "companyId").to("companyId").assertMatch();
    tester.match(OrderSourceInfo.class, "sourceInfo").$(String.class, "company").to("company")
        .assertMatch();
    tester.match(OrderDestinationInfo.class, "destinationInfo").$(Address.class, "customerAddress")
        .to("customerAddress").assertMatch();
    tester.match(Integer.TYPE, "companyId").to("data", "company", "companyId").assertMatch();
  }

  public void shouldValidate() {
    modelMapper.getTypeMap(Order.class, OrderDTO.class);
    modelMapper.validate();
  }
}
