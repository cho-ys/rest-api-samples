package com.microstrategy;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.*;
import java.net.*;
import java.util.Base64;


/**
 * @author patelNeel
 *
 * Created on 13- Dec- 2019
 *
 * Created HTTP connections used to call MicroStrategy Rest API operation
 *
 */
public class ApiOperation {

    static PropertiesUtil propertiesUtil;

    static{
        propertiesUtil = new PropertiesUtil();
        propertiesUtil.getConfigProperty();
    }

    boolean showReponseHeader = false;
    boolean showReponseBody =false;

    /**
     * Method to login into Microstrategy
     *
     * @param
     * @return authToken
     * @throws MalformedURLException
     * @throws IOException
     */
    public String login() {
        String authtoken = null;
        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            URL url = new URL(propertiesUtil.getBaseUrl() + "auth/login");
            System.out.println("1. Login URL: " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(ParamNames.POST);
            conn.setRequestProperty(ParamNames.ACCEPT, ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.ContentType, ParamNames.APPLICATIONJSON);
            JSONObject obj = new JSONObject();
            obj.put(ParamNames.LOGINMODE, propertiesUtil.getLoginMode());
            if((propertiesUtil.getUserName() != null)) {
                obj.put(ParamNames.USERNAME, propertiesUtil.getUserName());
            }
            if((propertiesUtil.getPassword() != null)) {
                obj.put(ParamNames.PASSWORD, propertiesUtil.getPassword());
            }
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(obj.toString());
            wr.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
            }
            if(showReponseHeader){
                String header = propertiesUtil.showResponseHeader(conn);
                System.out.println(header +"\n\n");
            }
            authtoken = conn.getHeaderField(ParamNames.XMSTRAUTHTOKEN);
            propertiesUtil.setAuthToken(authtoken);
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return authtoken;
    }

    /**
     * Method to create dossier/document new instance id
     *
     * @param
     * @return instance
     * @throws MalformedURLException
     * @throws IOException
     */
    public String getObjectPromptDossierInstance() throws org.json.simple.parser.ParseException {
        String instance = null;
        try {
            URL url = new URL(propertiesUtil.getBaseUrl() + "dossiers/" + propertiesUtil.getDossierId() + "/instances");
            System.out.println("2. Create Instance: " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(ParamNames.POST);
            conn.setRequestProperty(ParamNames.ACCEPT, ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.ContentType,ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.XMSTRAUTHTOKEN, propertiesUtil.getAuthToken());
            conn.setRequestProperty(ParamNames.XMSTRPROJECTID, propertiesUtil.getProjectId());
            JSONObject obj = new JSONObject();
            obj.put(ParamNames.PERSISTVIEWSTATE, true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(obj.toString());
            wr.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if(showReponseBody){
                System.out.println(response.toString()+"\n\n");
            }
            JSONParser parser = new JSONParser();
            JSONObject myResponse = (JSONObject) parser.parse(response.toString());
            instance  = (String) myResponse.get(ParamNames.MID);
            if(showReponseHeader){
                String header = propertiesUtil.showResponseHeader(conn);
                System.out.println(header+"\n\n");
            }
            propertiesUtil.setInstanceId(instance);
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    /**
     * Method to call Microstrategy api to reset prompt value
     *
     * @param
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public String reprompt() throws org.json.simple.parser.ParseException  {
        String repromptInstance = null;
        try {
            URL url = new URL(propertiesUtil.getBaseUrl() + "documents/" + propertiesUtil.getDossierId() + "/instances/"+propertiesUtil.getInstanceId()+"/rePrompt");
            System.out.println("3. Reprompt Dossier: " + url +"\n\n");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(ParamNames.POST);
            conn.setRequestProperty(ParamNames.ACCEPT, ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.ContentType, ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.XMSTRAUTHTOKEN, propertiesUtil.getAuthToken());
            conn.setRequestProperty(ParamNames.XMSTRPROJECTID, propertiesUtil.getProjectId());
            JSONObject obj = new JSONObject();
            obj.put(ParamNames.PERSISTVIEWSTATE, true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(obj.toString());
            wr.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if(showReponseBody){
                System.out.println(response.toString()+"\n\n");
            }
            JSONParser parser = new JSONParser();
            JSONObject myResponse = (JSONObject) parser.parse(response.toString());
            repromptInstance  = (String) myResponse.get(ParamNames.MID);
            propertiesUtil.setInstanceId(repromptInstance);
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return repromptInstance;
    }

    /**
     * This method to call Microstrategy api to answer the given prompt. Here, we are used "USA" and "Canada" country for given prompt selection.
     * @param
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public void updatΩeDossierWithPrompt() {
        try{
            URL url = new URL(propertiesUtil.getBaseUrl()+"documents/"+propertiesUtil.getDossierId()+"/instances/"+propertiesUtil.getInstanceId()+"/prompts/answers");
            System.out.println("4. Prompt Answer: "+url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(ParamNames.PUT);
            conn.setRequestProperty(ParamNames.ACCEPT, ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.ContentType, ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.XMSTRAUTHTOKEN, propertiesUtil.getAuthToken());
            conn.setRequestProperty(ParamNames.XMSTRPROJECTID, propertiesUtil.getProjectId());

            JSONObject j1 = new JSONObject();
            j1.put(ParamNames.NAME,"USA");
            JSONObject j2 = new JSONObject();
            j2.put(ParamNames.NAME,"Canada");

            JSONArray answers = new JSONArray();
            answers.add(j1);
            answers.add(j2);

            JSONObject jans = new JSONObject();
            jans.put(ParamNames.KEY, propertiesUtil.getKey());
            jans.put(ParamNames.TYPE, propertiesUtil.getType());
            jans.put(ParamNames.ANSWERS, answers);

            JSONArray jArray = new JSONArray();
            jArray.add(jans);

            JSONObject jPrompt = new JSONObject();
            jPrompt.put(ParamNames.PROMPTS, jArray);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            System.out.println(jPrompt +"\n\n");
            wr.write(jPrompt.toString());
            wr.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if(showReponseBody){
                System.out.println(response.toString()+"\n\n");
            }
            conn.disconnect();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to call Microstrategy api to export PDF
     *
     * @param
     * @return encodeString
     * @throws MalformedURLException
     * @throws IOException
     */
    public String exportPDF() throws org.json.simple.parser.ParseException{
        String encodeString = null;
        try{
            URL url = new URL(propertiesUtil.getBaseUrl() + "documents/"+propertiesUtil.getDossierId()+"/instances/"+propertiesUtil.getInstanceId()+"/pdf");
            System.out.println("5. Export PDF: "+url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod(ParamNames.POST);
            conn.setRequestProperty(ParamNames.ACCEPT, ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.ContentType, ParamNames.APPLICATIONJSON);
            conn.setRequestProperty(ParamNames.XMSTRAUTHTOKEN, propertiesUtil.getAuthToken());
            conn.setRequestProperty(ParamNames.XMSTRPROJECTID, propertiesUtil.getProjectId());
            conn.setRequestProperty(ParamNames.INSTANCEID, propertiesUtil.getInstanceId());
            conn.setRequestProperty(ParamNames.ID, propertiesUtil.getDossierId());
            JSONObject obj = new JSONObject();
            obj.put("orientation","AUTO");
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(obj.toString());
            wr.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONParser parser = new JSONParser();
            JSONObject myResponse = (JSONObject) parser.parse(response.toString());
            encodeString  = (String) myResponse.get(ParamNames.DATA);
            conn.disconnect();
        }catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodeString;
    }

    /**
     * Method to convert into PDF from base64
     *
     * @param
     * @return
     */
    public void decodeIntoPDF(String encodeString) throws IOException{
        System.out.println("4. Download into PDF");
        byte[] decodedBytes = Base64.getDecoder().decode(encodeString);
        String home = System.getProperty("user.home");
        File file = new File(home+"/Downloads/MicroStrategy.pdf");
        FileOutputStream fop = new FileOutputStream(file);
        fop.write(decodedBytes);
        fop.flush();
        fop.close();
        System.out.print("Download successfully in your Download folder");
    }
}
