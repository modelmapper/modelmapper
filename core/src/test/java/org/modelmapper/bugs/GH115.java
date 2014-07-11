package org.modelmapper.bugs;

import static org.testng.Assert.assertEquals;

import org.modelmapper.AbstractTest;
import org.modelmapper.PropertyMap;
import org.testng.annotations.Test;

@Test
public class GH115 extends AbstractTest {
  static class Source {
    private String title;
    private byte[][][] someBytes;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public byte[][][] getSomeBytes() {
      return someBytes;
    }

    public void setSomeBytes(byte[][][] someBytes) {
      this.someBytes = someBytes;
    }
  }

  static class Dest {
    private String title;
    private byte[][][] someBytes;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public byte[][][] getSomeBytes() {
      return someBytes;
    }

    public void setSomeBytes(byte[][][] someBytes) {
      this.someBytes = someBytes;
    }
  }

  public void shouldPropertyMapArrays() throws Exception {
    modelMapper.addMappings(new PropertyMap<Source, Dest>() {
      @Override
      protected void configure() {
        map().setSomeBytes(source.getSomeBytes());
      }
    });

    Source source = new Source();
    source.title = "ABCDEF";
    source.someBytes = new byte[][][] { { source.title.getBytes() } };

    Dest dest = modelMapper.map(source, Dest.class);

    assertEquals(source.title, dest.title);
    assertEquals(source.title, new String(dest.someBytes[0][0]));
  }
}
