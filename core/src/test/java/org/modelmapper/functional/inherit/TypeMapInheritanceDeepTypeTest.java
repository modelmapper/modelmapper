package org.modelmapper.functional.inherit;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.modelmapper.Provider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class TypeMapInheritanceDeepTypeTest extends AbstractTest {

  interface SrcParent {
    SrcChild getChild();
  }

  interface SrcChild {
    String getOwnerId();
  }

  static class SrcParentImpl implements SrcParent {
    private SrcChild child;

    public SrcParentImpl(SrcChild child) {
      this.child = child;
    }

    public SrcChild getChild() {
      return child;
    }
  }

  static class SrcChildImpl implements SrcChild {
    private String ownerId;

    public SrcChildImpl(String ownerId) {
      this.ownerId = ownerId;
    }

    public String getOwnerId() {
      return ownerId;
    }
  }

  interface DestParent {
    DestChild getChild();
    void setChild(DestChild child);
  }

  interface DestChild {
    String getOwnerId();
    void setOwnerId(String ownerId);
  }

  static class DestParentImpl implements DestParent {
    private DestChild child;

    public DestChild getChild() {
      return child;
    }

    public void setChild(DestChild child) {
      this.child = child;
    }
  }

  static class DestChildImpl implements DestChild {
    private String ownerId;

    public String getOwnerId() {
      return ownerId;
    }

    public void setOwnerId(String ownerId) {
      this.ownerId = ownerId;
    }
  }

  static class ParentPropertyMap extends PropertyMap<SrcParent, DestParent> {
    @Override
    protected void configure() {
      with(new Provider<Object>() {
        public Object get(ProvisionRequest<Object> request) {
          return new DestChildImpl();
        }
      }).map(source.getChild()).setChild(null);
    }
  }

  static class ChildPropertyMap extends PropertyMap<SrcChild, DestChild> {
    @Override
    protected void configure() {
      map().setOwnerId(source.getOwnerId());
    }
  }

  @BeforeMethod
  public void setUp() {
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);
  }

  @Test
  public void shouldMapIfInclude() throws Exception {
    modelMapper.addMappings(new ParentPropertyMap())
        .include(SrcParentImpl.class, DestParentImpl.class);

    modelMapper.addMappings(new ChildPropertyMap())
        .include(SrcChildImpl.class, DestChild.class);

    SrcParent src = new SrcParentImpl(new SrcChildImpl("foo"));
    DestParentImpl dest = modelMapper.map(src, DestParentImpl.class);
    assertEquals(dest.getChild().getOwnerId(), "foo");
  }
}