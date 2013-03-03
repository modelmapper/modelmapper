package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/31
 */
@Test
public class GH31 extends AbstractTest {
  public static class CompanyModel {
    String name;
    LocationModel location;
    Date createdAt;

    public Date getCreatedAt() {
      return createdAt;
    }
  }

  public static class LocationModel {
    String address;
    Date createdAt;

    public Date getCreatedAt() {
      return createdAt;
    }
  }

  public static class Company {
    String name;
    Location location;
    Date createdAt;

    public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
    }
  }

  public static class Location {
    String address;
    Date createdAt;

    public void setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
    }
  }

  @SuppressWarnings("deprecation")
  public void test() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    modelMapper.addMappings(new PropertyMap<CompanyModel, Company>() {
      @Override
      protected void configure() {
        map().setCreatedAt(source.getCreatedAt());
      }
    });

    modelMapper.addMappings(new PropertyMap<LocationModel, Location>() {
      @Override
      protected void configure() {
        map().setCreatedAt(source.getCreatedAt());
      }
    });

    Company company = new Company();
    company.name = "Pepsi Co.";
    company.createdAt = new Date(1999, 12, 12);
    company.location = new Location();
    company.location.address = "1234 Main St.";
    company.location.createdAt = new Date(1955, 5, 5);

    CompanyModel model = modelMapper.map(company, CompanyModel.class);

    assertEquals(company.name, model.name);
    assertEquals(company.createdAt, model.createdAt);
    assertEquals(company.location.address, model.location.address);
    assertEquals(company.location.createdAt, model.location.createdAt);
  }
}
