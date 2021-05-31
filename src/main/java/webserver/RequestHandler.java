package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.StringTokenizer;

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
            HttpRequest httpRequest = getHttpRequest(in);
            RequestType rt = getRequestType(httpRequest);
            byte[] responseBody = {};

            switch (rt) {
                case REQUEST_FILE:
                    responseBody = getRequestFile(httpRequest.getRequestUrl());
                    break;
                case REQUEST_BUSINESS_LOGIC:
                    responseBody = logicMapper.doRequestLogic(httpRequest);
                    break;
                default:
            }

            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, responseBody.length);
            responseBody(dos, responseBody);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public HttpRequest getHttpRequest(InputStream in) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String mainInfo = bufferedReader.readLine();
        String[] requestInfo = mainInfo.split(" ");
        String httpMethod = Optional.ofNullable(requestInfo[0]).orElseThrow(Exception::new);
        String requestUrl = Optional.ofNullable(requestInfo[1]).orElseThrow(Exception::new);

        if(httpMethod.equals("POST")) {
            int contentLen = 0;
            for(String line = bufferedReader.readLine(); (!line.isEmpty() && line!=null); line=bufferedReader.readLine()) {
                if(line.contains("Content-Length")) {
                    String[] info = line.split(":");
                    contentLen = Integer.parseInt(info[1].trim());
                    break;
                }
            }

            if (contentLen > 0) {
                char[] body = new char[contentLen];
                bufferedReader.read(body);
                String params = new String(body);
            }
        }

        return new HttpRequest(httpMethod, requestUrl);
    }

    private RequestType getRequestType(HttpRequest httpRequest) {
        String httpMethod = httpRequest.getHttpMethod();
        String requestUrl = httpRequest.getRequestUrl();

        switch (HttpMethod.valueOf(httpMethod)) {
            case GET:
                if(requestUrl.contains("?")) {
                    return RequestType.REQUEST_BUSINESS_LOGIC;
                } else if(requestUrl.equals("/") || requestUrl.contains(".")) {
                    return RequestType.REQUEST_FILE;
                } else {
                    return RequestType.REQUEST_BUSINESS_LOGIC;
                }

            case POST:
                break;
        }

        return null;
    }

    private byte[] getRequestFile(String requestUrl) throws IOException {
        byte[] result = (requestUrl.equals("/")) ? IOUtils.convertFileToByte(indexPage) : IOUtils.convertFileToByte(requestUrl);
        return result;
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
