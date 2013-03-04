package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.TypeToken;
import org.modelmapper.spi.MappingContext;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/40
 * 
 * Originally from:
 * 
 * https://groups.google.com/forum/#!topic/modelmapper/z7YjIY0Ms9c
 */
@Test
public class GH40 extends AbstractTest {
  static class CustomerEntity {
    List<ConcreteInsurance1Entity> concreteInsurance1;
    List<ConcreteInsurance2Entity> concreteInsurance2;
    List<SpecialInsuranceContainer> specials;
  }

  static class ConcreteInsurance1Entity {
    final String id;

    public ConcreteInsurance1Entity(String id) {
      this.id = id;
    }
  }

  static class ConcreteInsurance2Entity {
    final String id;

    public ConcreteInsurance2Entity(String id) {
      this.id = id;
    }
  }

  static class SpecialInsuranceContainer {
    ConcreteSpecialInsuranceEntity special;

    public SpecialInsuranceContainer(String id) {
      this.special = new ConcreteSpecialInsuranceEntity(id);
    }
  }

  static class ConcreteSpecialInsuranceEntity {
    final String id;

    public ConcreteSpecialInsuranceEntity(String id) {
      this.id = id;
    }
  }

  // ------------------------

  interface BaseInsuranceInterface {
    String getId();
  }

  static class CustomerDTO {
    List<BaseInsuranceInterface> insurances;
  }

  static class ConcreteInsurance1DTO implements BaseInsuranceInterface {
    String id;

    public String getId() {
      return id;
    }
  }

  static class ConcreteInsurance2DTO implements BaseInsuranceInterface {
    String id;

    public String getId() {
      return id;
    }
  }

  static class ConcreteSpecialInsuranceDTO implements BaseInsuranceInterface {
    String id;

    public String getId() {
      return id;
    }
  }

  public void test() {
    // Create a converter that combines the 3 insurance lists
    modelMapper.createTypeMap(CustomerEntity.class, CustomerDTO.class).setConverter(
        new Converter<CustomerEntity, CustomerDTO>() {
          public CustomerDTO convert(MappingContext<CustomerEntity, CustomerDTO> ctx) {
            List<BaseInsuranceInterface> insurances = new ArrayList<BaseInsuranceInterface>();
            ctx.getDestination().insurances = insurances;

            // Map and populate concrete insurance 1 list
            MappingContext<List<ConcreteInsurance1Entity>, List<ConcreteInsurance1DTO>> c1 = ctx.create(
                ctx.getSource().concreteInsurance1, new TypeToken<List<ConcreteInsurance1DTO>>() {
                }.getType());
            insurances.addAll(ctx.getMappingEngine().map(c1));

            // Map and populate concrete insurance 2 list
            MappingContext<List<ConcreteInsurance2Entity>, List<ConcreteInsurance2DTO>> c2 = ctx.create(
                ctx.getSource().concreteInsurance2, new TypeToken<List<ConcreteInsurance2DTO>>() {
                }.getType());
            insurances.addAll(ctx.getMappingEngine().map(c2));

            // Map and populate special insurance list list
            MappingContext<List<SpecialInsuranceContainer>, List<ConcreteSpecialInsuranceDTO>> c3 = ctx.create(
                ctx.getSource().specials, new TypeToken<List<ConcreteSpecialInsuranceDTO>>() {
                }.getType());
            insurances.addAll(ctx.getMappingEngine().map(c3));

            return ctx.getDestination();
          }
        });

    // Create a converter that unwraps the container
    modelMapper.createTypeMap(SpecialInsuranceContainer.class, ConcreteSpecialInsuranceDTO.class)
        .setConverter(new Converter<SpecialInsuranceContainer, ConcreteSpecialInsuranceDTO>() {
          public ConcreteSpecialInsuranceDTO convert(
              MappingContext<SpecialInsuranceContainer, ConcreteSpecialInsuranceDTO> ctx) {
            return ctx.getMappingEngine().map(
                ctx.create(ctx.getSource().special, ctx.getDestinationType()));
          }
        });

    CustomerEntity entity = new CustomerEntity();
    entity.concreteInsurance1 = Arrays.asList(new ConcreteInsurance1Entity("1"),
        new ConcreteInsurance1Entity("2"));
    entity.concreteInsurance2 = Arrays.asList(new ConcreteInsurance2Entity("3"),
        new ConcreteInsurance2Entity("4"));
    entity.specials = Arrays.asList(new SpecialInsuranceContainer("5"),
        new SpecialInsuranceContainer("6"));

    CustomerDTO dto = modelMapper.map(entity, CustomerDTO.class);

    int i = 0;
    for (; i < entity.concreteInsurance1.size(); i++)
      assertEquals(dto.insurances.get(i).getId(), entity.concreteInsurance1.get(i).id);
    for (int j = 0; j < entity.concreteInsurance2.size(); i++, j++)
      assertEquals(dto.insurances.get(i).getId(), entity.concreteInsurance2.get(j).id);
    for (int j = 0; j < entity.specials.size(); i++, j++)
      assertEquals(dto.insurances.get(i).getId(), entity.specials.get(j).special.id);
  }
}
