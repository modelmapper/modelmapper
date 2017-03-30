package org.modelmapper.internal;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

import org.modelmapper.config.Configuration;
import org.modelmapper.spi.MatchingStrategy;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyType;

/**
 * Provides support for testing matching strategies.
 * 
 * @author Jonathan Halterman
 */
public class MatchingStrategyTestSupport {
  protected final Configuration configuration;

  public MatchingStrategyTestSupport() {
    configuration = new InheritingConfiguration();
  }

  public MatchingStrategyTestSupport(MatchingStrategy matchingStrategy) {
    configuration = new InheritingConfiguration();
    configuration.setMatchingStrategy(matchingStrategy);
  }

  public MatchBuilder match(Class<?> declaringClass, Class<?> memberType, String memberName) {
    return new MatchBuilder(declaringClass, memberType, memberName);
  }

  /**
   * Creates a new MatchBuilder for the {@code memberNames} where the memberType will be
   * Object.class.
   */
  public MatchBuilder match(String... memberNames) {
    return new MatchBuilder(memberNames);
  }

  public MatchBuilder match(Class<?> memberType, String memberName) {
    return new MatchBuilder(memberType, memberName);
  }

  public class MatchBuilder {
    private final Class<?> declaringClass;
    private final PropertyNameInfoImpl memberNameInfo;

    MatchBuilder(Class<?> declaringClass) {
      this.declaringClass = declaringClass;
      memberNameInfo = new PropertyNameInfoImpl(declaringClass, configuration);
    }

    MatchBuilder(Class<?> declaringClass, Class<?> memberClass, String memberName) {
      this(declaringClass);
      $(memberClass, memberName);
    }

    MatchBuilder(String[] memberNames) {
      this(Object.class);
      for (String memberName : memberNames)
        $(Object.class, memberName);
    }

    MatchBuilder(Class<?> memberClass, String memberName) {
      this(Object.class);
      $(memberClass, memberName);
    }

    /**
     * Captures source member info. Assumes MemberType.FIELD.
     */
    public MatchBuilder $(Class<?> memberClass, String memberName) {
      memberNameInfo.pushSource(memberName, new TestMemberInfo(declaringClass, memberClass,
          PropertyType.FIELD, memberName));
      return this;
    }

    /**
     * Captures destination member info. Assumes memberClass Object.class and MemberType.FIELD.
     */
    public MatchBuilder $(String memberName) {
      memberNameInfo.pushDestination(memberName, new TestMemberInfo(declaringClass, Object.class,
          PropertyType.FIELD, memberName));
      return this;
    }

    public void assertNoMatch() {
      assertFalse(configuration.getMatchingStrategy().matches(memberNameInfo));
    }

    public void assertMatch() {
      assertTrue(configuration.getMatchingStrategy().matches(memberNameInfo));
    }

    public MatchBuilder to(PropertyType memberType, String memberName) {
      memberNameInfo.pushDestination(memberName, new TestMemberInfo(Object.class, Object.class,
          memberType, memberName));
      return this;
    }

    /**
     * Assumes MemberType.FIELD.
     */
    public MatchBuilder to(String... memberNames) {
      for (String memberName : memberNames)
        memberNameInfo.pushDestination(memberName, new TestMemberInfo(Object.class, Object.class,
            PropertyType.FIELD, memberName));
      return this;
    }
  }

  static class TestMember implements Member {
    Class<?> declaringClass;

    TestMember(Class<?> declaringClass) {
      this.declaringClass = declaringClass;
    }

    public Class<?> getDeclaringClass() {
      return declaringClass;
    }

    public int getModifiers() {
      return 0;
    }

    public String getName() {
      return null;
    }

    public boolean isSynthetic() {
      return false;
    }
  }

  static class TestMemberInfo implements PropertyInfo, Accessor, Mutator {
    Member member;
    Class<?> memberClass;
    PropertyType memberType;
    String memberName;

    TestMemberInfo(Class<?> declaringClass, Class<?> memberClass, PropertyType memberType,
        String memberName) {
      member = new TestMember(declaringClass);
      this.memberClass = memberClass;
      this.memberName = memberName;
      this.memberType = memberType;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
      return null;
    }

    public Member getMember() {
      return member;
    }

    public PropertyType getPropertyType() {
      return memberType;
    }

    public Class<?> getType() {
      return memberClass;
    }

    public void setValue(Object subject, Object value) {
    }

    public Object getValue(Object subject) {
      return null;
    }

    public Type getGenericType() {
      return null;
    }

    public String getName() {
      return memberName;
    }

    public Class<?> getInitialType() {
      return null;
    }

    public TypeInfo<?> getTypeInfo(InheritingConfiguration configuration) {
      return TypeInfoRegistry.typeInfoFor(memberClass, configuration);
    }
  }
}
