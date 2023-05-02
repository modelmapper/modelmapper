package org.modelmapper.internal.valueaccess;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Tests the mapping of a Record to a POJO.
 *
 * @author okaponta
 */
@Test
public class RecordValueReaderTest extends AbstractTest {
  public record UserRecord(
      String userId,
      String userName
  ) {
  }
  
  public static class User {
    String userId;
    String userName;
  }

  public void shouldMapRecordToBean() {
    UserRecord userRecord = new UserRecord("id", "name");

    User user = modelMapper.map(userRecord, User.class);

    assertEquals(user.userId, "id");
    assertEquals(user.userName, "name");

    UserRecord anotherUserRecord = new UserRecord("anotherId", "anotherName");

    user = modelMapper.map(anotherUserRecord, User.class);

    assertEquals(user.userId, "anotherId");
    assertEquals(user.userName, "anotherName");
  }

  public void shouldMapNullValue() {
    UserRecord userRecord = new UserRecord("id", null);

    User user = modelMapper.map(userRecord, User.class);

    assertEquals(user.userId, "id");
    assertNull(user.userName);
  }
}
