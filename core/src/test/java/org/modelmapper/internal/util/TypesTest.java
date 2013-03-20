package org.modelmapper.internal.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
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

  public void testBestConstructorOf() {
    Class<?>[] paramTypes = Types.bestConstructorOf(BigDecimal.class.getConstructors())
        .getParameterTypes();
    assertEquals(paramTypes.length, 1);
    assertTrue(paramTypes[0].isPrimitive());
  }

  public void shouldConstructPredefinedTypes() throws Exception {
    assertTrue(Types.construct(BigInteger.class, BigInteger.class) instanceof BigInteger);
    assertTrue(Types.construct(BigDecimal.class, BigInteger.class) instanceof BigDecimal);
  }
}
