package org.modelmapper.functional.skip;

import org.modelmapper.AbstractTest;
import org.modelmapper.Condition;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test
public class EnableSkipNullTest extends AbstractTest {
  static class UserDto {
    private String userId;

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }
  }

  static class ActiveUserEntity {
    private String id;
    private String uuid;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getUuid() {
      return uuid;
    }

    public void setUuid(String uuid) {
      this.uuid = uuid;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper.getConfiguration().setSkipNullEnabled(true);
  }

  public void shouldSkipNull() {
    ActiveUserEntity destination = new ActiveUserEntity();
    destination.setId("foo");
    destination.setUuid("bar");

    modelMapper.map(new UserDto(), destination);

    assertEquals(destination.getId(), "foo");
    assertEquals(destination.getUuid(), "bar");
  }

  public void shouldSkipNullWithPropertyMapSkipFailed() {
    final Condition<Object, Object> alwaysFalse = new Condition<Object, Object>() {
      public boolean applies(MappingContext<Object, Object> context) {
        return false;
      }
    };

    modelMapper.addMappings(new PropertyMap<UserDto, ActiveUserEntity>() {
      @Override
      protected void configure() {
        when(alwaysFalse).skip().setId(null);
        when(alwaysFalse).skip().setUuid(null);
      }
    });

    ActiveUserEntity destination = new ActiveUserEntity();
    destination.setId("foo");
    destination.setUuid("bar");

    modelMapper.map(new UserDto(), destination);

    assertEquals(destination.getId(), "foo");
    assertEquals(destination.getUuid(), "bar");
  }

  public void shouldSkipNullWithConverter() {
    final Converter<Object, Object> converter = new Converter<Object, Object>() {
      public Object convert(MappingContext<Object, Object> context) {
        return context.getSource();
      }
    };

    modelMapper.addMappings(new PropertyMap<UserDto, ActiveUserEntity>() {
      @Override
      protected void configure() {
        using(converter).map(source.getUserId()).setId(null);
        using(converter).map(source.getUserId()).setUuid(null);
      }
    });

    ActiveUserEntity destination = new ActiveUserEntity();
    destination.setId("foo");
    destination.setUuid("bar");

    modelMapper.map(new UserDto(), destination);

    assertEquals(destination.getId(), "foo");
    assertEquals(destination.getUuid(), "bar");
  }
}
