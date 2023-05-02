package org.modelmapper.internal.valueaccess;

import org.modelmapper.internal.Errors;
import org.modelmapper.spi.ValueReader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Handles reading from java.lang.Record instances.
 *
 * @author okaponta
 */
public class RecordValueReader implements ValueReader<Record> {
    @Override
    public Object get(Record record, String memberName) {
        Field field = matchField(record, memberName);
        if (field != null) {
            try {
                return field.get(record);
            } catch (IllegalAccessException e) {
                throw new Errors().addMessage(e, "Cannot get the member").toMappingException();
            }
        }
        return null;
    }

    @Override
    public Member<Record> getMember(Record record, String memberName) {
        Field field = matchField(record, memberName);
        Class<?> type = field != null ? field.getClass() : Record.class;
        return new Member<>(type) {
            @Override
            public Object get(Record source, String memberName) {
                return RecordValueReader.this.get(source, memberName);
            }
        };
    }

    @Override
    public Collection<String> memberNames(Record record) {
        Field[] fields = record.getClass().getDeclaredFields();
        List<String> memberNames = new ArrayList<>(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);
            memberNames.add(field.getName());
        }
        return memberNames;
    }

    private Field matchField(Record source, String memberName) {
        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (memberName.equalsIgnoreCase(field.getName()))
                return field;
        }
        return null;
    }
}
