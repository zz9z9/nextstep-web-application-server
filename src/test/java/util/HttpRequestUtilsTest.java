package util;

import org.junit.Test;
import util.HttpRequestUtils.Pair;
import webserver.HttpRequest;
import webserver.RequestType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static webserver.HttpMethod.GET;
import static webserver.HttpMethod.POST;

public class HttpRequestUtilsTest {
    @Test
    public void parseQueryString() {
        String queryString = "userId=javajigi";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));

        queryString = "userId=javajigi&password=password2";
        parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is("password2"));
    }

    @Test
    public void parseQueryString_null() {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString("");
        assertThat(parameters.isEmpty(), is(true));

        parameters = HttpRequestUtils.parseQueryString(" ");
        assertThat(parameters.isEmpty(), is(true));
    }

    @Test
    public void parseQueryString_invalid() {
        String queryString = "userId=javajigi&password";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("javajigi"));
        assertThat(parameters.get("password"), is(nullValue()));
    }

    @Test
    public void parseCookies() {
        String cookies = "logined=true; JSessionId=1234";
        Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);
        assertThat(parameters.get("logined"), is("true"));
        assertThat(parameters.get("JSessionId"), is("1234"));
        assertThat(parameters.get("session"), is(nullValue()));
    }

    @Test
    public void getKeyValue() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");
        assertThat(pair, is(new Pair("userId", "javajigi")));
    }

    @Test
    public void getKeyValue_invalid() throws Exception {
        Pair pair = HttpRequestUtils.getKeyValue("userId", "=");
        assertThat(pair, is(nullValue()));
    }

    @Test
    public void parseHeader() throws Exception {
        String header = "Content-Length: 59";
        Pair pair = HttpRequestUtils.parseHeader(header);
        assertThat(pair, is(new Pair("Content-Length", "59")));
    }

    @Test
    public void getRequestFileName() {
        Map<String, String> requestFile = new HashMap<>();
        requestFile.put("GET /index.html HTTP/1.1", "/index.html");
        requestFile.put("GET /css/style.css HTTP/1.1", "/css/style.css");
        requestFile.put("GET /js/script.js HTTP/1.1", "/js/script.js");

        try {
            for (String httpRequest : requestFile.keySet()) {
                byte[] bytes = httpRequest.getBytes();
                InputStream in = new ByteArrayInputStream(bytes);
                String fileName = HttpRequestUtils.getRequestFileName(in);
                String answer = requestFile.get(httpRequest);

                assertThat(fileName, is(answer));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getHttpRequest() throws Exception {
        Map<String, HttpRequest> requests = new HashMap<>();
        String request1 = "GET /index.html HTTP/1.1";
        String request2 = "POST /user/create HTTP/1.1";
        requests.put(request1, new HttpRequest(GET, "/index.html"));
        requests.put(request2, new HttpRequest(POST, "/user/create"));

        for (String req : requests.keySet()) {
            HttpRequest answer = requests.get(req);
            HttpRequest getObj = HttpRequestUtils.getHttpRequest(new ByteArrayInputStream(req.getBytes()));

            assertThat(getObj, is(answer));
        }
    }

    @Test
    public void getHttpRequestType() {
        HttpRequest req1 = new HttpRequest(POST, "/user/create");
        HttpRequest req2 = new HttpRequest(GET, "/user/info?email=test@test.com");
        HttpRequest req3 = new HttpRequest(GET, "/user/list");
        HttpRequest req4 = new HttpRequest(GET, "/index.html");
        HttpRequest req5 = new HttpRequest(GET, "/");
        Map<HttpRequest, RequestType> answers = new HashMap<>();

        answers.put(req1, RequestType.REQUEST_BUSINESS_LOGIC);
        answers.put(req2, RequestType.REQUEST_BUSINESS_LOGIC);
        answers.put(req3, RequestType.REQUEST_BUSINESS_LOGIC);
        answers.put(req4, RequestType.REQUEST_FILE);
        answers.put(req5, RequestType.REQUEST_FILE);

        for(HttpRequest req : answers.keySet()) {
            RequestType getType = HttpRequestUtils.getRequestType(req);
            RequestType answer = answers.get(req);

            assertEquals(getType, answer);
        }
    }
}
