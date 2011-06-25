/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.modelmapper.internal;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import org.modelmapper.config.Configuration;
import org.modelmapper.config.Configuration.AccessLevel;
import org.modelmapper.spi.PropertyInfo;
import org.modelmapper.spi.PropertyType;
import org.modelmapper.spi.NameTransformer;
import org.modelmapper.spi.NameableType;
import org.modelmapper.spi.NamingConvention;

/**
 * A TypeInfo implementation that lazily reflects members.
 * 
 * @author Jonathan Halterman
 */
class TypeInfoImpl<T> implements TypeInfo<T> {
  private final Class<T> type;
  private final Configuration configuration;
  private Map<String, Accessor> accessors;
  private Map<String, Mutator> mutators;

  TypeInfoImpl(Class<T> type, Configuration configuration) {
    this.type = type;
    this.configuration = configuration;
  }

  private static class InitRequest<M extends AccessibleObject & Member, PI extends PropertyInfo> {
    Map<String, PI> propertyInfo;
    PropertyResolver<M, PI> propertyResolver;
    PropertyType propertyType;
    Configuration config;
    AccessLevel accessLevel;
    NamingConvention namingConvention;
    NameTransformer nameTransformer;
  }

  static boolean canAccessMember(Member member, AccessLevel accessLevel) {
    int mod = member.getModifiers();
    switch (accessLevel) {
    default:
    case PUBLIC:
      return Modifier.isPublic(mod);
    case PROTECTED:
      return Modifier.isPublic(mod) || Modifier.isProtected(mod);
    case PACKAGE_PRIVATE:
      return Modifier.isPublic(mod) || Modifier.isProtected(mod) || !Modifier.isPrivate(mod);
    case PRIVATE:
      return true;
    }
  }

  /**
   * Populates the {@code propertyInfo} with {@code propertyResolver} resolved property info for
   * properties that are accessible by the {@code accessLevel} and satisfy the
   * {@code namingConvention}.
   */
  private static <M extends AccessibleObject & Member, PI extends PropertyInfo> void buildProperties(
      Class<?> initialType, Class<?> type, InitRequest<M, PI> initRequest) {

    for (M member : initRequest.propertyResolver.membersFor(type)) {
      if (canAccessMember(member, initRequest.accessLevel)
          && initRequest.propertyResolver.isValid(member)
          && initRequest.namingConvention.applies(member.getName(), initRequest.propertyType)) {

        String name = initRequest.nameTransformer.transform(member.getName(), PropertyType.FIELD
            .equals(initRequest.propertyType) ? NameableType.FIELD : NameableType.METHOD);
        PI info = initRequest.propertyResolver.propertyInfoFor(initialType, member,
            initRequest.config, name);
        initRequest.propertyInfo.put(name, info);

        if (!Modifier.isPublic(member.getModifiers())
            || !Modifier.isPublic(member.getDeclaringClass().getModifiers()))
          try {
            member.setAccessible(true);
          } catch (SecurityException e) {
            throw new AssertionError(e);
          }
      }
    }

    Class<?> superType = type.getSuperclass();
    if (superType != null && superType != Object.class)
      buildProperties(initialType, superType, initRequest);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof TypeInfo))
      return false;
    TypeInfo<?> typeInfo = (TypeInfo<?>) obj;
    return type.equals(typeInfo.getType()) && configuration.equals(typeInfo.getConfiguration());
  }

  /**
   * Lazily initializes and gets accessors.
   */
  @Override
  public synchronized Map<String, Accessor> getAccessors() {
    initAccessors();
    return accessors;
  }

  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Lazily initializes and gets mutators.
   */
  @Override
  public synchronized Map<String, Mutator> getMutators() {
    initMutators();
    return mutators;
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  @Override
  public int hashCode() {
    return type.hashCode() * 31 + configuration.hashCode();
  }

  @Override
  public String toString() {
    return type.toString();
  }

  Mutator mutatorForAccessor(String accessorMethodName) {
    return getMutators()
        .get(
            configuration.getSourceNameTransformer().transform(accessorMethodName,
                NameableType.METHOD));
  }

  private <M extends AccessibleObject & Member, PI extends PropertyInfo> void buildProperties(
      Map<String, PI> propertyInfo, PropertyResolver<M, PI> propertyResolver) {
    InitRequest<M, PI> initRequest = new InitRequest<M, PI>();
    initRequest.propertyInfo = propertyInfo;
    initRequest.propertyResolver = propertyResolver;
    initRequest.config = configuration;

    if (propertyInfo == accessors) {
      initRequest.namingConvention = configuration.getSourceNamingConvention();
      initRequest.nameTransformer = configuration.getSourceNameTransformer();
    } else {
      initRequest.namingConvention = configuration.getDestinationNamingConvention();
      initRequest.nameTransformer = configuration.getDestinationNameTransformer();
    }

    if (propertyResolver.equals(PropertyResolver.FIELDS)) {
      initRequest.accessLevel = configuration.getFieldAccessLevel();
      initRequest.propertyType = PropertyType.FIELD;
    } else {
      initRequest.propertyType = PropertyType.METHOD;
      initRequest.accessLevel = configuration.getMethodAccessLevel();
    }

    buildProperties(type, type, initRequest);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private synchronized void initAccessors() {
    if (accessors == null) {
      accessors = new LinkedHashMap<String, Accessor>();
      if (configuration.isFieldMatchingEnabled())
        buildProperties(accessors, (PropertyResolver) PropertyResolver.FIELDS);
      buildProperties(accessors, PropertyResolver.ACCESSORS);
    }
  }

  private synchronized void initMutators() {
    if (mutators == null) {
      mutators = new LinkedHashMap<String, Mutator>();
      if (configuration.isFieldMatchingEnabled())
        buildProperties(mutators, PropertyResolver.FIELDS);
      buildProperties(mutators, PropertyResolver.MUTATORS);
    }
  }
}
