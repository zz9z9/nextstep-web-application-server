package webserver.http;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {
    private DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public <T extends HttpStatusCode> void setStatusCode(T statusCode) throws IOException {
        dos.writeBytes(String.format("HTTP/1.1 %s \r\n", statusCode.getValue()));
    }

    public <T> void setHeader(String key, T value) throws IOException {
        dos.writeBytes(key +": " +value+"\r\n");
    }

    public <T> void setCookie(T value) throws IOException {
        dos.writeBytes( "Set-Cookie: " +value+"\r\n");
    }

    public void terminateHeader() throws IOException {
        dos.writeBytes("\r\n");
    }

    public void setBody(byte[] content) throws IOException {
        dos.write(content, 0, content.length);
        dos.flush();
    }
}

