package org.modelmapper.internal.util;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import javassist.util.proxy.ProxyFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test
public class TypesTest {
  static class Foo {
  }
  interface Bar {
  }

  static class NullInvocationHandler implements InvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) {
      return null;
    }
  }

  public void shouldDeProxyJavassistProxy() {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(Foo.class);
    Class<?> proxy = proxyFactory.createClass();

    assertEquals(Types.deProxy(proxy), Foo.class);
  }

  public void shouldDeProxyCGLibProxy() {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(ArrayList.class);
    enhancer.setCallbackTypes(new Class[] { NoOp.class });
    Class<?> proxy = enhancer.createClass();

    assertEquals(Types.deProxy(proxy), ArrayList.class);
  }

  public void shouldDeProxyDynamicProxy() {
    final Object proxy = Proxy.newProxyInstance(TypesTest.class.getClassLoader(),
            new Class<?>[]{Bar.class}, new NullInvocationHandler());
    assertEquals(Types.deProxy(proxy.getClass()), Bar.class);
  }
}
