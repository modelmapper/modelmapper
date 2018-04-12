package org.modelmapper.internal;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNotNull;

import java.lang.reflect.InvocationHandler;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
@SuppressWarnings("unused")
public class ProxyFactoryTest {
  enum E {
    a, b, c
  }

  static class A1 {
    A1() {
    }

    private A1(List<String> a) {
    }
  }

  static class A2 {
    A2(String a, Object b, A1 c, Integer d, E e, boolean f) {
    }

    private A2() {
    }

    A2(boolean a, int b, Map<String, String> c, E d) {
    }
  }

  @Test
  public void shouldProxyTypesWithNonDefaultConstructor() {
    InvocationHandler interceptor = mock(InvocationHandler.class);
    A1 a1 = ProxyFactory.proxyFor(A1.class, interceptor, null);
    assertNotNull(a1);
    A2 a2 = ProxyFactory.proxyFor(A2.class, interceptor, null);
    assertNotNull(a2);
  }
}
