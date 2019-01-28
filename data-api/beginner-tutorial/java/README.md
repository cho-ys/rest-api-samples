## References   
### [Tutorial overview](../README.md)  
### [Curl implementation](../curl/README.md)    
### [NodeJs implementation](../nodejs/README.md)  

# Java Implementation
## Prepare Java Development Environment:
* Install JDK   
  download JDK(8 or higher) and install it
* Add dependencies: 
  * [spring-boot-starter-jersey.jar](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-jersey): contains complete jersey dependencies

  * [gson.jar](https://mvnrepository.com/artifact/com.google.code.gson/gson): used for build json object and parse json object
* You can start with an [initial project](initial) and follow the steps below to implement the defined common workflow

   
## Common Code Information:
```java
/**
 * common methods and informations
 */
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

    private static final ObjectMapper mapper = new ObjectMapper();

    private static Map<String, String> createCommonRequestHeaders(String authToken, String cookies, String projectId) {
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        requestHeaders.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        requestHeaders.put("X-MSTR-AuthToken" , authToken);
        requestHeaders.put("X-MSTR-ProjectID" , projectId);
        requestHeaders.put( HttpHeaders.COOKIE, cookies);

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
}    

/**
 * self-defined java class to set request parameters.
 */
import javax.ws.rs.HttpMethod;
import java.util.HashMap;
import java.util.Map;

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

/**
 * self-defined class to help build JerseyClient to send post/get request
 */
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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

```

## 1. Login In
Authenticate a user with provided **username** and **password** through endpoint [**_POST /auth/login_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Authentication/postLogin) and return an **MicroStrategy auth Token** in the response header.

### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
        cookiesMap.forEach((name, cookie) -> {
            cookiesBuffer.append(name);
            cookiesBuffer.append("=");
            cookiesBuffer.append(cookie.getValue());
            cookiesBuffer.append(";");
        });
        String cookies = cookiesBuffer.deleteCharAt(cookiesBuffer.length() - 1).toString();

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
}

```
### Response code:
```
204
```
### Response header:
```
{Cache-Control=[no-cache, no-store, max-age=0, must-revalidate], Server=[MicroStrategy], 
Connection=[keep-alive], 

Set-Cookie=[JSESSIONID=FBBD865E62C86A9CCA4A88D8F1966F81; Path=/MicroStrategyLibrary2;  Secure; HttpOnly, AWSALB=OmDwAIpAmUvks6wD/4Xyqxbql6QnL89Q7IGB4g3x06IH1E6QG+igTEG/PkMp0AAMF2xuKr05hmPmwP6xm5aT0pscRcJ4lYJp1a4IEiBfDKJkI/N5hhX4mfLWAey7; Expires=Thu, 06 Dec 2018 06:51:19 GMT; Path=/], 

Pragma=[no-cache], 
Expires=[0], Date=[Thu, 29 Nov 2018 06:51:19 GMT], 
X-MSTR-AuthToken=[caqtiltv3hapqpq6i6uhml492o]}
```
### 2. Publish Cube(Optional) 
Publish a cube with provided **cube Id** in a specific project with **project Id** through endpoint [**_POST /cubes/{cubeId}_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/publishCube) .
### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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

}
```
### Response code:
```
200
```
## 3. Retrieving cube definition
Get the definition of a specific cube with provided **cube id** through endpoint [**_GET /cubes/{cubeId}_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/getDefinition), the cube definition include attributes and metrics in the cube.
### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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
    
}
```
### Response code:
```
200
```
### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "result": {
    "definition": {
      "availableObjects": {
        "attributes": [
          {
            "name": "Region",
            "id": "8D679D4B11D3E4981000E787EC6DE8A4",
            "type": "Attribute",
            "forms": [
              {
                "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
                "name": "DESC",
                "dataType": "Char"
              },
              {
                "id": "45C11FA478E745FEA08D781CEA190FE5",
                "name": "ID",
                "dataType": "Real"
              }
            ]
          },
        ...
        ],
        "metrics": [
          {
            "name": "Revenue",
            "id": "4C05177011D3E877C000B3B2D86C964F",
            "type": "Metric"
          },
          ...
        ]
      }
    }
  }
}
```
## 4. Retrieving Cube Raw Data 
Here firstly we will introduce some Data Models which is used to contruct request body for endpoint [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) used in this section.
We can add requestObjects, metric limit, sort and view filter to the request body to control and limit the returned cube raw data. Details java models as below:

### 4.1 Limit & offset feature
Create a new instance of a specific cube with provide **cube id** through endpoint [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) and return the cube **instance Id** and cube **raw data** in the response body. It can be set **limit** and **offset** query parameter to limit returned cube data.
#### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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

     
        ObjectNode bodyNode = mapper.createObjectNode();

        requestParameters.setBody(bodyNode.toString());
        requestParameters.setMethod(HttpMethod.POST);

        return handleRequest(requestParameters);
    }
}
```
#### Response code:
```
200
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "DEF9E30211E8E0DA5D2B0080EFF5FFFD",
  "result": {
    "definition": {
      "attributes": [
        {
          "name": "Call Center",
          "id": "8D679D3511D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        },
        ...
      ],
      "metrics": [
        {
          "name": "Cost",
          "id": "7FD5B69611D5AC76C000D98A4CC5F24F",
          "type": "Metric",
          "min": 40.053,
          "max": 39475.0564,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0",
            "negativeType": 1
          }
        },
        ...
      ],
      "thresholds": [],
      "sorting": []
    },
    "data": {
      "paging": {
        "total": 12960,
        "current": 10,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": true,
        "children": [
          {
            "depth": 0,
            "element": {
              "attributeIndex": 0,
              "formValues": {
                "DESC": "Atlanta"
              },
              "name": "Atlanta",
              "id": "h1;8D679D3511D3E4981000E787EC6DE8A4"
            },
            "isPartial": true,
            "children": [
              {
                "depth": 1,
                "element": {
                  "attributeIndex": 1,
                  "formValues": {
                    "DESC": "Books"
                  },
                  "name": "Books",
                  "id": "h1;8D679D3711D3E4981000E787EC6DE8A4"
                },
                "isPartial": true,
                "children": [
                  {
                    "depth": 2,
                    "element": {
                      "attributeIndex": 2,
                      "formValues": {
                        "DESC": "Jan 2014"
                      },
                      "name": "Jan 2014",
                      "id": "h201401;8D679D4411D3E4981000E787EC6DE8A4"
                    },
                    "isPartial": false,
                    "children": [...]
                  },
                  ...
                ]
              },
              ...
            ]
          },
          ...
        ]
      }
    }
  }
}
```
### 4.2 Retrieving cube data with requestObjects
Create a new instance of a specific cube with provided **cube id** through endpoint  [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) and return the cube instance Id and cube raw data in the response body. It can be set **requestObjects** json data in the request body to limit returned cube data including specify attributes and metrics. Details see in official document for [RequestedObjects feature](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Filtering_RptsCubes_requestedObjects.htm).

Based on last step, the request body will be added new part: requestObjects  json data.
#### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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

        requestParameters.setBody(bodyNode.toString());
        requestParameters.setMethod(HttpMethod.POST);

        return handleRequest(requestParameters);
    }
}

```
#### Response code:
```
200
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "DEF9E30211E8E0DA5D2B0080EFF5FFFD",
  "result": {
    "definition": {
      "attributes": [
        {
          "name": "Category",
          "id": "8D679D3711D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        },
        {
          "name": "Subcategory",
          "id": "8D679D4F11D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        }
      ],
      "metrics": [
        {
          "name": "Cost",
          "id": "7FD5B69611D5AC76C000D98A4CC5F24F",
          "type": "Metric",
          "min": 238242.4329999999,
          "max": 4181261.1674000043,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0",
            "negativeType": 1
          }
        },
        {
          "name": "Profit",
          "id": "4C051DB611D3E877C000B3B2D86C964F",
          "type": "Metric",
          "min": 6708.9268000011,
          "max": 927202.3326000016,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0;(\"$\"#,##0)",
            "negativeType": 3
          }
        }
      ],
      "thresholds": [],
      "sorting": []
    },
    "data": {
      "paging": {
        "total": 24,
        "current": 10,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": true,
        "children": [
          ...
        ]
      }
    }
  }
}
```
### 4.3 Retrieving cube data with sorting
Create a new instance of a specific cube with provided **cube id** through endpoint  [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance)  and return the cube **instance Id** and cube **raw data** in the response body. It can be set **sorting json data** in the request body to sort cube data. Details see in official document for [Sorting feature](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Sorting_data.htm).  

Based on last-step, the request body will be added sorting json data.
#### Implement as below:
```java 
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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

        requestParameters.setBody(bodyNode.toString());
        requestParameters.setMethod(HttpMethod.POST);

        return handleRequest(requestParameters);
    }
}

```
#### Response code:
```
200  
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "FE53EB7A11E8E1945D2B0080EF153DF9",
  "result": {
    "definition": {
      "attributes": [...],
      "metrics": [...],
      "thresholds": [],
      "sorting": [
        {
          "type": "form",
          "attribute": {
            "name": "Category",
            "id": "8D679D3711D3E4981000E787EC6DE8A4"
          },
          "form": {
            "name": "DESC",
            "id": "CCFBE2A5EADB4F50941FB879CCF1721C"
          },
          "order": "descending"
        },
        {
          "type": "metric",
          "metric": {
            "name": "Cost",
            "id": "7FD5B69611D5AC76C000D98A4CC5F24F"
          },
          "order": "descending"
        }
      ]
    },
    "data": {
      "paging": {
        "total": 24,
        "current": 10,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": true,
        "children": [...]
      }
    }
  }
}
```
### 4.4 Retrieving cube data with metricLimits
Create a new instance of a specific cube with provided **cube id** through endpoint  [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) and return the cube **instance Id** and cube **raw data** in the response body. It can be set **metric limits** json data in the request body to filter cube data based on metric in the template level (grid level). Details see in official document for [Metric limits feature](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Filtering_RptsCubes_metricLimits.htm).

Based on last-step, the request body will be added new part for metric limits json data.
#### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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

        requestParameters.setBody(bodyNode.toString());
        requestParameters.setMethod(HttpMethod.POST);

        return handleRequest(requestParameters);
    }
}

```
#### Response code:
```
200  
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "E1A1B43411E8E1955D2B0080EF051FFD",
  "result": {
    "definition": {
      "attributes": [...],
      "metrics": [...],
      "thresholds": [],
      "sorting": [...]
    },
    "data": {
      "paging": {
        "total": 7,
        "current": 7,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": false,
        "children": [
          {
            "depth": 0,
            "element": {
              "attributeIndex": 0,
              "formValues": {
                "DESC": "Electronics"
              },
              "name": "Electronics",
              "id": "h2;8D679D3711D3E4981000E787EC6DE8A4"
            },
            "isPartial": false,
            "children": [
              {
                "depth": 1,
                "element": {
                  "attributeIndex": 1,
                  "formValues": {
                    "DESC": "Video Equipment"
                  },
                  "name": "Video Equipment",
                  "id": "h26;8D679D4F11D3E4981000E787EC6DE8A4"
                },
                "metrics": {
                  "Cost": {
                    "rv": 4181261.1674000043,
                    "fv": "$4,181,261",
                    "mi": 0
                  },
                  "Profit": {
                    "rv": 927202.3326000016,
                    "fv": "$927,202",
                    "mi": 1
                  }
                }
              },
              ...
            ]
          },
          ...
        ]
      }
    }
  }
}
```
### 4.5 Retrieving cube data with viewFilter
Create a new instance of a specific cube with provided **cube id** through endpoint  [**_POST /cubes/{cubeId}/instances_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/createCubeInstance) and return the cube **instance Id** and cube **raw data** in the response body. It can be set **view filter** json data in the request body to filter cube data based on metric or attribute in the dataset level. Details see in official document for [view filter feature](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Filtering_RptsCubes_ViewFilter.htm).

Based on last-step, the request body will be added new part for view filters json data.
#### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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
}

```
#### Response code:
```
200
```
#### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "1F372B1611E8E197E12F0080EFD593A4",
  "result": {
    "definition": {
      "attributes": [...],
      "metrics": [...],
      "thresholds": [],
      "sorting": [...]
    },
    "data": {
      "paging": {
        "total": 1,
        "current": 1,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": false,
        "children": [
          {
            "depth": 0,
            "element": {
              "attributeIndex": 0,
              "formValues": {
                "DESC": "Books"
              },
              "name": "Books",
              "id": "h1;8D679D3711D3E4981000E787EC6DE8A4"
            },
            "isPartial": false,
            "children": [
              {
                "depth": 1,
                "element": {
                  "attributeIndex": 1,
                  "formValues": {
                    "DESC": "Science & Technology"
                  },
                  "name": "Science & Technology",
                  "id": "h15;8D679D4F11D3E4981000E787EC6DE8A4"
                },
                "metrics": {
                  "Cost": {
                    "rv": 627512.4050000004,
                    "fv": "$627,512",
                    "mi": 0
                  },
                  "Profit": {
                    "rv": 184274.5950000004,
                    "fv": "$184,275",
                    "mi": 1
                  }
                }
              }
            ]
          }
        ]
      }
    }
  }
}
```
## 5. Retrieving cube data with above created instanceId
Get the results of a previously **created instance id** of a specific cube with provided **cube id** through endpoint  [**_GET /cubes/{cubeId}/instances/{instanceId}_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Cubes/getReport)

Based on created instanceId from last-step response
### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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
}

```
### Response code:
```
200
```
### Response body:
```
{
  "id": "8CCD8D9D4051A4C533C719A6590DEED8",
  "name": "Intelligent Cube - Drilling outside the cube is disabled",
  "instanceId": "1F372B1611E8E197E12F0080EFD593A4",
  "result": {
    "definition": {
      "attributes": [
        {
          "name": "Category",
          "id": "8D679D3711D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        },
        {
          "name": "Subcategory",
          "id": "8D679D4F11D3E4981000E787EC6DE8A4",
          "type": "Attribute",
          "forms": [
            {
              "id": "CCFBE2A5EADB4F50941FB879CCF1721C",
              "name": "DESC",
              "dataType": "Char"
            }
          ]
        }
      ],
      "metrics": [
        {
          "name": "Cost",
          "id": "7FD5B69611D5AC76C000D98A4CC5F24F",
          "type": "Metric",
          "min": 627512.4050000004,
          "max": 627512.4050000004,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0",
            "negativeType": 1
          }
        },
        {
          "name": "Profit",
          "id": "4C051DB611D3E877C000B3B2D86C964F",
          "type": "Metric",
          "min": 184274.5950000004,
          "max": 184274.5950000004,
          "numberFormatting": {
            "category": 1,
            "decimalPlaces": 0,
            "thousandSeparator": true,
            "currencySymbol": "$",
            "currencyPosition": 0,
            "formatString": "\"$\"#,##0;(\"$\"#,##0)",
            "negativeType": 3
          }
        }
      ],
      "thresholds": [],
      "sorting": [
        {
          "type": "form",
          "attribute": {
            "name": "Category",
            "id": "8D679D3711D3E4981000E787EC6DE8A4"
          },
          "form": {
            "name": "DESC",
            "id": "CCFBE2A5EADB4F50941FB879CCF1721C"
          },
          "order": "descending"
        },
        {
          "type": "metric",
          "metric": {
            "name": "Cost",
            "id": "7FD5B69611D5AC76C000D98A4CC5F24F"
          },
          "order": "descending"
        }
      ]
    },
    "data": {
      "paging": {
        "total": 1,
        "current": 1,
        "offset": 0,
        "limit": 10,
        "prev": null,
        "next": null
      },
      "root": {
        "isPartial": false,
        "children": [
          {
            "depth": 0,
            "element": {
              "attributeIndex": 0,
              "formValues": {
                "DESC": "Books"
              },
              "name": "Books",
              "id": "h1;8D679D3711D3E4981000E787EC6DE8A4"
            },
            "isPartial": false,
            "children": [
              {
                "depth": 1,
                "element": {
                  "attributeIndex": 1,
                  "formValues": {
                    "DESC": "Science & Technology"
                  },
                  "name": "Science & Technology",
                  "id": "h15;8D679D4F11D3E4981000E787EC6DE8A4"
                },
                "metrics": {
                  "Cost": {
                    "rv": 627512.4050000004,
                    "fv": "$627,512",
                    "mi": 0
                  },
                  "Profit": {
                    "rv": 184274.5950000004,
                    "fv": "$184,275",
                    "mi": 1
                  }
                }
              }
            ]
          }
        ]
      }
    }
  }
}
```
## 6. Logout 
Close all existing sessions for the authenticated user through endpoint [**_POST /auth/logout_**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?#!/Authentication/postLogout) .Logout with the **used authToken**.
### Implement as below:
```java
public class Tutorial {

    public static void main(String args[]) throws IOException {

        /**
         * 1. logging in
         */
        Response loginResponse = login(USER_NAME, PASSWORD);
        String authToken = (String) loginResponse.getHeaders().getFirst("X-MSTR-AuthToken");
        Map<String, NewCookie> cookiesMap = loginResponse.getCookies();
        StringBuffer cookiesBuffer = new StringBuffer();
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

    public static void logout(String authToken, String cookies) {
        System.out.println("Trying To Log out...");

        String url = REST_BASE_URL + "/auth/logout";
        RequestParameters requestParameters = new RequestParameters();
        requestParameters.setUrl(url);
        requestParameters.getHeaders().put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        requestParameters.getHeaders().put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        requestParameters.getHeaders().put("X-MSTR-AuthToken", authToken);
        requestParameters.getHeaders().put(HttpHeaders.COOKIE, cookies);
        requestParameters.setMethod(HttpMethod.POST);

        handleRequest(requestParameters);
    }

}
    
```
### Response code:
```
204
```
### Response header:
```
{
Cache-Control=[no-cache, no-store, max-age=0, must-revalidate],     Server=[MicroStrategy], Connection=[keep-alive], 
SetCookie=[AWSALB=PSyHBZx/9rdKFdEjs/xmCV32bpqHRGlAkROagNATXJlaXH7DltolfhXfDKJSs9XH+Nk+kVSra9q801sbFul/dma3gQRJShML67vayW++6zV6OLdRjX413eshWzGO; Expires=Thu, 06 Dec 2018 07:52:27 GMT; Path=/], 
Pragma=[no-cache], 
Expires=[0], 
Date=[Thu, 29 Nov 2018 07:52:27 GMT]
}
```
## Sample Java Code  
[MicroStrategy REST API tutorial Java code](final)
