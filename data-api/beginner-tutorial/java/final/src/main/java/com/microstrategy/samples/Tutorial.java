package com.microstrategy.samples;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.ws.http.HTTPException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Tutorial {

    //REST Server Connection Info
    private static final String REST_BASE_URL = "https://demo.microstrategy.com/MicroStrategyLibrary2/api";
    private static final String USER_NAME = "guest";
    private static final String PASSWORD = "";

    //Sample Object Info
    private static final String CUBE_ID = "8CCD8D9D4051A4C533C719A6590DEED8";
    private static final String PROJECT_ID = "B7CA92F04B9FAE8D941C3E9B7E0CD754";

    //MicroStrategy header
    private static final String MSTR_AUTHTOKEN = "X-MSTR-AuthToken";
    private static final String MSTR_PROJECT_ID = "X-MSTR-ProjectID";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static Map<String, String> createCommonRequestHeaders(String authToken, String cookies, String projectId) {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        requestHeaders.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        requestHeaders.put(MSTR_AUTHTOKEN , authToken);
        requestHeaders.put(MSTR_PROJECT_ID , projectId);
        requestHeaders.put(HttpHeaders.COOKIE, cookies);

        return requestHeaders;
    }

    private static String handleRequest(RequestParameters requestParameters) {
        RequestWrapper requestWrapper = new RequestWrapper(requestParameters);
        Response response = requestWrapper.sendRequest();

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode() || response.getStatus() == Response.Status.OK.getStatusCode()) {
            String output = response.readEntity(String.class);
            System.out.println(output);
            return output;
        } else {
            throw new HTTPException(response.getStatus());
        }
    }

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuilder cookiesBuffer = new StringBuilder();
        cookiesMap.forEach((name, cookie) -> {
            cookiesBuffer.append(name);
            cookiesBuffer.append("=");
            cookiesBuffer.append(cookie.getValue());
            cookiesBuffer.append(";");
        });
        String cookies = cookiesBuffer.deleteCharAt(cookiesBuffer.length() - 1).toString();

        /**
         * 2. publishing cube (Optional step if cube is already published)
         */
        publishCube(authToken, cookies, PROJECT_ID, CUBE_ID);

        /**
         * 3. Retrieving cube definition
         */
        getCubeDefinition(authToken, cookies, PROJECT_ID, CUBE_ID);

        /**
         * 4. Retrieving cube raw data
         */
        String cubeJSONRawData = getCubeRawData(authToken, cookies, PROJECT_ID, CUBE_ID);
        String instanceId = (mapper.readTree(cubeJSONRawData)).path("instanceId").asText();

        /**
         * 5. Retrieving cube data with instanceId
         */
        getCubeDataWithInstanceId(authToken, cookies, PROJECT_ID, CUBE_ID, instanceId);

        /**
         * 6. logging out
         */
        logout(authToken, cookies);

    }


    public static Response login(String userName, String password) {
        System.out.println("Trying To Login ...");

        String url = REST_BASE_URL + "/auth/login";
        //body json format: {"username":"String","password":"String"}
        String bodyJsonStr = "{\"username\":\"" + userName + "\",\"" + password + "\":\"\"}";// userName and password need to be escaped by (\",etc)

        // set request parameters.
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setUrl(url);
        requestParameters.getHeaders().put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        requestParameters.getHeaders().put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        requestParameters.setBody(bodyJsonStr);
        requestParameters.setMethod(HttpMethod.POST);

        //send request
        RequestWrapper requestWrapper = new RequestWrapper(requestParameters);
        Response response = requestWrapper.sendRequest();
        if (response.getStatus() != Response.Status.NO_CONTENT.getStatusCode()) {
            throw new HTTPException(response.getStatus());
        }

        return response;
    }


    public static String publishCube(String authToken, String cookies, String projectId, String cubeId) {
        System.out.println("Trying To Publish Cube ...");

        String url = REST_BASE_URL + "/cubes/{cubeId}";
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setUrl(url);
        requestParameters.setHeaders(createCommonRequestHeaders(authToken, cookies, projectId));
        requestParameters.setMethod(HttpMethod.POST);

        //cubeId is an path parameters
        requestParameters.getPathVariables().put("cubeId", cubeId);

        return handleRequest(requestParameters);
    }


    public static String getCubeDefinition(String authToken, String cookies, String projectId, String cubeId) {
        System.out.println("Trying To Get Cube Definition ...");

        String url = REST_BASE_URL + "/cubes/{cubeId}";
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setUrl(url);
        requestParameters.setHeaders(createCommonRequestHeaders(authToken, cookies, projectId));
        requestParameters.getPathVariables().put("cubeId", cubeId);
        requestParameters.setMethod(HttpMethod.GET);

        return handleRequest(requestParameters);
    }


    public static String getCubeRawData(String authToken, String cookies, String projectId, String cubeId) throws IOException {
        System.out.println("Trying To Get Cube Raw Data...");

        String url = REST_BASE_URL + "/cubes/{cubeId}/instances";
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setUrl(url);
        requestParameters.setHeaders(createCommonRequestHeaders(authToken, cookies, projectId));


        //cubeId is an path parameter
        requestParameters.getPathVariables().put("cubeId", cubeId);

        /**
         * add query parameters
         */
        requestParameters.addQueryParam("offset", 0); //set offset value 0
        requestParameters.addQueryParam("limit", 10); // set limit value 10

        //add request body
        /**
         * requestedObjects format json data as below:
         *{
         *   "attributes": [
         *      {
         *       "name": "Category",
         *       "id": "8D679D3711D3E4981000E787EC6DE8A4"
         *     },
         *      {
         *        "name": "Subcategory",
         *       "id": "8D679D4F11D3E4981000E787EC6DE8A4"
         *     }
         *   ],
         *   "metrics": [
         *     {
         *       "name": "Cost",
         *       "id": "7FD5B69611D5AC76C000D98A4CC5F24F"
         *     },
         *     {
         *       "name": "Profit",
         *      "id": "4C051DB611D3E877C000B3B2D86C964F"
         *     }
         *   ]
         *}
         */
        String requestedObjects = "{\"attributes\":[{\"name\":\"Category\",\"id\":\"8D679D3711D3E4981000E787EC6DE8A4\"},{\"name\":\"Subcategory\",\"id\":\"8D679D4F11D3E4981000E787EC6DE8A4\"}],\"metrics\":[{\"name\":\"Cost\",\"id\":\"7FD5B69611D5AC76C000D98A4CC5F24F\"},{\"name\":\"Profit\",\"id\":\"4C051DB611D3E877C000B3B2D86C964F\"}]}";

        /**
         *Sorting format json data as below:
         *
         *[
         * {
         *   "type": "form",
         *   "order": "descending",
         *   "attribute": {
         *     "id": "8D679D3711D3E4981000E787EC6DE8A4",
         *     "name": "Category"
         *   },
         *   "form": {
         *     "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
         *     "name": "DESC"
         *   }
         *  },
         * {
         *   "type": "metric",
         *   "metric": {
         *   "id": "7FD5B69611D5AC76C000D98A4CC5F24F",
         *   "name": "Cost"
         *   },
         *   "order": "descending"
         * }
         *]
         */
        String sorting = "[{\"type\":\"form\",\"order\":\"descending\",\"attribute\":{\"id\":\"8D679D3711D3E4981000E787EC6DE8A4\",\"name\":\"Category\"},\"form\":{\"id\":\"CCFBE2A5EADB4F50941FB879CCF1721C\",\"name\":\"DESC\"}},{\"type\":\"metric\",\"metric\":{\"id\":\"7FD5B69611D5AC76C000D98A4CC5F24F\",\"name\":\"Cost\"},\"order\":\"descending\"}]";

        /**
         *Metric Limits format json data as below:
         *
         *{
         *   "4C051DB611D3E877C000B3B2D86C964F":{
         *     "operator": "Greater",
         *     "operands": [
         *     {
         *       "type": "metric",
         *       "id": "4C051DB611D3E877C000B3B2D86C964F",
         *       "name": "Profit"
         *     },
         *     {
         *       "type": "constant",
         *       "value": "160000",
         *       "dataType": "Real"
         *     }
         *    ]
         *   }
         *}
         */
        String metricLimits = "{\"4C051DB611D3E877C000B3B2D86C964F\":{\"operator\":\"Greater\",\"operands\":[{\"type\":\"metric\",\"id\":\"4C051DB611D3E877C000B3B2D86C964F\",\"name\":\"Profit\"},{\"type\":\"constant\",\"value\":\"160000\",\"dataType\":\"Real\"}]}}";

        /**
         *View Filters format json data as below:
         *
         *{
         *   "operator": "And",
         *   "operands": [
         *   {
         *     "operator": "In",
         *     "operands": [
         *     {
         *       "type": "form",
         *       "attribute": {
         *         "id": "8D679D3711D3E4981000E787EC6DE8A4",
         *         "name": "Category"
         *       },
         *       "form": {
         *         "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
         *         "name": "DESC"
         *       }
         *     },
         *     {
         *       "type": "constants",
         *       "dataType": "Char",
         *       "values": [
         *         "Books",
         *         "Music"
         *       ]
         *     }
         *     ]
         *   }
         *  ]
         *}
         */
        String viewFilter = "{\"operator\":\"And\",\"operands\":[{\"operator\":\"In\",\"operands\":[{\"type\":\"form\",\"attribute\":{\"id\":\"8D679D3711D3E4981000E787EC6DE8A4\",\"name\":\"Category\"},\"form\":{\"id\":\"CCFBE2A5EADB4F50941FB879CCF1721C\",\"name\":\"DESC\"}},{\"type\":\"constants\",\"dataType\":\"Char\",\"values\":[\"Books\",\"Music\"]}]}]}";

        /**
         * requestBody format json as below:
         * {
         *    "viewFilter": {...},
         *    "requestedObjects": {...},
         *    "metricLimits": {...},
         *    "sorting": [...]
         * }
         */
        ObjectNode bodyNode = mapper.createObjectNode();
        bodyNode.set("requestedObjects", mapper.readTree(requestedObjects));
        bodyNode.set("sorting", mapper.readTree(sorting));
        bodyNode.set("metricLimits", mapper.readTree(metricLimits));
        bodyNode.set("viewFilter", mapper.readTree(viewFilter));

        requestParameters.setBody(bodyNode.toString());
        requestParameters.setMethod(HttpMethod.POST);

        return handleRequest(requestParameters);
    }


    public static String getCubeDataWithInstanceId(String authToken, String cookies, String projectId, String cubeId, String instanceId) {
        System.out.println("Trying To Get Cube Raw Data With InstanceId ...");

        String url = REST_BASE_URL + "/cubes/{cubeId}/instances/{instanceId}";
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setUrl(url);
        requestParameters.setHeaders(createCommonRequestHeaders(authToken, cookies, projectId));

        //cubeId and instanceId are an path parameters
        requestParameters.getPathVariables().put("cubeId", cubeId);
        requestParameters.getPathVariables().put("instanceId", instanceId);

        /**
         * add query paramters
         */
        requestParameters.addQueryParam("offset", 0); //set offset value 0
        requestParameters.addQueryParam("limit", 10); // set limit value 10
        requestParameters.setMethod(HttpMethod.GET);

        return handleRequest(requestParameters);

    }

    public static void logout(String authToken, String cookies) {
        System.out.println("Trying To Log out...");

        String url = REST_BASE_URL + "/auth/logout";
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setUrl(url);
        requestParameters.getHeaders().put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        requestParameters.getHeaders().put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        requestParameters.getHeaders().put(MSTR_AUTHTOKEN, authToken);
        requestParameters.getHeaders().put(HttpHeaders.COOKIE, cookies);
        requestParameters.setMethod(HttpMethod.POST);

        handleRequest(requestParameters);
    }

}


