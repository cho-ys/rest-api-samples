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


        /**
         * 2. publishing cube (Optional step if cube is already published)
         */


        /**
         * 3. Retrieving cube definition
         */


        /**
         * 4. Retrieving cube raw data
         */



        /**
         * 5. Retrieving cube data with instanceId
         */


        /**
         * 6. logging out
         */


    }

}


