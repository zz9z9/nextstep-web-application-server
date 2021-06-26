package util;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import webserver.http.HttpMethod;
import webserver.http.HttpRequest;
import webserver.request.RequestType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpRequestUtils {

    public static String getRequestFileName(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String requestInfo = bufferedReader.readLine();
        String requestFile = requestInfo.split(" ")[1];

        return requestFile;
    }

    public static HttpRequest getHttpRequest(InputStream in) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String mainInfo = bufferedReader.readLine();
        String[] requestInfo = mainInfo.split(" ");
        HttpMethod httpMethod = HttpMethod.valueOf(Optional.ofNullable(requestInfo[0]).orElseThrow(Exception::new));
        String requestUrl = Optional.ofNullable(requestInfo[1]).orElseThrow(Exception::new);

        if (HttpMethod.GET == httpMethod) {
            return processGetRequest(requestUrl, bufferedReader);
        } else if (HttpMethod.POST == httpMethod && bufferedReader.ready()) {
            return processPostRequest(requestUrl, bufferedReader);
        }

        return new HttpRequest(httpMethod, requestUrl);
    }

    private static HttpRequest processGetRequest(String requestUrl, BufferedReader bufferedReader) throws IOException {
        Map<String, String> cookies = null;

        for (String line = bufferedReader.readLine(); (line != null && !line.isEmpty()); line = bufferedReader.readLine()) {
            if (line.contains("Cookie")) {
                String[] info = line.split(":");
                cookies = parseCookies(info[1]);
                break;
            }
        }

        if (!requestUrl.contains("?")) {
            return new HttpRequest(HttpMethod.GET, requestUrl, cookies);
        }

        String[] info = requestUrl.split("\\?");
        Map<String, String> params = parseQueryString(info[1]);

        return new HttpRequest(HttpMethod.GET, info[0], params, cookies);
    }

    private static HttpRequest processPostRequest(String requestUrl, BufferedReader bufferedReader) throws IOException {
        int contentLen = 0;
        String contentType = "";
        Map<String, String> params = null;
        Map<String, String> cookies = null;

        for (String line = bufferedReader.readLine(); (line != null && !line.isEmpty()); line = bufferedReader.readLine()) {
            if (line.contains("Content-Length")) {
                String[] info = line.split(":");
                contentLen = Integer.parseInt(info[1].trim());
            } else if (line.contains("Content-Type")) {
                String[] info = line.split(":");
                contentType = info[1].trim(); // ex) application/x-www-form-urlencoded
            } else if (line.contains("Cookie")) {
                String[] info = line.split(":");
                cookies = parseCookies(info[1]);
            }
        }

        if (contentLen > 0) {
            char[] body = new char[contentLen];
            bufferedReader.read(body);

            if (contentType.equals("application/x-www-form-urlencoded")) {
                String queryString = new String(body);
                params = parseQueryString(queryString);
            } else if (contentType.equals("application/json")) {
                // TODO
            }
        }

        return new HttpRequest(HttpMethod.POST, requestUrl, params, cookies);
    }

    public static RequestType getRequestType(HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        String requestUrl = httpRequest.getRequestUrl();

        switch (httpMethod) {
            case GET:
                if (requestUrl.contains("?")) {
                    return RequestType.REQUEST_BUSINESS_LOGIC;
                } else if (requestUrl.equals("/") || requestUrl.contains(".")) {
                    return RequestType.REQUEST_FILE;
                } else {
                    return RequestType.REQUEST_BUSINESS_LOGIC;
                }

            case POST:
                return RequestType.REQUEST_BUSINESS_LOGIC;
        }

        return null;
    }

    /**
     * @param queryString URL에서 ? 이후에 전달되는 field1=value1&field2=value2 형식임
     * @return
     */
    public static Map<String, String> parseQueryString(String queryString) {
        return parseValues(queryString, "&");
    }

    /**
     * @param cookies 값은 name1=value1; name2=value2 형식임
     * @return
     */
    public static Map<String, String> parseCookies(String cookies) {
        return parseValues(cookies, ";");
    }

    private static Map<String, String> parseValues(String values, String separator) {
        if (Strings.isNullOrEmpty(values)) {
            return Maps.newHashMap();
        }

        String[] tokens = values.split(separator);
        return Arrays.stream(tokens).map(t -> getKeyValue(t, "=")).filter(p -> p != null)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    static Pair getKeyValue(String keyValue, String regex) {
        if (Strings.isNullOrEmpty(keyValue)) {
            return null;
        }

        String[] tokens = keyValue.split(regex);
        if (tokens.length != 2) {
            return null;
        }

        return new Pair(tokens[0], tokens[1]);
    }

    public static Pair parseHeader(String header) {
        return getKeyValue(header, ": ");
    }

    public static class Pair {
        String key;
        String value;

        Pair(String key, String value) {
            this.key = key.trim();
            this.value = value.trim();
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Pair other = (Pair) obj;
            if (key == null) {
                if (other.key != null)
                    return false;
            } else if (!key.equals(other.key))
                return false;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Pair [key=" + key + ", value=" + value + "]";
        }
    }
}
