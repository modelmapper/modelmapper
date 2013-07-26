package org.modelmapper.dagger;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

/**
 * @author Jonathan Halterman
 */
@Test
public class DaggerIntegrationTest {
  static class Order {
  }

  static class OrderDTO {
  }

  @Module(injects = OrderDTO.class)
  static class WarehouseModule {
    private final OrderDTO orderDTO;

    public WarehouseModule(OrderDTO orderDTO) {
      this.orderDTO = orderDTO;
    }

    @Provides
    OrderDTO providesOrderDTO() {
      return orderDTO;
    }
  }

  public void testFromDagger() {
    OrderDTO orderDTO = new OrderDTO() {
    };

    ObjectGraph objectGraph = ObjectGraph.create(new WarehouseModule(orderDTO));

    Provider<?> provider = DaggerIntegration.fromDagger(objectGraph);
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setProvider(provider);

    assertEquals(mapper.map(new Order(), OrderDTO.class), orderDTO);
  }
}
