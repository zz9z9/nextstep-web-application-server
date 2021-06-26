package webserver.http;

public enum HttpStatusCode2xx implements HttpStatusCode {
    OK("200 OK");

    private String statusCode;

    HttpStatusCode2xx(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getValue() {
        return this.statusCode;
    }
}
