package org.modelmapper.internal.converter;

import org.modelmapper.internal.util.Optionals;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

public class OptionalConverter implements ConditionalConverter<Object, Object> {

    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        if (Optionals.isOptional(destinationType)) {
            return MatchResult.FULL;
        } else {
            return MatchResult.NONE;
        }
    }

    public Object convert(MappingContext<Object, Object> context) {
        Object source = context.getSource();
        return Optionals.fromNullable(source);
    }

}
