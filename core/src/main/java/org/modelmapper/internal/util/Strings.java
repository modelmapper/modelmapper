/*
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
package org.modelmapper.internal.util;

import java.util.List;

import org.modelmapper.spi.PropertyInfo;

/**
 * @author Jonathan Halterman
 */
public final class Strings {
  private Strings() {
  }

  public static String join(List<? extends PropertyInfo> properties) {
    StringBuilder sb = new StringBuilder();
    String delim = "";
    for (PropertyInfo info : properties) {
      sb.append(delim).append(info.getName());
      delim = ".";
    }

    return sb.toString();
  }

  public static String joinWithFirstType(List<? extends PropertyInfo> properties) {
    StringBuilder sb = new StringBuilder();
    String delim = "";
    for (PropertyInfo info : properties) {
      sb.append(delim).append(delim.equals("") ? info : info.getName());
      delim = ".";
    }

    return sb.toString();
  }
}
