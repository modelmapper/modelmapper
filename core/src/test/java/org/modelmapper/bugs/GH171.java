package org.modelmapper.bugs;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class GH171 {
  static class AddressDTO {
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

  static class EmployeeDTO {
    private String name;
    private AddressDTO addressDTO;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public AddressDTO getAddressDTO() {
      return addressDTO;
    }

    public void setAddressDTO(AddressDTO addressDTO) {
      this.addressDTO = addressDTO;
    }
  }

  static class AddressVO {
    private String city;

    public String getCity() {
      return city;
    }

    public void setCity(String city) {
      this.city = city;
    }
  }

  static class EmployeeVO {
    private String name;
    private AddressVO addressVO;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public AddressVO getAddressVO() {
      return addressVO;
    }

    public void setAddressVO(AddressVO addressVO) {
      this.addressVO = addressVO;
    }
  }

  private ModelMapper modelMapper;

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
  }

  public void shouldMap1() {
    modelMapper.addMappings(new PropertyMap<AddressVO, AddressDTO>() {
      @Override
      protected void configure() {
        map(source.getCity(), destination.getCity());
      }
    });

    modelMapper.addMappings(new PropertyMap<EmployeeVO, EmployeeDTO>() {
      @Override
      protected void configure() {
        map(source.getAddressVO(), destination.getAddressDTO());
      }
    });

    EmployeeVO employeeVO = new EmployeeVO();
    employeeVO.setName("Reddy");
    AddressVO addressVO = new AddressVO();
    addressVO.setCity("City2");
    employeeVO.setAddressVO(addressVO);

    EmployeeDTO employeeDTO = new EmployeeDTO();
    employeeDTO.setName("kiran");
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setStreet("Street1");
    addressDTO.setCity("City1");
    employeeDTO.setAddressDTO(addressDTO);

    modelMapper.map(employeeVO, employeeDTO);

    Assert.assertEquals(employeeDTO.getName(), "Reddy");
    Assert.assertEquals(employeeDTO.getAddressDTO().getCity(), "City2");
    Assert.assertEquals(employeeDTO.getAddressDTO().getStreet(), "Street1");
  }

  public void shouldMap2() {
    modelMapper.addMappings(new PropertyMap<AddressVO, AddressDTO>() {
      @Override
      protected void configure() {
        map(source.getCity(), destination.getCity());
      }
    });

    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setStreet("Street1");
    addressDTO.setCity("City1");

    AddressVO addressVO = new AddressVO();
    addressVO.setCity("City2");

    modelMapper.map(addressVO, addressDTO);

    Assert.assertEquals(addressDTO.getCity(), "City2");
    Assert.assertEquals(addressDTO.getStreet(), "Street1");
  }
}
