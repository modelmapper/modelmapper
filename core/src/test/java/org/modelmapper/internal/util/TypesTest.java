package org.modelmapper.internal.util;

import static org.testng.Assert.assertEquals;

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
  public void shouldDeProxyJavassistProxy() {
    ProxyFactory proxyFactory = new ProxyFactory();
    proxyFactory.setSuperclass(ArrayList.class);
    Class<?> proxy = proxyFactory.createClass();

    assertEquals(Types.deProxy(proxy), ArrayList.class);
  }

  public void shouldDeProxyCGLibProxy() throws Exception {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(ArrayList.class);
    enhancer.setCallbackTypes(new Class[] { NoOp.class });
    Class<?> proxy = enhancer.createClass();

    assertEquals(Types.deProxy(proxy), ArrayList.class);
  }
}
