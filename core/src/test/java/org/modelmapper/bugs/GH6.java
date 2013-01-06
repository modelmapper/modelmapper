package org.modelmapper.bugs;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

/**
 * https://github.com/jhalterman/modelmapper/issues/6
 * 
 * @author Jonathan Halterman
 */
@Test
public class GH6 extends AbstractTest {
  public static class Source {
    SourceMessage message;

    public SourceMessage getMessage() {
      return message;
    }

    public void setMessage(SourceMessage message) {
      this.message = message;
    }
  }

  public static class SourceMessage {
    SourceMessageBody body;

    public SourceMessageBody getBody() {
      return body;
    }

    public void setBody(SourceMessageBody body) {
      this.body = body;
    }
  }

  public static class SourceMessageBody {
    String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  public static class Dest {
    DestMessage message;

    public DestMessage getMessage() {
      return message;
    }

    public void setMessage(DestMessage message) {
      this.message = message;
    }
  }

  public static class DestMessage {
    DestMessageBody body;

    public DestMessageBody getBody() {
      return body;
    }

    public void setBody(DestMessageBody body) {
      this.body = body;
    }
  }

  public static class DestMessageBody {
    String value = "originalValue";

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  public void shouldNotInstantiateIntermediateObjects() {
    Dest dest = modelMapper.map(new Source(), Dest.class);
    assertNull(dest.message);

    Source source = new Source();
    source.message = new SourceMessage();
    dest = modelMapper.map(source, Dest.class);
    assertNull(dest.message);
  }

  public void shouldNotInstantiateIntermediateObjectOnProvidedDestination() {
    Source source = new Source();
    Dest dest = new Dest();
    dest.message = new DestMessage();
    modelMapper.map(source, dest);
    assertNull(dest.message.body);

    source.message = new SourceMessage();
    modelMapper.map(source, dest);
    assertNull(dest.message.body);
  }

  public void shouldIgnoreIntermediateDestinationWhenSourceIsNotNull() {
    Source source = new Source();
    source.message = new SourceMessage();
    Dest dest = new Dest();
    dest.message = new DestMessage();
    modelMapper.map(source, dest);
    assertNotNull(dest.message);

    source.message.body = new SourceMessageBody();
    dest.message.body = new DestMessageBody();
    modelMapper.map(source, dest);
    assertNotNull(dest.message.body);
  }

  /**
   * Cannot support this for now due to potentially asymmetric mappings. We don't know for sure that
   * source.message maps to dest.message and therefore can't null dest.message just because
   * source.message is null.
   * 
   * TODO enable once better information is captured for asymmetric mappings
   */
  @Test(enabled = false)
  public void shouldNullifyIntermediateDestinationWhenSourceIsNull() {
    Source source = new Source();
    Dest dest = new Dest();
    dest.message = new DestMessage();
    modelMapper.map(source, dest);
    assertNull(dest.message);

    source.message = new SourceMessage();
    dest.message = new DestMessage();
    dest.message.body = new DestMessageBody();
    modelMapper.map(source, dest);
    assertNull(dest.message.body);
  }
}
