package org.modelmapper.functional.persistence;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnitUtil;

import org.modelmapper.AbstractTest;
import org.modelmapper.Condition;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

@Test
public class LazyFetching extends AbstractTest {
  static class CompanyDTO {
    List<EmployeeDTO> employees;
  }

  static class EmployeeDTO {
    String firstName;
    String lastName;
  }

  public void shouldSkipLazyFetchedProperties() throws Throwable {
    // Given
    EntityManager em = Persistence.createEntityManagerFactory("mmtest").createEntityManager();
    Company c = new Company();
    Employee e = new Employee();
    c.employees = Arrays.asList(e);
    em.getTransaction().begin();
    em.persist(c);
    em.getTransaction().commit();
    em.clear();
    c = em.find(Company.class, 1L);

    // When
    final PersistenceUnitUtil unitUtil = em.getEntityManagerFactory().getPersistenceUnitUtil();
    modelMapper.getConfiguration().setPropertyCondition(new Condition<Object, Object>() {
      public boolean applies(MappingContext<Object, Object> context) {
        return unitUtil.isLoaded(context.getSource());
      }
    });
    CompanyDTO dto = modelMapper.map(c, CompanyDTO.class);

    // Then
    assertFalse(unitUtil.isLoaded(c, "employees"));
    assertTrue(dto.employees == null);
  }
}
