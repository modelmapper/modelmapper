package org.modelmapper.inheritance;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class InheritanceExample1 {

    public static void main(String... args) {
        BaseSrcA baseSrcA = new BaseSrcA();
        BaseSrcB baseSrcB = new BaseSrcB();
        List<BaseSrc> bases = new ArrayList<BaseSrc>();
        bases.add(baseSrcA);
        bases.add(baseSrcB);
        C c = new C(bases);

        ModelMapper modelMapper = new ModelMapper();

        TypeMap<BaseSrc, BaseDest> typeMap = modelMapper.createTypeMap(BaseSrc.class, BaseDest.class);

        typeMap.include(BaseSrcA.class, BaseDestA.class)
                .include(BaseSrcB.class, BaseDestB.class);

        CcDTO ccDTO = modelMapper.map(c, CcDTO.class);

        assertEquals(2, ccDTO.getBases().size());
        assertTrue(ccDTO.getBases().get(0) instanceof BaseDestA);
        assertTrue(ccDTO.getBases().get(1) instanceof BaseDestB);
    }
}
