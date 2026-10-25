package org.modelmapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.Assert;
import org.junit.Test;
import org.modelmapper.convention.MatchingStrategies;

public class MapTooMuchTest {

    @Data
    private static class User {
        String login;
        int businessId;
        int id = 3;
    }

    @Data
    private static class NewUser {
        String login;
        int businessId;
    }

    @Test
    public void testUserId1() {
        NewUser newUser = new NewUser();
        newUser.setLogin("abcdefg");
        newUser.setBusinessId(1);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        User user = mapper.map(newUser, User.class);

        Assert.assertEquals(user.getId(), 3);
        Assert.assertEquals(user.getLogin(), "abcdefg");
        Assert.assertEquals(user.getBusinessId(), 1);
    }

    @Test
    public void testUserId2() {
        NewUser newUser = new NewUser();
        newUser.setLogin("abcdefg");
        newUser.setBusinessId(1);

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User user = mapper.map(newUser, User.class);

        Assert.assertEquals(user.getId(), 3);
        Assert.assertEquals(user.getLogin(), "abcdefg");
        Assert.assertEquals(user.getBusinessId(), 1);
    }

}


