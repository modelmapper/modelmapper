package org.modelmapper.user;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * From https://groups.google.com/forum/?fromgroups#!topic/modelmapper/b3sZB5JpDzA
 */
@Test
public class CharArray extends AbstractTest {
  static class UserEntity {
    char[] password;
  }

  static class User {
    String password;
  }

  public void test() {
    UserEntity entity = new UserEntity();
    entity.password = "abc123".toCharArray();
    User user = modelMapper.map(entity, User.class);

    assertEquals(user.password, "abc123");
  }
}
