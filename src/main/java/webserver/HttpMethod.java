package webserver;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT");

    String httpMethod;

    HttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }
}
