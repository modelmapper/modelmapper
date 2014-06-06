package org.modelmapper.internal;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class ProxyFactoryTest {
  enum E {
    a, b, c
  }

  static class A1 {
    A1() {
    }

    @SuppressWarnings("unused")
    private A1(List<String> a) {
    }
  }

  static class A2 {
    A2(String a, Object b, A1 c, Integer d, E e, boolean f) {
    }

    @SuppressWarnings("unused")
    private A2() {
    }

    A2(boolean a, int b, Map<String, String> c, E d) {
    }
  }

  @Test
  public void shouldProxyTypesWithNonDefaultConstructor() {
    MethodInterceptor interceptor = mock(MethodInterceptor.class);
    assertTrue(ProxyFactory.proxyFor(A1.class, interceptor, null) instanceof A1);
    assertTrue(ProxyFactory.proxyFor(A2.class, interceptor, null) instanceof A2);
  }
}
