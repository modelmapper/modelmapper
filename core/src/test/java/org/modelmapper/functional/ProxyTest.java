package org.modelmapper.functional;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class ProxyTest extends AbstractTest {
  public static class Source {
    public String getValue() {
      return "dummy";
    }
  }

  public static class Dest {
    String value;
  }

  static class TestCallback implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy)
        throws Throwable {
      if (method.getName().equals("getValue"))
        return "abc";
      return null;
    }
  }

  public void shouldMapProxies() throws Exception {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(Source.class);
    enhancer.setCallbackTypes(new Class[] { TestCallback.class });
    enhancer.setCallbacks(new Callback[] { new TestCallback() });
    Object proxy = enhancer.create();

    Dest dest = modelMapper.map(proxy, Dest.class);
    assertEquals(dest.value, "abc");
  }
}
