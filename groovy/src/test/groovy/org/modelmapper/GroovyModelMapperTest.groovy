package org.modelmapper

import org.testng.annotations.Test

@Test
class GroovyModelMapperTest {
  static class A {
    String propertyA
  }

  static class B {
    String propertyB
  }

  static class Map extends PropertyMap<A, B> {
    @Override
    protected void configure() {
      map().setPropertyB(source.getPropertyA())
    }
  }

  public void shouldCreateExplicitMappings() {
    final modelMapper = new ModelMapper()
    modelMapper.addMappings(new Map())
    modelMapper.validate()
  }
}