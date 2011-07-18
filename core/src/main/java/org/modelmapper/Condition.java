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
package org.modelmapper;

import org.modelmapper.spi.MappingContext;

/**
 * Condition that must apply in order for mapping to occur.
 * 
 * @param <S> source type
 * @param <D> destination type
 * 
 * @author Jonathan Halterman
 */
public interface Condition<S, D> {
  /**
   * Specifies whether the condition applies to the {@code context}.
   * 
   * @param context of current mapping process
   */
  boolean applies(MappingContext<S, D> context);
}
