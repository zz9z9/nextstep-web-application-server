package webserver.response;

public class ExecutionResult {
    private ResponseType responseType;
    private Object returnData;

    public ExecutionResult(ResponseType responseType, Object returnData) {
        this.responseType = responseType;
        this.returnData = returnData;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public Object getReturnData() {
        return returnData;
    }
}
