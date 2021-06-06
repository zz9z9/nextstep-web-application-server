package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String indexPage = "/index.html";
    private static final LogicMapper logicMapper = new LogicMapper();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequestUtils.getHttpRequest(in);
            RequestType rt = HttpRequestUtils.getRequestType(httpRequest);
            DataOutputStream dos = new DataOutputStream(out);
            byte[] responseBody = {};

            switch (rt) {
                case REQUEST_FILE:
                    String requestUrl = httpRequest.getRequestUrl();
                    responseBody = (requestUrl.equals("/")) ? IOUtils.convertFileToByte(indexPage) : IOUtils.convertFileToByte(requestUrl);
                    response2xxHeader(dos, responseBody.length);
                    break;
                case REQUEST_BUSINESS_LOGIC:
                    String redirectPage = logicMapper.doRequestLogic(httpRequest);
                    if(!redirectPage.isEmpty()) {
                        String redirectUrl = "http://localhost:8080"+redirectPage; // TODO : 하드코딩 말고 request origin으로 ??
                        response3xxHeader(dos, redirectUrl);
                    } else {
                        response2xxHeader(dos, responseBody.length); // 화면측에서 리다이렉트 하도록
                    }
                    break;
                default:
            }

            responseBody(dos, responseBody);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void response2xxHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response3xxHeader(DataOutputStream dos, String redirectUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location : "+redirectUrl);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
