package webserver;

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

        ExecutionResult result = (params!=null) ? executeMethodWithParams(execution, params, httpResponse) : executeMethodWithoutParams(execution);

        return result;
    }

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

    public ExecutionResult executeMethodWithoutParams(Execution execution) throws Exception {
        Method logic = logicExecutor.getClass().getMethod(execution.getMethodName());
        Object returnObj = logic.invoke(logicExecutor);

        return new ExecutionResult(execution.getResponseType(), returnObj);
    }
}
