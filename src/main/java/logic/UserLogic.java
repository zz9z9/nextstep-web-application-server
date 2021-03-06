package logic;

import db.DataBase;
import model.User;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;

import java.io.IOException;

// TODO : 싱글톤 패턴 멀티스레드 등 고려하는 방식으로 리팩토링하기
public class UserLogic {
    private static final UserLogic userLogic = new UserLogic();

    private UserLogic(){}

    public static UserLogic getInstance() {
        return userLogic;
    }

    public String signup(User user) {
        DataBase.addUser(user);
        return "/index.html";
    }

    public User findUser(String id) {
        return DataBase.findUserById(id);
    }

    public String login(String id, String pw, HttpResponse response) throws IOException {
        User findUser = findUser(id);
        if(findUser!=null && pw.equals(findUser.getPassword())) {
            response.setCookie("logined=true; Path=/");
            return "/index.html";
        }

        response.setCookie("logined=false; Path=/");
        return "/user/login_failed.html";
    }

    public String getUserList(HttpRequest request) {
        String isLogined = request.getCookie("logined");
        if(isLogined!=null && isLogined.equals("true")) {
            return "/user/list.html";
        }
        return "/user/login.html";
    }
}
