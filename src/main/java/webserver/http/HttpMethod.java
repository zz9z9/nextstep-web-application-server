package webserver.http;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    PUT("PUT");

    private String methodType;

    HttpMethod(String methodType) {
        this.methodType = methodType;
    }
}
