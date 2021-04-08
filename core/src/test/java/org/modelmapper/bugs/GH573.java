package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.modelmapper.AbstractTest;
import org.modelmapper.Provider;
import org.testng.annotations.Test;

@Test
public class GH573 extends AbstractTest {

  public void shouldProviderGetRightSource() {
    final Map<Class<?>, Object> provider = new HashMap<Class<?>, Object>();
    provider.put(A.class, new B());
    provider.put(AA.class, new BB());
    provider.put(AAA.class, new BBB());

    modelMapper.getConfiguration().setProvider(new Provider<Object>() {
      @Override
      public Object get(ProvisionRequest<Object> request) {
        Class<?> sourceType = request.getSource().getClass();
        return provider.get(sourceType);
      }
    });

    B destination = modelMapper.map(new A(), B.class);
    assertEquals(destination.x.y.z, "10");
    assertSame(provider.get(A.class), destination);
    assertSame(provider.get(AA.class), destination.x);
    assertSame(provider.get(AAA.class), destination.x.y);
  }

  private static class A {
    AA x = new AA();
  }

  private static class AA {
    AAA y = new AAA();
  }

  private static class AAA {
    int z = 10;
  }

  private static class B {
    BB x;
  }

  private static class BB {
    BBB y;
  }

  private static class BBB {
    String z;
  }
}
