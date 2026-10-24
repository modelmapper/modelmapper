package org.modelmapper.functional.lambda;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * Method reference setters of every primitive type must work without resolving the setter's type arguments.
 */
@Test
public class TypeMapLambdaPrimitiveSetterTest extends AbstractTest {
  static class Src {
    int i = 1;
    long l = 2L;
    boolean z = true;
    double d = 3.5;
    float f = 4.5f;
    short s = 5;
    byte b = 6;
    char c = 'x';

    int getI() { return i; }
    long getL() { return l; }
    boolean isZ() { return z; }
    double getD() { return d; }
    float getF() { return f; }
    short getS() { return s; }
    byte getB() { return b; }
    char getC() { return c; }
  }

  static class Dest {
    int ri;
    long rl;
    boolean rz;
    double rd;
    float rf;
    short rs;
    byte rb;
    char rc;

    void setRi(int v) { ri = v; }
    void setRl(long v) { rl = v; }
    void setRz(boolean v) { rz = v; }
    void setRd(double v) { rd = v; }
    void setRf(float v) { rf = v; }
    void setRs(short v) { rs = v; }
    void setRb(byte v) { rb = v; }
    void setRc(char v) { rc = v; }
  }

  public void shouldMapPrimitiveSetters() {
    modelMapper.createTypeMap(Src.class, Dest.class).addMappings(m -> {
      m.map(Src::getI, Dest::setRi);
      m.map(Src::getL, Dest::setRl);
      m.map(Src::isZ, Dest::setRz);
      m.map(Src::getD, Dest::setRd);
      m.map(Src::getF, Dest::setRf);
      m.map(Src::getS, Dest::setRs);
      m.map(Src::getB, Dest::setRb);
      m.map(Src::getC, Dest::setRc);
    });

    Dest dest = modelMapper.map(new Src(), Dest.class);

    assertEquals(dest.ri, 1);
    assertEquals(dest.rl, 2L);
    assertEquals(dest.rz, true);
    assertEquals(dest.rd, 3.5);
    assertEquals(dest.rf, 4.5f);
    assertEquals(dest.rs, (short) 5);
    assertEquals(dest.rb, (byte) 6);
    assertEquals(dest.rc, 'x');
  }
}
