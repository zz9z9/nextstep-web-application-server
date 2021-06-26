package webserver.request;

import webserver.response.ExecutionResult;
import webserver.response.ResponseType;
import webserver.http.*;

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
    private Map<String, Execution> getMappingUrl = new HashMap<>();
    private Map<String, Execution> postMappingUrl = new HashMap<>();

    public RequestLogicMapper() {
        initGetRequest();
        initPostRequest();
    }

    private void initGetRequest() {
        getMappingUrl.put("/user/create", new Execution("signup", ResponseType.HTML_PAGE));
        getMappingUrl.put("/user/list", new Execution("getUserList", ResponseType.HTML_PAGE));
    }

    private void initPostRequest() {
        postMappingUrl.put("/user/create", new Execution("signup", ResponseType.HTML_PAGE));
        postMappingUrl.put("/user/login", new Execution("login", ResponseType.HTML_PAGE));
    }

    public ExecutionResult doRequestLogic(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        String requestUrl = httpRequest.getRequestUrl();
        Map<String,String> params = httpRequest.getParams();
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
                httpResponse.setStatusCode(HttpStatusCode3xx.REDIRECTION);
                break;
            case DATA:
                httpResponse.setStatusCode(HttpStatusCode2xx.OK);
        }

        ExecutionResult result = (params!=null) ? executeMethodWithParams(execution, params, httpResponse) : executeMethodWithoutParams(execution, httpRequest);

        return result;
    }

    // TODO : catch (NoSuchMethodException e) 이 방식 말고, 유연하게 다양한 파리미터 가진 로직 메서드에 대응할 수 있도록 수정하기
    public ExecutionResult executeMethodWithParams(Execution execution, Map<String,String> params, HttpResponse httpResponse) throws Exception {
        Method logic;
        Object returnObj;
        try {
            logic = logicExecutor.getClass().getMethod(execution.getMethodName(), Map.class);
            returnObj = logic.invoke(logicExecutor, params);
        } catch (NoSuchMethodException e) {
            logic = logicExecutor.getClass().getMethod(execution.getMethodName(), Map.class, HttpResponse.class);
            returnObj = logic.invoke(logicExecutor, params, httpResponse);
        }

        return new ExecutionResult(execution.getResponseType(), returnObj);
    }

    // TODO : catch (NoSuchMethodException e) 이 방식 말고, 유연하게 다양한 파리미터 가진 로직 메서드에 대응할 수 있도록 수정하기
    public ExecutionResult executeMethodWithoutParams(Execution execution, HttpRequest httpRequest) throws Exception {
        Method logic;
        Object returnObj;

        try {
           logic = logicExecutor.getClass().getMethod(execution.getMethodName());
           returnObj = logic.invoke(logicExecutor);
        } catch (NoSuchMethodException e) {
            logic = logicExecutor.getClass().getMethod(execution.getMethodName(), HttpRequest.class);
            returnObj = logic.invoke(logicExecutor, httpRequest);
        }

        return new ExecutionResult(execution.getResponseType(), returnObj);
    }
}
