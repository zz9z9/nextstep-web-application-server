package util;

import logic.UserLogic;
import model.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserLogicTest {

    static UserLogic logic = UserLogic.getInstance();

    @Test
    public void isSingleTon() {
        UserLogic ul1 = UserLogic.getInstance();
        UserLogic ul2 = UserLogic.getInstance();

        assertSame(ul1,ul2);
    }

    @Test
    public void signup() {
        User user = new User("wodbsekd", "1234", "이재윤", "lblbjy@gmail.com");
        logic.signup(user);

        User findUser = logic.findUser(user.getUserId());
        assertSame(user, findUser);
    }
}
