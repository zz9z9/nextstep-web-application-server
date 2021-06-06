package webserver;

import logic.UserLogic;
import model.User;

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

    private Map<String, Execution> getMappingUrl = new HashMap<>();
    private Map<String, Execution> postMappingUrl = new HashMap<>();

    public LogicMapper() {
        initGetRequest();
        initPostRequest();
    }

    private void initGetRequest() {
        getMappingUrl.put("/user/create", new Execution(UserLogic.getInstance(), UserLogic.class, "signup", User.class));
    }

    private void initPostRequest() {
        postMappingUrl.put("/user/create", new Execution(UserLogic.getInstance(), UserLogic.class, "signup", User.class));
    }

    public byte[] doRequestLogic(HttpRequest httpRequest) throws Exception {
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

        byte[] response = (params!=null) ? executeMethodWithParams(execution, params) : executeMethodWithoutParams(execution);

        return response;
    }

    public byte[] executeMethodWithParams(Execution execution, Map<String,String> params) throws Exception {
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

        execution.getLogicClass()
                .getMethod(execution.getMethodName(), paramClass)
                .invoke(execution.getTargetInstance(), instance);

        return "SUCCESS".getBytes();
    }

    public byte[] executeMethodWithoutParams(Execution execution) throws Exception {
        execution.getLogicClass()
                .getMethod(execution.getMethodName(), execution.getParamClass())
                .invoke(execution.getTargetInstance());

        return "SUCCESS".getBytes();
    }
}
