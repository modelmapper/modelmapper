package org.modelmapper.functional.deepmapping;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
@SuppressWarnings("unused")
public class NestedMappingTest3  extends AbstractTest {
  private static class Name1 {
    String firstName;
    String lastName;
  }

  private static class Name2 {
    String first;
    String last;
  }

  private static class Order {
    int id;
    Person person = new Person();
  }

  private static class OrderDTO1 {
    int id;
    int personId;
    String personNameFirstName;
    String personNameLastName;
  }

  private static class OrderDTO2 {
    String personFirstName;
    String personLastName;
  }

  private static class OrderDTO3 {
    Name1 name;
    String firstName;
    String lastName;
  }

  private static class Person {
    int id;
    Name1 name = new Name1();
  }

  private static class PersonDTO1 {
    String firstName;
    String lastName;
  }

  private static class PersonDTO2 {
    Name1 personName;
  }

  public void shouldMapName2ToPersonDTO1() {
    Name2 name = new Name2();
    name.first = "john";
    name.last = "doe";
    PersonDTO1 dto = modelMapper.map(name, PersonDTO1.class);

    modelMapper.validate();
    assertEquals(dto.firstName, name.first);
    assertEquals(dto.lastName, name.last);
  }


  /**
   * <pre>
   * int id -> id
   * String personNameFirstName -> person/name/firstName
   * String personNameLastName -> person/name/lastName
   * </pre>
   * 
   * Requires disambiguation.
   */
  public void shouldMapOrderDTO1ToOrder() {
    OrderDTO1 dto = new OrderDTO1();
    dto.personNameFirstName = "john";
    dto.personNameLastName = "doe";
    Order order = modelMapper.map(dto, Order.class);

    modelMapper.validate();
    assertEquals(order.person.name.firstName, dto.personNameFirstName);
    assertEquals(order.person.name.lastName, dto.personNameLastName);
  }

  /**
   * Maps Order/person/name/firstName from OrderDTO2/personFirstName<br>
   * Maps Order/person/name/lastName from OrderDTO2/personLastName<br>
   */
  public void shouldMapOrderDTO2ToOrder() {
    OrderDTO2 dto = new OrderDTO2();
    dto.personFirstName = "john";
    dto.personLastName = "doe";
    Order order = modelMapper.map(dto, Order.class);

    assertEquals(order.person.name.firstName, dto.personFirstName);
    assertEquals(order.person.name.lastName, dto.personLastName);
  }

  /**
   * <pre>
   * Name name -> person/name
   * </pre>
   */
  public void shouldMapOrderDTO3ToOrder() {
    OrderDTO3 dto = new OrderDTO3();
    dto.name = new Name1();
    dto.name.firstName = "john";
    dto.name.lastName = "doe";
    
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    Order order = modelMapper.map(dto, Order.class);

    assertEquals(order.person.name.firstName, dto.name.firstName);
    assertEquals(order.person.name.lastName, dto.name.lastName);
  }

  /**
   * Maps OrderDTO1/personNameFirstName from Order/person/name/firstName<br>
   * Maps OrderDTO1/personNameLastName from Order/person/name/lastName<br>
   */
  public void shouldMapOrderToOrderDTO1() {
    Order o = new Order();
    OrderDTO1 dto = modelMapper.map(o, OrderDTO1.class);

    modelMapper.validate();
    assertEquals(dto.personNameFirstName, o.person.name.firstName);
    assertEquals(dto.personNameLastName, o.person.name.lastName);
  }

  /**
   * Maps OrderDTO2/personFirstName from Order/person/name/firstName<br>
   * Maps OrderDTO2/personLastName from Order/person/name/lastName<br>
   */
  public void shouldMapOrderToOrderDTO2() {
    Order o = new Order();
    OrderDTO2 dto = modelMapper.map(o, OrderDTO2.class);

    modelMapper.validate();
    assertEquals(dto.personFirstName, o.person.name.firstName);
    assertEquals(dto.personLastName, o.person.name.lastName);
  }

  public void shouldMapOrderToOrderDTO3() {
    Order o = new Order();

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    OrderDTO3 dto3 = modelMapper.map(o, OrderDTO3.class);
    modelMapper.validate();
    
    assertEquals(dto3.firstName, o.person.name.firstName);
    assertEquals(dto3.lastName, o.person.name.lastName);
    assertEquals(dto3.name.firstName, o.person.name.firstName);
    assertEquals(dto3.name.lastName, o.person.name.lastName);
  }

  /**
   * Maps PersonDTO1/firstName from Person/name/firstName<br>
   * Maps PersonDTO1/lastName from Person/name/lastName
   */
  public void shouldMapPersonToPersonDTO1() {
    Person person = new Person();
    PersonDTO1 dto = modelMapper.map(person, PersonDTO1.class);

    modelMapper.validate();
    assertEquals(dto.firstName, person.name.firstName);
    assertEquals(dto.lastName, person.name.lastName);
  }

  /**
   * Maps PersonDTO2/personName from Person/name
   */
  public void shouldMapPersonToPersonDTO2() {
    Person person = new Person();
    PersonDTO2 dto = modelMapper.map(person, PersonDTO2.class);

    modelMapper.validate();
    assertEquals(dto.personName.firstName, person.name.firstName);
    assertEquals(dto.personName.lastName, person.name.lastName);
  }
}
