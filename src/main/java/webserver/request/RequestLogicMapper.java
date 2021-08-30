package webserver.request;

import webserver.http.*;
import webserver.response.ExecutionResult;
import webserver.response.ResponseType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestLogicMapper {
    static class Execution {
        private String methodName;
        private ResponseType responseType;

        public Execution(String methodName, ResponseType responseType) {
            this.methodName = methodName;
            this.responseType = responseType;
       }

        public String getMethodName() {
            return methodName;
        }

        public ResponseType getResponseType() {
            return responseType;
        }
    }

    private LogicExecutor logicExecutor = LogicExecutor.getInstance();
    private static Map<String, Execution> getMappingUrl = new HashMap<>();
    private static Map<String, Execution> postMappingUrl = new HashMap<>();

    static {
        initGetRequest();
        initPostRequest();
    }

    private static void initGetRequest() {
        getMappingUrl.put("/user/create", new Execution("signup", ResponseType.HTML_PAGE));
        getMappingUrl.put("/user/list", new Execution("getUserList", ResponseType.HTML_PAGE));
    }

    private static void initPostRequest() {
        postMappingUrl.put("/user/create", new Execution("signup", ResponseType.HTML_PAGE));
        postMappingUrl.put("/user/login", new Execution("login", ResponseType.HTML_PAGE));
    }

    public ExecutionResult doRequestLogic(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        String requestUrl = httpRequest.getRequestUrl();
        Execution execution = null;

        switch (httpMethod) {
            case GET:
                execution = Optional.ofNullable(getMappingUrl.get(requestUrl)).orElseThrow(NoSuchMethodError::new);
                break;
            case POST:
                execution = Optional.ofNullable(postMappingUrl.get(requestUrl)).orElseThrow(NoSuchMethodError::new);
                break;
        }

        switch (execution.getResponseType()) {
            case HTML_PAGE:
                httpResponse.setStatusCode(HttpStatusCode3xx.Found);
                break;
            case DATA:
                httpResponse.setStatusCode(HttpStatusCode2xx.OK);
        }

        Map<String, String> params = httpRequest.getParams();
        ExecutionResult result = (params != null) ? executeMethodWithParams(execution, params, httpRequest, httpResponse) : executeMethodWithoutParams(execution, httpRequest, httpResponse);

        return result;
    }

    public ExecutionResult executeMethodWithParams(Execution execution, Map<String, String> params, HttpRequest request, HttpResponse response) throws Exception {
        Method logic = logicExecutor.getClass().getMethod(execution.getMethodName(), Map.class, HttpRequest.class, HttpResponse.class);
        Object returnObj = logic.invoke(logicExecutor, params, request, response);

        return new ExecutionResult(execution.getResponseType(), returnObj);
    }

    public ExecutionResult executeMethodWithoutParams(Execution execution, HttpRequest request, HttpResponse response) throws Exception {
        Method logic = logicExecutor.getClass().getMethod(execution.getMethodName(), HttpRequest.class, HttpResponse.class);
        Object returnObj = logic.invoke(logicExecutor, request, response);;

        return new ExecutionResult(execution.getResponseType(), returnObj);
    }
}
