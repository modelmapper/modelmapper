package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.UUID;
import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

@Test
public class GH779 extends AbstractTest {
  public static class User {
    private UUID id;
    private User createdBy;

    public UUID getId() {
      return id;
    }

    public User getCreatedBy() {
      return createdBy;
    }
  }

  public static class UserDto {
    private UUID id;
    private UserSimpleDto createdBy;

    public UUID getId() {
      return id;
    }

    public void setId(UUID id) {
      this.id = id;
    }

    public UserSimpleDto getCreatedBy() {
      return createdBy;
    }

    public void setCreatedBy(UserSimpleDto createdBy) {
      this.createdBy = createdBy;
    }
  }

  public static class UserSimpleDto {
    private UUID id;
    private UUID createdBy;

    public UUID getId() {
      return id;
    }

    public void setId(UUID id) {
      this.id = id;
    }

    public UUID getCreatedBy() {
      return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
      this.createdBy = createdBy;
    }
  }

  public void shouldNotReuseIncompatibleCircularDestination() {
    modelMapper.addConverter(new Converter<User, UUID>() {
      public UUID convert(MappingContext<User, UUID> context) {
        return context.getSource() == null ? null : context.getSource().getId();
      }
    });
    User source = new User();
    source.id = UUID.fromString("12345678-1234-1234-1234-123456789abc");
    source.createdBy = source;
    UserDto result = modelMapper.map(source, UserDto.class);
    assertEquals(result.getId(), source.id);
    assertNotNull(result.getCreatedBy());
    assertEquals(result.getCreatedBy().getId(), source.id);
    assertEquals(result.getCreatedBy().getCreatedBy(), source.id);
  }
}
