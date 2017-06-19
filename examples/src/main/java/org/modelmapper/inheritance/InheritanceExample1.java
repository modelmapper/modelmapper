package org.modelmapper.inheritance;


import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.Provider;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration;

public class InheritanceExample1 {

    public static void main(String... args) {
        org.modelmapper.inheritance.C c = new org.modelmapper.inheritance.C(Arrays.asList(new BaseSrcA(),  new BaseSrcB()));

        ModelMapper modelMapper = new ModelMapper();

        TypeMap<BaseSrc, BaseDest> typeMap = modelMapper.createTypeMap(BaseSrc.class, BaseDest.class)
                .include(BaseSrcA.class, BaseDestA.class)
                .include(BaseSrcB.class, BaseDestB.class);

        modelMapper.typeMap(BaseSrcA.class, BaseDest.class).setProvider(new Provider<BaseDest>() {
            public BaseDest get(ProvisionRequest<BaseDest> request) {
                return new BaseDestA();
            }
        });
        modelMapper.typeMap(BaseSrcB.class, BaseDest.class).setProvider(new Provider<BaseDest>() {
            public BaseDest get(ProvisionRequest<BaseDest> request) {
                return new BaseDestB();
            }
        });

        CcDTO ccDTO = modelMapper.map(c, CcDTO.class);

        assertEquals(2, ccDTO.getBases().size());
        assertTrue(ccDTO.getBases().get(0) instanceof BaseDestA);
        assertTrue(ccDTO.getBases().get(1) instanceof BaseDestB);
    }
}
