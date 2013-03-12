package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class GH42 extends AbstractTest {
  public static class Source {
    private String id;
    private String address;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }
  }

  public static class Destination {
    private String id;
    private String streetNumber;
    private String streetName;
    private String suburb;
    private String stateCode;
    private String postCode;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getStreetNumber() {
      return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
      this.streetNumber = streetNumber;
    }

    public String getStreetName() {
      return streetName;
    }

    public void setStreetName(String streetName) {
      this.streetName = streetName;
    }

    public String getSuburb() {
      return suburb;
    }

    public void setSuburb(String suburb) {
      this.suburb = suburb;
    }

    public String getStateCode() {
      return stateCode;
    }

    public void setStateCode(String stateCode) {
      this.stateCode = stateCode;
    }

    public String getPostCode() {
      return postCode;
    }

    public void setPostCode(String postCode) {
      this.postCode = postCode;
    }
  }

  @BeforeMethod
  protected void beforeMethod() {
    modelMapper.createTypeMap(Source.class, Destination.class).setPreConverter(
        new Converter<Source, Destination>() {
          public Destination convert(MappingContext<Source, Destination> ctx) {
            Source source = ctx.getSource();
            Destination dest = ctx.getDestination();
            ctx.getMappingEngine().map(ctx);

            String[] breakdown = StringUtils.split(source.getAddress(), ',');
            ArrayUtils.reverse(breakdown);
            for (int i = 0; i < breakdown.length; i++) {
              switch (i) {
                case 0:
                  dest.setPostCode(breakdown[i].trim());
                  break;
                case 1:
                  dest.setStateCode(breakdown[i].trim());
                  break;
                case 2:
                  dest.setSuburb(breakdown[i].trim());
                  break;
                case 3:
                  dest.setStreetName(breakdown[i].trim());
                  break;
                case 4:
                  dest.setStreetNumber(breakdown[i].trim());
                default:
                  break;
              }
            }

            return dest;
          }
        });
  }

  public void testFullAddress() {
    Source source = new Source();
    source.setId("15");
    source.setAddress("1, Fake St, Suburb, QLD, 4000");

    Destination dest = modelMapper.map(source, Destination.class);

    assertEquals("1", dest.getStreetNumber());
    assertEquals("Fake St", dest.getStreetName());
    assertEquals("Suburb", dest.getSuburb());
    assertEquals("QLD", dest.getStateCode());
    assertEquals("4000", dest.getPostCode());
    assertEquals(source.getId(), dest.getId());
  }

  public void testPartialAddress() {
    Source source = new Source();
    source.setId("15");
    source.setAddress("Suburb, QLD, 4000");

    Destination dest = modelMapper.map(source, Destination.class);

    assertNull(dest.getStreetNumber());
    assertNull(dest.getStreetName());
    assertEquals("Suburb", dest.getSuburb());
    assertEquals("QLD", dest.getStateCode());
    assertEquals("4000", dest.getPostCode());
    assertEquals(source.getId(), dest.getId());
  }
}
