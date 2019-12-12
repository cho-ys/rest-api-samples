package com.microstrategy;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * POJO class: following attributes would require to pass either in header or query param section.
 */
public class PropertiesUtil {

    private final static Logger logger = Logger.getLogger(ApiOperation.class);

    private String userName;
    private String password;
    private String loginMode;
    private String projectId;
    private String baseUrl;
    private String authToken;
    private String instanceId;
    private String reportId;


    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

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


    /**
     * Fetch value from config.properties.
     */
    public void getConfigProperty() {
        Properties properties = new Properties();
        try(InputStream input = new FileInputStream("config.properties");){
            properties.load(input);
            logger.info("BASE URL :" + properties.getProperty("BASEURL"));
            setBaseUrl(properties.getProperty("BASEURL"));
            setLoginMode(properties.getProperty("LOGINMODE"));
            setProjectId(properties.getProperty("PROJECTID"));
            setReportId(properties.getProperty("REPORTID"));
            if (!(properties.getProperty("PASSWORD").trim().equals(""))) {
                setPassword(properties.getProperty("PASSWORD"));
            }
            if (!(properties.getProperty("USERNAME").trim().equals(""))) {
                setUserName(properties.getProperty("USERNAME"));
            }
        }catch(IOException ex) {
            logger.debug("Failed to fetch value from configure file: ", ex);
        }

    }

    /**
     * Response header display.
     */
    public String showResponseHeader(HttpURLConnection conn) {
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
        }
        catch (IOException ex) {
            logger.debug("Failed to response header: ", ex);
        }
        return builder.toString();
    }
}
