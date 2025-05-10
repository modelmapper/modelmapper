package org.modelmapper.internal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import java.util.UUID;
import lombok.Data;

public class CyclicMappingTest {
    ModelMapper modelMapper = new ModelMapper();
    @Data
    private static class Action {
        UUID id;
        Action previous;
    }

    @Data
    private static class ActionDTO {
        UUID id;
        UUID previousId;
    }

    @Test
    public void testPreviousLink() {

        ActionDTO dto = new ActionDTO();
        dto.setPreviousId(UUID.randomUUID());
        dto.setId(UUID.randomUUID());

        Action model = modelMapper.map(dto, Action.class);

        Assertions.assertInstanceOf(Action.class, model);

        Assertions.assertEquals(model.getId(), dto.getId());

        Assertions.assertNotNull(model.getPrevious()); // fails here when strict
        Assertions.assertInstanceOf(Action.class, model.getPrevious());

        Assertions.assertEquals(model.getPrevious().getId(), dto.getPreviousId()); // fails here with normal matching
    }
}
