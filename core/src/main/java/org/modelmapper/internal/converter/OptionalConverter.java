package org.modelmapper.internal.converter;

import com.google.common.base.Optional;
import java.lang.reflect.ParameterizedType;
import org.modelmapper.internal.util.Types;
import org.modelmapper.spi.ConditionalConverter;
import org.modelmapper.spi.MappingContext;

public class OptionalConverter implements ConditionalConverter<Object, Optional<Object>> {

    public MatchResult match(Class<?> sourceType, Class<?> destinationType) {
        if (Optional.class.isAssignableFrom(destinationType)) {
            return MatchResult.FULL;
        } else {
            return MatchResult.NONE;
        }
    }

    public Optional<Object> convert(MappingContext<Object, Optional<Object>> context) {
        Object source = context.getSource();
        return Optional.fromNullable(source);
    }

}
