package org.modelmapper.guice;

import static org.testng.Assert.assertEquals;

import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Jonathan Halterman
 */
@Test
public class GuiceIntegrationTest {
  static class Source {
  }

  static class Dest {
  }

  public void testFromGuice() {
    final Dest dest = new Dest();

    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Dest.class).toInstance(dest);
      }
    });

    Provider<?> provider = GuiceIntegration.fromGuice(injector);
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setProvider(provider);

    assertEquals(mapper.map(new Source(), Dest.class), dest);
  }
}
