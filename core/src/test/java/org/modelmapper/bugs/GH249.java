package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class GH249 extends AbstractTest {
  static class Role {
    private String name;

    public Role() {
    }

    public Role(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  static class PojoAgent {
    private Set<Role> roles;

    public Set<Role> getRoles() {
      return roles;
    }

    public void setRoles(Set<Role> roles) {
      this.roles = roles;
    }
  }

  static class DomainAgent {
    private Set<String> roles;

    public Set<String> getRoles() {
      return roles;
    }

    public void setRoles(Set<String> roles) {
      this.roles = roles;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper.addConverter(new Converter<Role, String>() {
      public String convert(MappingContext<Role, String> context) {
        return context.getSource().getName();
      }
    });
  }

  public void shouldMap() {
    PojoAgent pojoAgent = new PojoAgent();
    pojoAgent.setRoles(new HashSet<Role>(Arrays.asList(new Role("foo"), new Role("bar"))));

    DomainAgent domainAgent = modelMapper.map(pojoAgent, DomainAgent.class);
    assertEquals(2, domainAgent.getRoles().size());
  }

  public void shouldMapExistDestination() {
    PojoAgent pojoAgent = new PojoAgent();
    pojoAgent.setRoles(new HashSet<Role>(Arrays.asList(new Role("foo"), new Role("bar"))));

    DomainAgent domainAgent = new DomainAgent();
    domainAgent.setRoles(new HashSet<String>());

    modelMapper.map(pojoAgent, domainAgent);

    assertEquals(2, domainAgent.getRoles().size());
  }
}
