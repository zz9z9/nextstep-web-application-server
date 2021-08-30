package webserver.http;

/**
 * 200 OK
 * 요청이 성공적으로 되었습니다. 성공의 의미는 HTTP 메소드에 따라 달라집니다:
 * GET: 리소스를 불러와서 메시지 바디에 전송되었습니다.
 * HEAD: 개체 해더가 메시지 바디에 있습니다.
 * PUT 또는 POST: 수행 결과에 대한 리소스가 메시지 바디에 전송되었습니다.
 * TRACE: 메시지 바디는 서버에서 수신한 요청 메시지를 포함하고 있습니다.
 *
 * 201 Created
 * 요청이 성공적이었으며 그 결과로 새로운 리소스가 생성되었습니다. 이 응답은 일반적으로 POST 요청 또는 일부 PUT 요청 이후에 따라옵니다.
 *
 * 202 Accepted
 * 요청을 수신하였지만 그에 응하여 행동할 수 없습니다. 이 응답은 요청 처리에 대한 결과를 이후에 HTTP로 비동기 응답을 보내는 것에 대해서 명확하게 명시하지 않습니다.
 * 이것은 다른 프로세스에서 처리 또는 서버가 요청을 다루고 있거나 배치 프로세스를 하고 있는 경우를 위해 만들어졌습니다.
 *
 * 203 Non-Authoritative Information (en-US)
 * 이 응답 코드는 돌려받은 메타 정보 세트가 오리진 서버의 것과 일치하지 않지만 로컬이나 서드 파티 복사본에서 모아졌음을 의미합니다.
 * 이러한 조건에서는 이 응답이 아니라 200 OK 응답을 반드시 우선됩니다.
 *
 * 204 No Content
 * 요청에 대해서 보내줄 수 있는 콘텐츠가 없지만, 헤더는 의미있을 수 있습니다. 사용자-에이전트는 리소스가 캐시된 헤더를 새로운 것으로 업데이트 할 수 있습니다.
 *
 * 205 Reset Content
 * 이 응답 코드는 요청을 완수한 이후에 사용자 에이전트에게 이 요청을 보낸 문서 뷰를 리셋하라고 알려줍니다.
 *
 * 206 Partial Content
 * 이 응답 코드는 클라이언트에서 복수의 스트림을 분할 다운로드를 하고자 범위 헤더를 전송했기 때문에 사용됩니다.
 */
public enum HttpStatusCode2xx implements HttpStatusCode {
    OK("200 OK"),
    Created("201 Created"),
    Accepted("202 Accepted"),
    NonAuthoritativeInformation("203 Non-Authoritative Information"),
    NoContent("204 No Content"),
    ResetContent("205 Reset Content"),
    PartialContent("206 Partial Content");

    private String statusCode;

    HttpStatusCode2xx(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String getValue() {
        return this.statusCode;
    }
}
