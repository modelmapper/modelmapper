package org.modelmapper;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.spi.MappingContext;

public class FieldRelocation {
  public static class Person {
    String name;
    List<Car> cars;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<Car> getCars() {
      return cars;
    }

    public void setCars(List<Car> cars) {
      this.cars = cars;
    }
  }

  public static class Car {
    String type;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

  public static class AnotherPerson {
    List<AnotherCar> cars;

    public List<AnotherCar> getCars() {
      return cars;
    }

    public void setAnotherCars(List<AnotherCar> cars) {
      this.cars = cars;
    }
  }

  public static class AnotherCar {
    String personName;
    String type;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getPersonName() {
      return personName;
    }

    public void setPersonName(String personName) {
      this.personName = personName;
    }
  }

  public static void main(String... args) {
    ModelMapper modelMapper = new ModelMapper();

    modelMapper.addConverter(new Converter<Car, AnotherCar>() {
      public AnotherCar convert(MappingContext<Car, AnotherCar> context) {
        Person person = (Person) context.getParent().getParent().getSource();
        context.getDestination().setPersonName(person.getName());
        context.getDestination().setType(context.getSource().getType());
        return context.getDestination();
      }
    });

    // Alternatively, using a provider
    // modelMapper.getConfiguration().setProvider(new Provider<AnotherCar>() {
    // public AnotherCar get(org.modelmapper.Provider.ProvisionRequest<AnotherCar> request) {
    // AnotherCar anotherCar = new AnotherCar();
    // anotherCar.setPersonName(((Person) request.getSource()).getName());
    // return anotherCar;
    // }
    // });

    modelMapper.addMappings(new PropertyMap<Person, AnotherPerson>() {
      @Override
      protected void configure() {
        map(source.getCars()).setAnotherCars(null);
      }
    });

    Person person = new Person();
    person.name = "joe";
    Car car1 = new Car();
    car1.type = "Honda";
    Car car2 = new Car();
    car2.type = "Toyota";
    person.cars = Arrays.asList(car1, car2);
    AnotherPerson anotherPerson = modelMapper.map(person, AnotherPerson.class);

    assertEquals(anotherPerson.getCars().get(0).personName, "joe");
    assertEquals(anotherPerson.getCars().get(0).type, car1.type);
    assertEquals(anotherPerson.getCars().get(1).personName, "joe");
    assertEquals(anotherPerson.getCars().get(1).type, car2.type);
  }
}
