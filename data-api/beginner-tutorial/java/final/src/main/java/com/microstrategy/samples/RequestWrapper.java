package com.microstrategy.samples;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


/**
 * self-defined class to help build JerseyClient to send post/get request
 */
public class RequestWrapper {
    private static final Client client = ClientBuilder.newClient();

    private RequestParameters component;
    private WebTarget target;
    Builder builder;

    public RequestWrapper(RequestParameters component) {
        this.component = component;
        target = client.target(component.getUrl());
        if (component.getQueryParams() != null) {
            component.getQueryParams().forEach((key, value) -> target = target.queryParam(key, value));
        }

        if (component.getPathVariables() != null) {
            component.getPathVariables().forEach((key, value) -> target = target.resolveTemplate(key,value));
        }
        builder = target.request();

        if (component.getHeaders() != null) {
            component.getHeaders().forEach((key, value) -> builder.header(key, value));
        }
    }

    public Response sendRequest() {
        Entity entity;
        String method = component.getMethod();
        if (method.equals(HttpMethod.GET) || method.equals(HttpMethod.DELETE)) {
            entity = null;
        } else {
            entity = Entity.json(component.getBody());
        }
       return builder.build(method, entity).invoke();
    }

}
