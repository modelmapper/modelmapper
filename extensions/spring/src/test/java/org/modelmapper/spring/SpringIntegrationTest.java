package org.modelmapper.spring;

import static org.testng.Assert.assertTrue;

import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class SpringIntegrationTest {
  static class Source {
  }

  static class Dest {
  }

  public void testFromSpring() {
    final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    RootBeanDefinition bean = new RootBeanDefinition(Dest.class, RootBeanDefinition.AUTOWIRE_NO, false);
    beanFactory.registerBeanDefinition("bean", bean);

    Provider<?> provider = SpringIntegration.fromSpring(beanFactory);
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setProvider(provider);

    assertTrue(mapper.map(new Source(), Dest.class) instanceof Dest);
  }
}
