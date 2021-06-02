package webserver;

import java.util.Objects;


public class HttpRequest {
    HttpMethod httpMethod;
    String requestUrl;

    public HttpRequest(HttpMethod httpMethod, String requestUrl) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
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
