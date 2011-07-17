package org.modelmapper.functional.loosematching;

import org.modelmapper.AbstractTest;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.internal.MatchingStrategyTestSupport;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests a scenario where the destination members are fulfilled from various parts of the source
 * object graph. Requires the loose matching strategy.
 * 
 * <pre>
 * Entity      DTO
 *   A a;        A1 aa
 *     B b;        B1 bb;
 *     C c;          C1 c;
 *   D d;          D1 dd;
 *     E e;          E1 e;
 *       F f;        F1 f;
 *   G g;            G1 g;
 *   
 * Entity=>DTO
 *   a.c->aa.bb.c
 *   d.e->aa.dd.e
 *   d.e.f->aa.dd.f
 *   g->aa.dd.g
 * </pre>
 * 
 * @author Jonathan Halterman
 */
@Test(groups = "functional")
public class LooseMappingTest2 extends AbstractTest {
  static class Entity {
    A a;
    D d;
    G g;
  }

  static class A {
    B b;
    C c;
  }

  static class B {
  }

  static class C {
    String c;
  }

  static class D {
    E e;
  }

  static class E {
    String e;
    F f;
  }

  static class F {
    String f;
  }

  static class G {
    String g;
  }

  static class DTO {
    A1 aa;
  }

  static class A1 {
    B1 bb;
    D1 dd;
  }

  static class B1 {
    C1 c;
  }

  static class C1 {
    String c;
  }

  static class D1 {
    E1 e;
    F1 f;
    G1 g;
  }

  static class E1 {
    String e;
  }

  static class F1 {
    String f;
  }

  static class G1 {
    String g;
  }

  @Override
  @BeforeMethod
  protected void initContext() {
    super.initContext();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
  }

  /**
   * Asserts that the expected matches are found.
   * 
   * <pre>
   * A a/C c -> aa/bb/c
   * D d/E e -> aa/dd/e
   * D d/E e/F f -> aa/dd/f
   * G g -> aa/dd/g
   * </pre>
   */
  public void assertMatches() {
    MatchingStrategyTestSupport tester = new MatchingStrategyTestSupport(MatchingStrategies.LOOSE);
    tester.match(A.class, "a").$(C.class, "c").to("aa", "bb", "c").assertMatch();
    tester.match(D.class, "d").$(E.class, "e").to("aa", "dd", "e").assertMatch();
    tester.match(D.class, "d").$(E.class, "e").$(F.class, "f").to("aa", "dd", "f").assertMatch();
    tester.match(G.class, "g").to("aa", "dd", "g").assertMatch();
  }

  public void shouldValidate() {
    modelMapper.getTypeMap(Entity.class, DTO.class);
    modelMapper.validate();
  }
}
