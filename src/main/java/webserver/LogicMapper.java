package webserver;

import logic.UserLogic;
import model.User;
import util.HttpRequestUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LogicMapper {
    static class Execution <T> {
        T targetInstance;
        Class logicClass;
        String methodName;
        Class paramClass;

        public Execution(T targetInstance, Class logicClass, String methodName) {
            this.targetInstance = targetInstance;
            this.logicClass = logicClass;
            this.methodName = methodName;
        }

        public Execution(T targetInstance, Class logicClass, String methodName, Class paramClass) {
            this.targetInstance = targetInstance;
            this.logicClass = logicClass;
            this.methodName = methodName;
            this.paramClass = paramClass;
        }

        public T getTargetInstance() {
            return targetInstance;
        }

        public Class getLogicClass() {
            return logicClass;
        }

        public String getMethodName() {
            return methodName;
        }

        public Class getParamClass() {
            return paramClass;
        }
    }

    static Map<String, Execution> getMappingUrl = new HashMap<>();

    public LogicMapper() {
        initGetRequest();
    }

    private void initGetRequest() {
        getMappingUrl.put("/user/create", new Execution(UserLogic.getInstance(), UserLogic.class, "signup", User.class));
    }

    private void initPostRequest() {

    }

    public byte[] doRequestLogic(HttpRequest httpRequest) throws Exception {
        HttpMethod httpMethod = HttpMethod.valueOf(httpRequest.getHttpMethod());
        String requestUrl = httpRequest.getRequestUrl();
        byte[] response = {};

        switch (httpMethod) {
            case GET:
                response = requestUrl.contains("?") ? executeMethodWithParamsForGetRequest(requestUrl) : executeMethodWithoutParamsForGetRequest(requestUrl);
                executeMethodWithParamsForGetRequest(requestUrl);
                break;
        }

        return response;
    }

    public byte[] executeMethodWithParamsForGetRequest(String requestUrl) throws Exception {
        String[] info = requestUrl.split("\\?");
        String url = info[0];
        Map<String, String> params = HttpRequestUtils.parseQueryString(info[1]);
        Execution execution = Optional.ofNullable(getMappingUrl.get(url)).orElseThrow(NoSuchMethodError::new);
        Class paramClass = execution.getParamClass();
        Object instance = paramClass.getDeclaredConstructor().newInstance();

        for(String key : params.keySet()) {
            Optional.ofNullable(paramClass.getDeclaredField(key)).ifPresent((field) -> {
                field.setAccessible(true);
                try {
                    field.set(instance, params.get(key));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }

        execution.getLogicClass().getMethod(execution.getMethodName(), execution.getParamClass()).invoke(execution.getTargetInstance(), instance);

        return "SUCCESS".getBytes();
    }

    public byte[] executeMethodWithoutParamsForGetRequest(String requestUrl) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Execution execution = Optional.ofNullable(getMappingUrl.get(requestUrl)).orElseThrow(NoSuchMethodError::new);
        execution.getLogicClass().getMethod(execution.getMethodName(), execution.getParamClass()).invoke(execution.getTargetInstance());

        return "SUCCESS".getBytes();
    }
}
