package org.modelmapper.bugs;

import lombok.Data;

import org.modelmapper.AbstractTest;
import org.modelmapper.ConfigurationException;
import org.modelmapper.ModelMapper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

@Test
public class GH578 extends AbstractTest {

    enum CLASSIFIER{
        AAA,
        BBB;
    }

    @Data
    class TypeA {
        TypeABox box;
    }

    @Data
    class TypeABox {
        CLASSIFIER param;

    }

    @Data
    class TypeB {
        TypeBBox box;
    }

    @Data
    class TypeBBox {
        CLASSIFIER param;
    }

    public void testBoxMapping() {

        TypeA typeA = new TypeA();
        TypeABox typeABox = new TypeABox();

        typeABox.setParam(CLASSIFIER.AAA);
        typeA.setBox(typeABox);

        TypeB typeB = new TypeB();
        TypeBBox typeBBox = new TypeBBox();

        typeBBox.setParam(CLASSIFIER.BBB);
        typeB.setBox(typeBBox);
        ModelMapper modelMapper = new ModelMapper();
        try {
            modelMapper.createTypeMap(TypeA.class, TypeB.class)
                    .addMappings(mapper -> mapper.skip(TypeA::getBox,TypeB::setBox));
            fail();
        }catch (ConfigurationException e) {
            assertEquals(e.getErrorMessages().size(), 1);
            assertEquals(e.getErrorMessages().iterator().next().getMessage(),
                    "Not able to skip box., because there are already nested properties are mapped: [box.param.]. "
                            + "Do you skip the property after the implicit mappings mapped? "
                            + "We recommended you to create an empty type map, and followed by addMappings and implicitMappings calls" );
        }
        modelMapper.map(typeA, typeB);
    }
}
