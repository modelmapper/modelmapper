package org.modelmapper;

import java.util.List;

public interface ConstructorInjector {
  List<ConstructorParam> getParameters(Class<?> destinationType);
  boolean isApplicable(Class<?> destinationType);
}
