package org.modelmapper.functional.merging;

import static org.testng.Assert.assertEquals;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * Tests merging from an existing TypeMap with a Converter.
 */
@Test
public class MergingWithConverter extends AbstractTest {
  static class Source {
    Date date1;
    Date date2;
  }

  static class Destination {
    XMLGregorianCalendar date1;
    XMLGregorianCalendar date2;
  }

  public void test() {
    modelMapper.addConverter(new Converter<Date, XMLGregorianCalendar>() {
      public XMLGregorianCalendar convert(MappingContext<Date, XMLGregorianCalendar> context) {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(context.getSource());
        try {
          return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (Exception e) {
          return null;
        }
      }
    });

    Source source = new Source();
    source.date1 = new Date();
    source.date2 = new Date();

    Destination dest = modelMapper.map(source, Destination.class);
    assertEquals(dest.date1.toGregorianCalendar().getTime(), source.date1);
    assertEquals(dest.date2.toGregorianCalendar().getTime(), source.date2);
  }
}
