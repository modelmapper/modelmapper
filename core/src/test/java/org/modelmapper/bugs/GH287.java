package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import org.modelmapper.AbstractTest;
import org.modelmapper.ConfigurationException;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.ErrorMessage;
import org.modelmapper.spi.SourceGetter;
import org.testng.annotations.Test;

@Test
public class GH287 extends AbstractTest {
  static final class SourceA {
    private SourceB child;

    public SourceB getChild()  {
      return child;
    }

    public void setChild(SourceB child) {
      this.child = child;
    }
  }

  static final class SourceB {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  static final class DestA {
    private DestB child;

    public DestB getChild() {
      return child;
    }

    public void setChild(DestB child) {
      this.child = child;
    }
  }

  static final class DestB {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  public void shouldMapFinalClassWithoutNullPointerException() {
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);

    try {
      modelMapper.addMappings(new PropertyMap<SourceA, DestA>() {
        protected void configure() {
          map().getChild().setValue(source.getChild().getValue());
        }
      });
      fail();
    } catch (ConfigurationException e) {
      assertEquals(e.getErrorMessages().size(), 4);
      for (ErrorMessage errorMessage : e.getErrorMessages()) {
        assertTrue(errorMessage.getMessage().contains("Cannot map final type"));
      }
    }
  }

  public void shouldMapFinalClassWithoutNullPointerException2() {
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);

    try {
      modelMapper.addMappings(new PropertyMap<SourceB, DestB>() {
        protected void configure() {
          map().setValue(source.getValue());
        }
      });
      fail();
    } catch (ConfigurationException e) {
      assertEquals(e.getErrorMessages().size(), 2);
      for (ErrorMessage errorMessage : e.getErrorMessages()) {
        assertTrue(errorMessage.getMessage().contains("Cannot map final type"));
      }
    }
  }

  public void shouldMapFinalClassWithoutNullPointerException3() {
    modelMapper.getConfiguration().setImplicitMappingEnabled(false);

    try {
      modelMapper.typeMap(SourceA.class, DestA.class).addMapping(
          new SourceGetter<SourceA>() {
            public Object get(SourceA source) {
              return source.getChild().getValue();
            }
          },
          new DestinationSetter<DestA, String>() {
            public void accept(DestA destination, String value) {
              destination.getChild().setValue(value);
            }
          });
      fail();
    } catch (ConfigurationException e) {
      assertEquals(e.getErrorMessages().size(), 1);
      for (ErrorMessage errorMessage : e.getErrorMessages()) {
        assertTrue(errorMessage.getMessage().contains("Cannot map final type"));
      }
    }
  }
}
