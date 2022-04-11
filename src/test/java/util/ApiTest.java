package util;

import db.DataBase;
import logic.UserLogic;
import model.User;
import static org.junit.Assert.*;
import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiTest {

    @Test
    public void tt() throws Exception {
        StringBuilder formData = new StringBuilder()
                .append("userId=wodbsekd2&")
                .append("password=1234&")
                .append("name=user&")
                .append("email=lblbjy@gmail.com");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/user/create"))
                .POST(HttpRequest.BodyPublishers.ofString(formData.toString()))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        UserLogic logic = UserLogic.getInstance();
        User findUser  = logic.findUser("wodbsekd");
//        User findUser = DataBase.findUserById("wodbsekd");
        System.out.println("findUser : "+findUser);
        assertNotNull(findUser);
    }
}
