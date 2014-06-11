package org.modelmapper.internal;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.UUID;

import org.modelmapper.AbstractTest;
import org.testng.annotations.Test;

@Test
public class GH111 extends AbstractTest {
  interface ReferenceableResource<T> {
    T getReference();
  }

  class UpdatePersonResource implements ReferenceableResource<UUID> {
    private UUID reference;

    public UUID getReference() {
      return reference;
    }

    public void setReference(UUID reference) {
      this.reference = reference;
    }
  }

  interface ReferenceableResource1 {
    UUID getReference();
  }

  class UpdatePersonResource1 implements ReferenceableResource1 {
    private UUID reference;

    public UUID getReference() {
      return reference;
    }

    public void setReference(UUID reference) {
      this.reference = reference;
    }
  }

  @Test
  public void typeInfoReference() {
    // given:
    TypeInfo<UpdatePersonResource> typeInfo = new TypeInfoImpl<UpdatePersonResource>(
        new UpdatePersonResource(), UpdatePersonResource.class,
        (InheritingConfiguration) modelMapper.getConfiguration());
    // when:
    Map<String, Accessor> accessors = typeInfo.getAccessors();
    // then:
    assertEquals(accessors.get("reference").getType(), UUID.class);
  }

  @Test
  public void typeInfoReference1() {
    // given:
    TypeInfo<UpdatePersonResource1> typeInfo = new TypeInfoImpl<UpdatePersonResource1>(
        new UpdatePersonResource1(), UpdatePersonResource1.class,
        (InheritingConfiguration) modelMapper.getConfiguration());
    // when:
    Map<String, Accessor> accessors = typeInfo.getAccessors();
    // then:
    assertEquals(accessors.get("reference").getType(), UUID.class);
  }
}
