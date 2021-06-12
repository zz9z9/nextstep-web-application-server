package webserver;

import logic.UserLogic;
import model.User;

import java.io.IOException;
import java.util.Map;

public class LogicExecutor {

    private static final LogicExecutor logicExecutor = new LogicExecutor();

    private LogicExecutor(){}

    public static LogicExecutor getInstance() {
        return logicExecutor;
    }

    private UserLogic userLogic = UserLogic.getInstance();

    public String signup(Map<String, String> params) {
        String id = params.get("userId");
        String pw = params.get("password");
        String name = params.get("name");
        String email = params.get("email");
        User newUser = new User(id, pw, name, email);

        return userLogic.signup(newUser);
    }

    public String login(Map<String, String> params, HttpResponse response) throws IOException {
        String id = params.get("userId");
        String pw = params.get("password");

        return userLogic.login(id,pw,response);
    }

}
