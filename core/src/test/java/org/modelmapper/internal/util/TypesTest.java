package org.modelmapper.internal.util;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import javassist.util.proxy.ProxyFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
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

  @BeforeMethod
  public void setup() {
    if (JavaVersions.getMajorVersion() >= 16) {
      throw new SkipException("Required java < 16");
    }
  }

  public void shouldDeProxyJavassistProxy() throws Exception {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(Foo.class);
    Object proxy = proxyFactory.create(new Class[0], new Object[0]);

    assertEquals(Types.deProxiedClass(proxy), Foo.class);
  }

  public void shouldDeProxyCGLibProxy() {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(ArrayList.class);
    enhancer.setCallbackTypes(new Class[] { NoOp.class });
    Object proxy = enhancer.create();

    assertEquals(Types.deProxiedClass(proxy), ArrayList.class);
  }

  public void shouldDeProxyDynamicProxy() {
    final Object proxy = Proxy.newProxyInstance(TypesTest.class.getClassLoader(),
            new Class<?>[]{Bar.class}, new NullInvocationHandler());
    assertEquals(Types.deProxiedClass(proxy), Bar.class);
  }
}
