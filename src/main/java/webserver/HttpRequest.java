package webserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO : Header, Body로 나누기
public class HttpRequest {
    private HttpMethod httpMethod;
    private String requestUrl;
    private Map<String, String> params;
    private Map<String, String> cookies;

    public HttpRequest(HttpMethod httpMethod, String requestUrl) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
    }

    public HttpRequest(HttpMethod httpMethod, String requestUrl, Map<String, String> cookies) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.cookies = cookies;
    }

    public HttpRequest(HttpMethod httpMethod, String requestUrl, Map<String, String> params, Map<String, String> cookies) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.params = params;
        this.cookies = cookies;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getCookie(String key) {
        if(this.cookies==null) {
            return null;
        }

        return this.cookies.getOrDefault(key, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return httpMethod == that.httpMethod &&
                Objects.equals(requestUrl, that.requestUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, requestUrl);
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "httpMethod=" + httpMethod +
                ", requestUrl='" + requestUrl + '\'' +
                '}';
    }
}
