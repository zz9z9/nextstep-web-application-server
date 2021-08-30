package webserver.http;

public enum HttpStatusCode3xx implements HttpStatusCode {

    MultipleChoice("300 Multiple Choice"),
    MovedPermanently("301 Moved Permanently"),
    Found("302 Found"),
    SeeOther("303 See Other"),
    NotModified("304 Not Modified"),
    TemporaryRedirect("307 Temporary Redirect"),
    PermanentRedirect("308 Permanent Redirect");


    private String statusCode;

    HttpStatusCode3xx(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getValue() {
        return this.statusCode;
    }
}
