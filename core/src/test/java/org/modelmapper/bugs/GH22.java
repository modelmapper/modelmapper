package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * From https://groups.google.com/forum/#!topic/modelmapper/9yV4qNBTuEQ
 * 
 * This bug was caused by client.billingData.agent.businessCategory being cached in the
 * MappingContext's destination cache and reused when client.businessCategory was mapped. This was
 * caused by caching being done by Mutator instead of by path.
 * 
 * @author Jonathan Halterman
 */
@Test
public class GH22 extends AbstractTest {
  /** Source Objects */

  static class RegistryDTO {
    BusinessCategoryDTO businessCategory;
  }

  static class ClientDTO extends RegistryDTO {
    CustomerBillingDataDTO billingData;
  }

  static class AgentDTO extends RegistryDTO {
  }

  static class CustomerBillingDataDTO {
    AgentDTO agent;
  }

  static class BusinessCategoryDTO {
    Long id;
    String code;
  }

  /** Destination objects */

  static class Registry {
    BusinessCategory businessCategory;
  }

  static class Client extends Registry {
    CustomerBillingData billingData;
  }

  static class Agent extends Registry {
  }

  static class CustomerBillingData {
    Agent agent;
  }

  static class BusinessCategory {
    Long id;
    String code;
  }

  public void test() {
    ClientDTO source = new ClientDTO();

    source.billingData = new CustomerBillingDataDTO();
    source.billingData.agent = new AgentDTO();
    source.billingData.agent.businessCategory = new BusinessCategoryDTO();
    source.billingData.agent.businessCategory.id = 1l;
    source.billingData.agent.businessCategory.code = "code 1";

    source.businessCategory = new BusinessCategoryDTO();
    source.businessCategory.id = 2l;
    source.businessCategory.code = "code 2";

    Client dest = modelMapper.map(source, Client.class);

    assertEquals(dest.billingData.agent.businessCategory.id,
        source.billingData.agent.businessCategory.id);
    assertEquals(dest.billingData.agent.businessCategory.code,
        source.billingData.agent.businessCategory.code);
    assertEquals(dest.businessCategory.id, source.businessCategory.id);
    assertEquals(dest.businessCategory.code, source.businessCategory.code);
  }
}
