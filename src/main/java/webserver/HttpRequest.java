package webserver;

class HttpRequest {
    String httpMethod;
    String requestUrl;

    public HttpRequest(String httpMethod, String requestUrl) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }
}
