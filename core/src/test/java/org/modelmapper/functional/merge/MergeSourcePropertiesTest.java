package org.modelmapper.functional.merge;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

@Test
public class MergeSourcePropertiesTest extends AbstractTest {
  static class Source {
    private int year;
    private int month;
    private int day;

    public Source(int year, int month, int day) {
      this.year = year;
      this.month = month;
      this.day = day;
    }

    public int getYear() {
      return year;
    }

    public void setYear(int year) {
      this.year = year;
    }

    public int getMonth() {
      return month;
    }

    public void setMonth(int month) {
      this.month = month;
    }

    public int getDay() {
      return day;
    }

    public void setDay(int day) {
      this.day = day;
    }
  }

  static class Destination {
    private String date;

    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }
  }

  public void shouldMergeSourceProperties() {
    final Converter<Source, String> converter = new Converter<Source, String>() {
      public String convert(MappingContext<Source, String> context) {
        return String.format("%04d/%02d/%02d", context.getSource().getYear(), context.getSource().getMonth(), context.getSource().getDay());
      }
    };
    modelMapper.addMappings(new PropertyMap<Source, Destination>() {
      protected void configure() {
        using(converter).map(source).setDate(null);
      }
    });

    Destination destination = modelMapper.map(new Source(2000, 1, 1), Destination.class);
    assertEquals(destination.getDate(), "2000/01/01");
  }
}
