package com.microstrategy.samples;


import javax.ws.rs.HttpMethod;
import java.util.HashMap;
import java.util.Map;

/**
 * self-defined java class to set request parameters.
 */
public class RequestParameters {

    private String url;

    private Map<String, String> headers;

    private String body;

    private Map<String, String> queryParams;

    private Map<String, String> pathVariables;

    private String method;

    public RequestParameters() {
        headers = new HashMap<>();
        queryParams = new HashMap<>();
        pathVariables = new HashMap<>();
        body = "";
        url = "";
        method = HttpMethod.GET;
    }

    public void addQueryParam(String key, String value) {
        queryParams.put(key, value);
    }
    public void addQueryParam(String key, int value) { addQueryParam(key, String.valueOf(value)); }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
