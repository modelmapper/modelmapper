package org.modelmapper.internal.converter;

import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

import java.util.UUID;

class UuidConverter implements ConditionalConverter<Object, UUID>{
    public UUID convert(MappingContext<Object, UUID> context) {
        Object source = context.getSource();
        if (source == null)
            return null;

        Class<?> sourceType = context.getSourceType();

        String string = sourceType.isArray() && sourceType.getComponentType() == Character.TYPE
                || sourceType.getComponentType() == Character.class ? String.valueOf((char[]) source)
                : source.toString();
        return UUID.fromString(string);
    }

    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        return destinationType == UUID.class ? sourceType == UUID.class ? MatchResult.FULL
                : MatchResult.PARTIAL : MatchResult.NONE;
    }
}