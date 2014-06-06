package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/47
 */
@Test
public class GH47 extends AbstractTest {
  public static class SqlDate {
    java.sql.Date date;

    public java.sql.Date getDate() {
      return date;
    }

    public void setDate(java.sql.Date sqlDate) {
      this.date = sqlDate;
    }
  }

  public static class JavaDate {
    Date date;

    public Date getDate() {
      return date;
    }

    public void setDate(Date javaDate) {
      this.date = javaDate;
    }
  }

  @SuppressWarnings("deprecation")
  public void shouldMapPropertyMappedTypes() {
    modelMapper.addMappings(new PropertyMap<SqlDate, JavaDate>() {
      protected void configure() {
        map().setDate(source.getDate());
      }
    });

    SqlDate sqlDate = new SqlDate();
    sqlDate.setDate(new java.sql.Date(new Date("2013/10/10").getTime()));

    JavaDate jDate = modelMapper.map(sqlDate, JavaDate.class);
    assertEquals(jDate.date.getTime(), sqlDate.date.getTime());
  }
}
