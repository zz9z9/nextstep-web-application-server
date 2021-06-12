package webserver;

public enum HttpStatusCode3xx implements HttpStatusCode{
    REDIRECTION("302 Found");

    private String statusCode;

    HttpStatusCode3xx(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getValue() {
        return this.statusCode;
    }
}
