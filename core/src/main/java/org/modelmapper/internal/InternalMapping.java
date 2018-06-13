/*
 * Copyright 2018 the original author or authors.
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

import java.util.List;
import org.modelmapper.spi.Mapping;
import org.modelmapper.spi.PropertyInfo;

/**
 *  An internal mappings that defines internal methods for mappings
 *
 *  @author Chun Han Hsiao
 */
interface InternalMapping extends Mapping {
  /**
   * Creates a merged mapping whose source path begins with the {@code mergedAccessors} and
   * destination path begins with the {@code mergedMutators}.
   */
  InternalMapping createMergedCopy(List<? extends PropertyInfo> mergedAccessors,
      List<? extends PropertyInfo> mergedMutators);

  /**
   * Returns whether the mapping is explicit or implicit.
   */
  boolean isExplicit();
}
