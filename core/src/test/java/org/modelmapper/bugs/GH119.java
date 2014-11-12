package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

@Test
public class GH119 extends AbstractTest {
  static class Domain {
    long id;
    Domain parent;
  }

  static class DomainDto {
    long id;
    DomainDto parent;

    void setId(long id) {
      this.id = id;
    }

    void setParent(DomainDto parent) {
      this.parent = parent;
    }
  }

  @Test
  public void shouldMapCircularReferencesWithProvider() {
    Provider<Object> provider = new Provider<Object>() {
      public Object get(ProvisionRequest<Object> request) {
        if (request.getSource() instanceof DomainDto)
          return new Domain();
        return null;
      }
    };

    modelMapper.getConfiguration().setProvider(provider);

    DomainDto parent = new DomainDto();
    parent.setId(1L);

    DomainDto domainDto = new DomainDto();
    domainDto.setId(2L);
    domainDto.setParent(parent);

    Domain domain = modelMapper.map(domainDto, Domain.class);
    assertEquals(domain.id, 2L);
    assertEquals(domain.parent.id, 1L);
  }
}
