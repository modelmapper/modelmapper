package org.modelmapper.functional.optional;

import com.google.common.base.Optional;
import org.modelmapper.AbstractTest;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.testng.Assert;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

@Test(groups = "functional")
public class OptionalMapping extends AbstractTest {

    static class Source {

        String name;
    }

    static class SourceOpt {

        Optional<String> name;
    }

    static class Dest {

        String name;
    }

    static class DestOpt {

        Optional<String> name;
    }

    public void shouldUseTypeMapPropertyConverter() {
        modelMapper.createTypeMap(Source.class, DestOpt.class).setPropertyConverter(
                new Converter<Object, Optional<Object>>() {
                    public Optional<Object> convert(MappingContext<Object, Optional<Object>> context) {
                        return Optional.<Object>of("test");
                    }
                });

        Source source = new Source();
        source.name = "dummy";
        DestOpt dest = modelMapper.map(source, DestOpt.class);
        Assert.assertNotNull(dest.name);
        Assert.assertTrue(dest.name.isPresent());
        assertEquals("test", dest.name.get());
    }

    public void shouldUseTypeMapPropertyConverterEvenIfSourceIsNull() {
        modelMapper.createTypeMap(Source.class, DestOpt.class).setPropertyConverter(
                new Converter<Object, Optional<Object>>() {
                    public Optional<Object> convert(MappingContext<Object, Optional<Object>> context) {
                        return Optional.absent();
                    }

                });

        Source source = new Source();
        DestOpt dest = modelMapper.map(source, DestOpt.class);
        Assert.assertNotNull(dest.name);
        Assert.assertFalse(dest.name.isPresent());
    }


    public void shouldMapToOptionalAbsent() {
        Source source = new Source();
        source.name = null;
        DestOpt dest = modelMapper.map(source, DestOpt.class);
        modelMapper.validate();
        Assert.assertNotNull(dest.name);
        Assert.assertFalse(dest.name.isPresent());
    }

    public void shouldMapToOptionalPresent() {
        Source source = new Source();
        source.name = "name";
        DestOpt dest = modelMapper.map(source, DestOpt.class);
        modelMapper.validate();
        Assert.assertNotNull(dest.name);
        Assert.assertTrue(dest.name.isPresent());
        Assert.assertEquals(source.name, dest.name.get());
    }

}
