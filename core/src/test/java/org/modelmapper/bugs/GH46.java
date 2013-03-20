package org.modelmapper.bugs;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/46
 */
@Test
public class GH46 {
  static class Class1 {
    BigInteger i;
    BigDecimal bd;

    public BigInteger getBI() {
      return i;
    }

    public void setBI(BigInteger i) {
      this.i = i;
    }

    public BigDecimal getBd() {
      return bd;
    }

    public void setBd(BigDecimal bd) {
      this.bd = bd;
    }
  }

  static class Class2 {
    BigInteger i2;
    BigDecimal bd2;

    public BigInteger getBI2() {
      return i2;
    }

    public void setBI2(BigInteger i2) {
      this.i2 = i2;
    }

    public BigDecimal getBd2() {
      return bd2;
    }

    public void setBd2(BigDecimal bd2) {
      this.bd2 = bd2;
    }
  }

  private PropertyMap<Class1, Class2> map = new PropertyMap<Class1, Class2>() {
    protected void configure() {
      map(source.getBI()).setBI2(null);
      map(source.getBd()).setBd2(null);
    }
  };

  public void test() {
    ModelMapper modelMapper;
    modelMapper = new ModelMapper();
    modelMapper.addMappings(map);

    Class1 c1 = new Class1();
    c1.setBI(BigInteger.valueOf(4));
    c1.setBd(new BigDecimal("123.123"));

    Class2 c2 = new Class2();
    modelMapper.map(c1, c2);
  }
}
