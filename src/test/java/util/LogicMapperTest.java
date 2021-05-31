package util;

import db.DataBase;
import model.User;
import org.junit.Test;
import webserver.LogicMapper;

import static org.junit.Assert.assertEquals;

public class LogicMapperTest {
    private static final LogicMapper logicMapper = new LogicMapper();

    @Test
    public void addUser() throws Exception {
        String requestUrl = "/user/create?userId=user&password=1234&name=aaaa&email=aaa%40aaa.com";
        User saveUser = new User("testId", "testPw", "testMan", "test@test.com");
        logicMapper.executeMethodWithParamsForGetRequest(requestUrl);

        User getUser = DataBase.findUserById(saveUser.getUserId());
        assertEquals(saveUser, getUser);
    }
}
