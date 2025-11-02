/*
 * Copyright 2025 the original author or authors.
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
package org.modelmapper.internal.converter;

import java.time.temporal.Temporal;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

/**
 * Convert Temporal and its derived classes to their corresponding implementations
 *
 * @author Ngoc Nhan
 */
class TemporalConverter implements ConditionalConverter<Temporal, Temporal> {

    @Override
    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        boolean isSameClass = sourceType == destinationType
            && Temporal.class.isAssignableFrom(sourceType)
            && Temporal.class.isAssignableFrom(destinationType);
        return isSameClass ? MatchResult.FULL : MatchResult.NONE;
    }

    @Override
    public Temporal convert(MappingContext<Temporal, Temporal> context) {
        return context.getDestination() != null ? context.getDestination() : context.getSource();
    }

}