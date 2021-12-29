package org.modelmapper.functional.lambda;

import org.modelmapper.AbstractTest;
import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

@Test
public class TypeMapLambdaDeepMapTest extends AbstractTest {
  static class ParentSrc {
    Src src;

    public ParentSrc(Src src) {
      this.src = src;
    }

    public Src getSrc() {
      return src;
    }

    public void setSrc(Src src) {
      this.src = src;
    }
  }

  static class Src {
    String srcText;

    public Src(String srcText) {
      this.srcText = srcText;
    }

    public String getSrcText() {
      return srcText;
    }

    public void setSrcText(String srcText) {
      this.srcText = srcText;
    }
  }

  static class ParentDest {
    Dest dest;

    public Dest getDest() {
      return dest;
    }

    public void setDest(Dest dest) {
      this.dest = dest;
    }
  }

  static class Dest {
    String destText;

    public String getDestText() {
      return destText;
    }

    public void setDestText(String destText) {
      this.destText = destText;
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);
  }

  public void shouldAddMappingDeeply() {
    TypeMap<ParentSrc, ParentDest> typeMap = modelMapper.createTypeMap(ParentSrc.class, ParentDest.class);
    typeMap.addMapping(source -> source.getSrc().getSrcText(),
        (destination, value) -> destination.getDest().setDestText((String) value));

    typeMap.validate();
    assertEquals(typeMap.map(new ParentSrc(new Src("bar"))).getDest().destText, "bar");
  }

  public void shouldAddMappingWithProvider() {
    final Dest dest = new Dest();

    TypeMap<ParentSrc, ParentDest> typeMap = modelMapper.createTypeMap(ParentSrc.class, ParentDest.class);
    typeMap.addMappings(
        mapping -> mapping.with((Provider<Dest>) request -> dest).map(ParentSrc::getSrc, ParentDest::setDest));

    typeMap.validate();
    assertSame(typeMap.map(new ParentSrc(new Src("foo"))).dest, dest);
  }
}
