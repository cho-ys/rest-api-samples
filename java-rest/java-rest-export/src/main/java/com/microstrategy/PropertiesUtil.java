package com.microstrategy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author patelNeel
 *
 * Created on 13- Dec- 2019
 *
 * To get and set request and response parameters
 *
 */
public class PropertiesUtil {

    private String userName;
    private String password;
    private String loginMode;
    private String projectId;
    private String dossierId;
    private String baseUrl;
    private String authToken;
    private String instanceId;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDossierId() {
        return dossierId;
    }

    public void setDossierId(String dossierId) {
        this.dossierId = dossierId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public void getConfigProperty(){
        Properties properties = new Properties();
        try(InputStream input = new FileInputStream("config.properties");){
            properties.load(input);
            System.out.println("BASE URL :" + properties.getProperty("BASEURL") + "\n\n");
            setBaseUrl(properties.getProperty("BASEURL"));
            setLoginMode(properties.getProperty("LOGINMODE"));
            setProjectId(properties.getProperty("PROJECTID"));
            setDossierId(properties.getProperty("DOSSIERID"));
            if (!(properties.getProperty("PASSWORD").trim().equals(""))) {
                setPassword(properties.getProperty("PASSWORD"));
            }
            if (!(properties.getProperty("USERNAME").trim().equals(""))) {
                setUserName(properties.getProperty("USERNAME"));
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

    public String showResponseHeader( HttpURLConnection conn) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(conn.getResponseCode())
                    .append(" ")
                    .append(conn.getResponseMessage())
                    .append("\n");
            Map<String, List<String>> map = conn.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                if (entry.getKey() == null)
                    continue;
                builder.append(entry.getKey())
                        .append(": ");

                List<String> headerValues = entry.getValue();
                Iterator<String> it = headerValues.iterator();
                if (it.hasNext()) {
                    builder.append(it.next());

                    while (it.hasNext()) {
                        builder.append(", ")
                                .append(it.next());
                    }
                }
                builder.append("\n");
            }
            System.out.println(builder);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }


}
