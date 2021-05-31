package logic;

import db.DataBase;
import model.User;

// TODO : 싱글톤 패턴 멀티스레드 등 고려하는 방식으로 리팩토링하기
public class UserLogic {
    private static final UserLogic userLogic = new UserLogic();

    private UserLogic(){}

    public static UserLogic getInstance() {
        return userLogic;
    }

    public void signup(User user) {
        DataBase.addUser(user);
    }
}
