package org.modelmapper.functional.deepmapping;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.Asserts;
import org.modelmapper.ConfigurationException;
import org.modelmapper.convention.MatchingStrategies;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class NestedMappingTest2 extends AbstractTest {
  private static class Date {
    int year;
    int month;
    int date;

    @SuppressWarnings("unused")
    public Date() {
    }

    public Date(int year, int month, int date) {
      this.year = year;
      this.month = month;
      this.date = date;
    }
  }

  private static class Event1 {
    Date date;
  }

  private static class Event2 {
    Date eventDate;
  }

  private static class EventDTO1 {
    int year;
    int month;
    int date;
  }

  private static class EventDTO2 {
    int eventDate;
    int eventMonth;
    int eventYear;
  }

  private static class EventDTO3 {
    Date date;
    int year;
  }

  private static class EventDTO4 {
    int firstYear;
    int secondYear;
  }

  private static class Event3 {
    Date firstEvent;
    Date secondEvent;
  }

  /**
   * Maps EventDTO1/year from Event1/date/year<br>
   * Maps EventDTO1/month from Event1/date/month<br>
   * Maps EventDTO1/date from Event1/date/date
   */
  public void shouldMapEvent1ToEventDTO1() {
    Event1 e = new Event1();
    e.date = new Date(2010, 5, 1);

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    EventDTO1 dto = modelMapper.map(e, EventDTO1.class);

    modelMapper.validate();
    assertEquals(dto.year, e.date.year);
    assertEquals(dto.month, e.date.month);
    assertEquals(dto.date, e.date.date);
  }

  /**
   * Maps EventDTO2/date from Event1/date<br>
   * Maps EventDTO2/eventYear from Event1/date/year<br>
   * Maps EventDTO2/eventMonth from Event1/date/month<br>
   * Maps EventDTO2/eventDate from Event1/date/date
   */
  public void shouldMapEvent1ToEventDTO2() {
    Event1 event = new Event1();
    event.date = new Date(2010, 5, 1);

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    EventDTO2 dto = modelMapper.map(event, EventDTO2.class);

    modelMapper.validate();
    assertEquals(dto.eventYear, event.date.year);
    assertEquals(dto.eventMonth, event.date.month);
    assertEquals(dto.eventDate, event.date.date);
  }

  /**
   * Maps EventDTO3/date from Event1/date<br>
   * Maps EventDTO3/year from Event1/date/year<br>
   * 
   * <p>
   * Fulfills multiple destination members using the same source member.
   */
  public void shouldMapEvent1ToEventDTO3() {
    Event1 event = new Event1();
    event.date = new Date(2010, 5, 1);

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    EventDTO3 dto = modelMapper.map(event, EventDTO3.class);

    modelMapper.validate();
    assertEquals(dto.date, event.date);
    assertEquals(dto.year, event.date.year);
  }

  /**
   * Maps EventDTO4/firstYear from Event1/date/year<br>
   * Maps EventDTO4/secondYear from Event1/date/year<br>
   */
  @Test(enabled = false)
  public void shouldMapEvent1ToEventDTO4() {
    Event1 event = new Event1();
    event.date = new Date(2010, 5, 1);

    EventDTO4 dto = modelMapper.map(event, EventDTO4.class);

    modelMapper.validate();
    assertEquals(dto.firstYear, event.date.year);
    assertEquals(dto.secondYear, event.date.year);
  }

  /**
   * Maps EventDTO1/date from Event2/eventDate/date<br>
   * Maps EventDTO1/month from Event2/eventDate/month<br>
   * Maps EventDTO1/year from Event2/eventDate/year
   * 
   * <p>
   * Disambiguates EventDTO1/date and EventDTO1/date/date by type.
   */
  public void shouldMapEvent2ToEventDTO1() {
    Event2 e = new Event2();
    e.eventDate = new Date(2010, 5, 1);
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    EventDTO1 dto = modelMapper.map(e, EventDTO1.class);

    modelMapper.validate();
    assertEquals(dto.date, e.eventDate.date);
    assertEquals(dto.month, e.eventDate.month);
    assertEquals(dto.year, e.eventDate.year);
  }

  /**
   * <pre>
   * Date eventDate/date -> eventDate
   * Date eventDate/month-> eventMonth
   * Date eventDate/year-> eventYear
   * </pre>
   */
  public void shouldMapEvent2ToEventDTO2() {
    Event2 e = new Event2();
    e.eventDate = new Date(2010, 5, 1);

    EventDTO2 dto = modelMapper.map(e, EventDTO2.class);
    modelMapper.getTypeMap(Event2.class, EventDTO2.class);

    modelMapper.validate();
    assertEquals(dto.eventDate, e.eventDate.date);
    assertEquals(dto.eventMonth, e.eventDate.month);
    assertEquals(dto.eventYear, e.eventDate.year);
  }

  /**
   * Maps Event1/date/year from EventDTO1/year<br>
   * Maps Event1/date/month from EventDTO1/month<br>
   * Maps Event1/date/date from EventDTO1/date
   */
  public void shouldMapEventDTO1ToEvent1() {
    EventDTO1 dto = new EventDTO1();
    dto.date = 21;
    dto.month = 12;
    dto.year = 2009;

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    Event1 event = modelMapper.map(dto, Event1.class);

    modelMapper.validate();
    assertEquals(event.date.year, dto.year);
    assertEquals(event.date.month, dto.month);
    assertEquals(event.date.date, dto.date);
  }

  /**
   * Maps Event1/date/year from EventDTO2/eventYear<br>
   * Maps Event1/date/month from EventDTO2/eventMonth <br>
   * Maps Event1/date/date from EventDTO2/eventDate
   */
  public void shouldMapEventDTO2ToEvent1() {
    EventDTO2 dto = new EventDTO2();
    dto.eventDate = 21;
    dto.eventMonth = 12;
    dto.eventYear = 2005;

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    Event1 event = modelMapper.map(dto, Event1.class);

    modelMapper.validate();
    assertEquals(event.date.date, dto.eventDate);
    assertEquals(event.date.month, dto.eventMonth);
    assertEquals(event.date.year, dto.eventYear);
  }

  /**
   * Maps Event1/date from EventDTO3/date<br>
   */
  public void shouldMapEventDTO3ToEvent1() {
    EventDTO3 dto = new EventDTO3();
    dto.date = new Date(2010, 5, 1);
    dto.year = 2001;

    Event1 event = modelMapper.map(dto, Event1.class);

    modelMapper.validate();
    assertEquals(event.date, dto.date);
  }

  /**
   * <pre>
   * Date date/int date -> eventDate
   * Date date/int month -> eventMonth
   * Date date/int year <>
   * int year -> eventYear -> eventYear
   * </pre>
   */
  public void shouldMapEventDTO3ToEventDTO2() {
    EventDTO3 dto3 = new EventDTO3();
    dto3.date = new Date(2010, 5, 20);
    dto3.year = 2005;

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
    EventDTO2 dto2 = modelMapper.map(dto3, EventDTO2.class);

    assertEquals(dto2.eventDate, 20);
    assertEquals(dto2.eventMonth, 5);
    assertEquals(dto2.eventYear, 2005);
  }

  public void shouldThrowOnAmbiguousEventDTO4ToEvent1() {
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

    try {
      modelMapper.map(new EventDTO4(), EventDTO1.class);
    } catch (ConfigurationException e) {
      Asserts.assertContains(e.getMessage(), "matches multiple");
      return;
    }

    fail();
  }

  /**
   * <pre>
   * int firstYear -> firstEvent/year
   * int secondYear -> secondEvent/year
   * </pre>
   */
  public void shouldMapEventDTO4ToEvent3() {
    EventDTO4 dto = new EventDTO4();
    dto.firstYear = 2001;
    dto.secondYear = 2005;

    Event3 event3 = modelMapper.map(dto, Event3.class);

    // The typeMap is not full match
    // modelMapper.validate();

    assertEquals(dto.firstYear, event3.firstEvent.year);
    assertEquals(event3.firstEvent.date, 0);
    assertEquals(event3.firstEvent.month, 0);
    assertEquals(dto.secondYear, event3.secondEvent.year);
    assertEquals(event3.secondEvent.date, 0);
    assertEquals(event3.secondEvent.month, 0);
  }
}
