package org.modelmapper.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public class ImplicitMappingBuilderTest {
    ModelMapper modelMapper = new ModelMapper();

    @Test
    public void entityToVo() throws Exception {
        Entity relation = Entity.builder().id(1).groupName("relation").build();
        Entity entity = Entity.builder().id(2).groupName("entity").relation(relation).build();

        Vo vo = modelMapper.map(entity, Vo.class);
        Assertions.assertEquals(vo.getId(), entity.getId());
        Assertions.assertEquals(vo.getGroupName(), entity.getGroupName());
        Assertions.assertEquals(vo.getRelationId(), entity.getRelation().getId());
        Assertions.assertEquals(vo.getRelationGroupName(), entity.getRelation().getGroupName());
    }

    @Test
    public void voToEntity() throws Exception {
        Vo vo = Vo.builder().id(2).relationId(1).build();

        Entity e = modelMapper.map(vo, Entity.class);
        Assertions.assertEquals(e.getId(), vo.getId());
        Assertions.assertEquals(e.getGroupName(), vo.getGroupName());
        Assertions.assertNotNull(e.getRelation());
        Assertions.assertEquals(e.getRelation().getId(), vo.getRelationId());
        Assertions.assertEquals(e.getRelation().getGroupName(), vo.getRelationGroupName());
    }

    @Test
    public void otherEntityToVo() throws Exception {
        OtherEntity.DifferentEntity relation = OtherEntity.DifferentEntity.builder().id(1).groupName("relation").build();
        OtherEntity entity = OtherEntity.builder().id(2).groupName("entity").relation(relation).build();

        Vo vo = modelMapper.map(entity, Vo.class);
        Assertions.assertEquals(vo.getId(), entity.getId());
        Assertions.assertEquals(vo.getGroupName(), entity.getGroupName());
        Assertions.assertEquals(vo.getRelationId(), entity.getRelation().getId());
        Assertions.assertEquals(vo.getRelationGroupName(), entity.getRelation().getGroupName());
    }

    @Test
    public void voToOtherEntity() throws Exception {
        Vo vo = Vo.builder().id(2).relationId(1).build();

        OtherEntity e = modelMapper.map(vo, OtherEntity.class);
        Assertions.assertEquals(e.getId(), vo.getId());
        Assertions.assertEquals(e.getGroupName(), vo.getGroupName());
        Assertions.assertNotNull(e.getRelation());
        Assertions.assertEquals(e.getRelation().getId(), vo.getRelationId());
        Assertions.assertEquals(e.getRelation().getGroupName(), vo.getRelationGroupName());
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Vo {
        long id;
        String groupName;
        long relationId;
        String relationGroupName;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Entity {
        long id;
        String groupName;
        Entity relation;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OtherEntity {
        long id;
        String groupName;
        DifferentEntity relation;

        @Data
        @EqualsAndHashCode(callSuper = false)
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class DifferentEntity {
            long id;
            String groupName;
            OtherEntity relation;
        }
    }

}