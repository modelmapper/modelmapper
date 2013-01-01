package org.modelmapper.bugs;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * https://github.com/jhalterman/modelmapper/issues/31
 */
@Test
public class GH31 extends AbstractTest {
  public abstract static class Catalog<T extends Product> {
    T product;
    String label;
  }

  public abstract static class Product<T extends Catalog> {
    T catalog;
    String label;
  }

  public static class CatalogOne extends Catalog<ProductOne> {
  }

  public static class ProductOne extends Product<CatalogOne> {
  }

  public static class CatalogTwo extends Catalog<ProductTwo> {
  }

  public static class ProductTwo extends Product<CatalogTwo> {
  }

  public void test() {
    CatalogOne source = new CatalogOne();
    source.label = "catalog1";
    source.product = new ProductOne();
    source.product.label = "product1";
    source.product.catalog = source;

    CatalogTwo dest = modelMapper.map(source, CatalogTwo.class);

    assertTrue(dest.product instanceof ProductTwo);
    assertEquals(dest.label, source.label);
    assertEquals(dest.product.label, source.product.label);
    assertEquals(dest.product.catalog, dest);
  }
}
