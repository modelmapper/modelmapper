package org.modelmapper.internal.util;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.testng.annotations.Test;

@Test
public class MembersTest {
  interface IInterface {
    String getter();

    void setter(String value);
  }

  static class Parent implements IInterface {
    String field;
    String parentField;

    public String getter() {
      return null;
    }

    public void setter(String value) {
    }
  }

  static class Child extends Parent {
    Integer field;
    String childField;

    String childGetter() {
      return null;
    }

    void childSetter(String value) {
    }
  }

  interface DerivedInterface extends IInterface {
  }

  interface MoreDerivedInterface extends DerivedInterface {
  }

  public void shouldGetMethodForInterface() {
    Method getter = Members.methodFor(IInterface.class, "getter", (Class<?>[]) null);
    Method setter = Members.methodFor(IInterface.class, "setter", new Class<?>[] { String.class });
    assertEquals(getter.getDeclaringClass(), IInterface.class);
    assertEquals(setter.getDeclaringClass(), IInterface.class);
  }

  public void shouldGetMethodForDerivedInterface() {
    Method getter = Members.methodFor(DerivedInterface.class, "getter", (Class<?>[]) null);
    Method setter = Members.methodFor(DerivedInterface.class, "setter", new Class<?>[] { String.class });
    assertEquals(getter.getDeclaringClass(), IInterface.class);
    assertEquals(setter.getDeclaringClass(), IInterface.class);
 }

  public void shouldGetMethodForMoreDerivedInterface() {
    Method getter = Members.methodFor(MoreDerivedInterface.class, "getter", (Class<?>[]) null);
    Method setter = Members.methodFor(MoreDerivedInterface.class, "setter", new Class<?>[] { String.class });
    assertEquals(getter.getDeclaringClass(), IInterface.class);
    assertEquals(setter.getDeclaringClass(), IInterface.class);
  }

  public void shouldGetMethodForClass() {
    Method getter = Members.methodFor(Parent.class, "getter", (Class<?>[]) null);
    Method setter = Members.methodFor(Parent.class, "setter", new Class<?>[] { String.class });
    assertEquals(getter.getDeclaringClass(), Parent.class);
    assertEquals(setter.getDeclaringClass(), Parent.class);
  }

  public void shuoldGetMethodForChildClass() {
    Method getter = Members.methodFor(Child.class, "getter", (Class<?>[]) null);
    Method setter = Members.methodFor(Child.class, "setter", new Class<?>[] { String.class });
    Method childGetter = Members.methodFor(Child.class, "childGetter", (Class<?>[]) null);
    Method childSetter = Members.methodFor(Child.class, "childSetter",
        new Class<?>[] { String.class });
    assertEquals(getter.getDeclaringClass(), Parent.class);
    assertEquals(setter.getDeclaringClass(), Parent.class);
    assertEquals(childGetter.getDeclaringClass(), Child.class);
    assertEquals(childSetter.getDeclaringClass(), Child.class);
  }

  public void shouldGetFieldForClass() {
    Field field = Members.fieldFor(Parent.class, "field");
    Field parentField = Members.fieldFor(Parent.class, "parentField");
    assertEquals(field.getDeclaringClass(), Parent.class);
    assertEquals(parentField.getDeclaringClass(), Parent.class);
  }

  public void shouldGetFieldForChildClas() {
    Field field = Members.fieldFor(Child.class, "field");
    Field parentField = Members.fieldFor(Child.class, "parentField");
    Field childField = Members.fieldFor(Child.class, "childField");
    assertEquals(field.getType(), Integer.class);
    assertEquals(field.getDeclaringClass(), Child.class);
    assertEquals(parentField.getDeclaringClass(), Parent.class);
    assertEquals(childField.getDeclaringClass(), Child.class);
  }
}
