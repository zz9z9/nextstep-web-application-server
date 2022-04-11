package webserver.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import webserver.response.ExecutionResult;
import webserver.http.HttpRequest;
import webserver.http.HttpResponse;
import webserver.http.HttpStatusCode2xx;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String indexPage = "/index.html";
    private static final RequestLogicMapper requestLogicMapper = new RequestLogicMapper();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequestUtils.getHttpRequest(in);
            RequestType requestType = HttpRequestUtils.getRequestType(httpRequest);
            DataOutputStream dos = new DataOutputStream(out);
            HttpResponse httpResponse = new HttpResponse(dos);
            byte[] responseBody = {};

            switch (requestType) {
                case REQUEST_STATIC_RESOURCE:
                    String requestUrl = httpRequest.getRequestUrl();
                    responseBody = (requestUrl.equals("/")) ? IOUtils.convertFileToByte(indexPage) : IOUtils.convertFileToByte(requestUrl);
                    String contentType = "text/html; charset=utf-8";

                    if(requestUrl.contains(".css")) {
                        contentType = "text/css";
                    } else if(requestUrl.contains(".js")) {
                        contentType = "application/javascript";
                    }

                    httpResponse.setStatusCode(HttpStatusCode2xx.OK);
                    httpResponse.setHeader("Content-Type", contentType);
                    httpResponse.setHeader("Content-Length", responseBody.length);
                    break;

                case REQUEST_BUSINESS_LOGIC:
                    ExecutionResult result = requestLogicMapper.doRequestLogic(httpRequest, httpResponse);

                    switch (result.getResponseType()) {
                        case HTML_PAGE:
                            String redirectPage = (String) result.getReturnData();
                            String redirectUrl = "http://localhost:8080"+redirectPage; // TODO : 하드코딩 말고 request origin으로 ?
                            httpResponse.setHeader("Location", redirectUrl);
                            break;

                        case DATA:
                            break;

                        case EMPTY:
                            httpResponse.setStatusCode(HttpStatusCode2xx.OK);
                            httpResponse.setHeader("Content-Type", "text/html;charset=utf-8");
                            httpResponse.setHeader("Content-Length", responseBody.length);
                            break;
                    }
                    break;
            }

            httpResponse.terminateHeader();
            httpResponse.setBody(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
