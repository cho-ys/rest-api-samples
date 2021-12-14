package com.microstrategy;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class is making rest api calls and uses HTTP requests such as POST, GET, and DELETE.
 * From Light weight JSON data, fetch necessary information from it and process as per your need.
 */
public class ApiOperation {


    private final static Logger logger = LogManager.getLogger(ApiOperation.class);
    static PropertiesUtil propertiesUtil;
    static {
        propertiesUtil = new PropertiesUtil();
        propertiesUtil.getConfigProperty();
    }

    /**
     * Authenticate a user and create HTTP session on the web server where the
     * user's MicroStrategy sessions are stored.
     * @param
     * @return authToken Authorization token
     * @throws MalformedURLException
     * @throws IOException
     */
    public String login() {
        logger.info("Login into MicroStrategy");
        String authtoken = null;
        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            URL url = new URL(propertiesUtil.getBaseUrl() + "auth/login");
            logger.info("Login URL: " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //() -> con.disconnect() is a lambda expression which execute con.disconnect() at finally stage of the try statement.
            try (AutoCloseable conc = () -> conn.disconnect()) {
                conn.setDoOutput(true);
                //Pass Header parameters
                conn.setRequestMethod(ParamNames.POST);
                conn.setRequestProperty(ParamNames.ACCEPT, ParamNames.APPLICATIONJSON);
                conn.setRequestProperty(ParamNames.ContentType, ParamNames.APPLICATIONJSON);
                JSONObject obj = new JSONObject();
                obj.put(ParamNames.LOGINMODE, propertiesUtil.getLoginMode());
                if ((propertiesUtil.getUserName() != null)) {
                    obj.put(ParamNames.USERNAME, propertiesUtil.getUserName());
                }
                if ((propertiesUtil.getPassword() != null)) {
                    obj.put(ParamNames.PASSWORD, propertiesUtil.getPassword());
                }
                try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
                    wr.write(obj.toString());
                    wr.flush();
                }
                //204 response code is expected to get authorization token
                if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                    logger.debug("Failed login: " + getErrorMessage(conn));
                    throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
                }
                authtoken = conn.getHeaderField(ParamNames.XMSTRAUTHTOKEN);
                propertiesUtil.setAuthToken(authtoken);
            }
        } catch (MalformedURLException e) {
            logger.debug("Issue with MicroStrategy URL: ", e);
        } catch (Exception e) {
            logger.debug("Exception occurred at login step: ", e);
        }
        return authtoken;
    }



    /**
     * Create a report instance and get the report data.
     * @return response instance of a report
     */
    public String getReportInstance() {
        String instance = null;
        try {
            URL url = new URL(propertiesUtil.getBaseUrl() + "v2/reports/" + propertiesUtil.getReportId() + "/instances");
            logger.info("Create report instance: " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try (AutoCloseable conc = () -> conn.disconnect()) {
                conn.setDoOutput(true);
                conn.setRequestMethod(ParamNames.POST);
                conn.setRequestProperty(ParamNames.ContentType,ParamNames.APPLICATIONJSON);
                conn.setRequestProperty(ParamNames.XMSTRAUTHTOKEN, propertiesUtil.getAuthToken());
                conn.setRequestProperty(ParamNames.XMSTRPROJECTID, propertiesUtil.getProjectId());
                JSONObject obj = new JSONObject();
                obj.put(ParamNames.PERSISTVIEWSTATE, true);

                //200 response code is expected after successful fetch report definition
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    logger.debug("Failed to create report instance: " + getErrorMessage(conn));
                    throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
                }

                String response = getResponse(conn);
                JSONObject res = new JSONObject(response);
                instance = res.getString(ParamNames.instanceId);
                propertiesUtil.setInstanceId(instance);
            }
        } catch (MalformedURLException e) {
            logger.debug("Issue with MicroStrategy URL: ", e);
        } catch (Exception e) {
            logger.debug("Failed to create Report instance: ", e);
        }
        return instance;
    }

    /**
     * Get the result of a report instance.
     * @return
     */
    public String getReportByInstance() {
        String response = null;
        try {
            URL url = new URL(propertiesUtil.getBaseUrl() + "v2/reports/" + propertiesUtil.getReportId() + "/instances/" + propertiesUtil.getInstanceId());
            logger.info("Get the definition and data result of a grid : " + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //() -> con.disconnect() is a lambda expression which execute con.disconnect() at finally stage of the try statement.
            try (AutoCloseable conc = () -> conn.disconnect()) {
                conn.setRequestMethod(ParamNames.GET);
                conn.setDoOutput(true);
                //Pass Header parameters
                conn.setRequestProperty(ParamNames.ACCEPT,ParamNames.APPLICATIONJSON);
                conn.setRequestProperty(ParamNames.XMSTRAUTHTOKEN, propertiesUtil.getAuthToken());
                conn.setRequestProperty(ParamNames.XMSTRPROJECTID, propertiesUtil.getProjectId());

                //200 response code is expected after successful fetch report definition
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    logger.debug("Failed to get Report data based on instance: " + getErrorMessage(conn));
                    throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
                }
                response = getResponse(conn);
            }
        } catch (MalformedURLException e) {
            logger.debug("Issue with MicroStrategy URL: ", e);
        } catch (Exception e) {
            logger.debug("Failed to get report data based on instance: ", e);
        }
        return response;
    }


    /**
     * Export report data into excel sheet.
     * @param jsonReport Report data
     */
    public void parseReport(String jsonReport) {

        //Fetch Row header values
        List<String> rowHeader = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonReport);
        //Get row data in jason array form
        JSONArray rows = jsonObject.getJSONObject("definition").getJSONObject("grid").getJSONArray("rows");
        //Get row header
        getRowHeader(rows, rowHeader);


        //Fetch Metric values
        List<String> columnData = new ArrayList<>();
        //Add all column data into the list
        List<List<String>> metricData = new ArrayList<>();
        JSONArray data = jsonObject.getJSONObject("data").getJSONObject("metricValues").getJSONArray("formatted");
        for (int i = 0; i < data.length(); i++) {
            JSONArray dataArray = data.getJSONArray(i);
            for (int k = 0; k < dataArray.length(); k++) {
                String singleData = dataArray.getString(k);
                columnData.add(singleData);
            }
            metricData.add(columnData);
            columnData = new ArrayList<>();
        }

        //Fetch Attribute values(Row data)
        //Store one row at a time in list
        List<String> metricRows  = new ArrayList<>();
        //Store list of attribute values
        List <List<String>>  attributesList = new ArrayList<>();

        //Get row indexes in jason array form which would bind actual row data
        JSONArray rowMetrics = jsonObject.getJSONObject("data").getJSONObject("headers").getJSONArray("rows");

        getRowData(rowMetrics, rows, metricRows, attributesList);
        //In Excel sheet, we are tyring to insert row by row.
        //Store MicroStrategy report data row by row in following main sheet in list format.
        List<List<String>> sheetData = new ArrayList<>();

        //Save Attribute header data main sheet
        sheetData.add(rowHeader);

        List<String> inlineRowHeader = null;

        int rowHeaders = 0;

        //Fetch Column Header
        //Get Column index from Json
        JSONArray columnIndexs = jsonObject.getJSONObject("data").getJSONObject("headers").getJSONArray("columns");
        for (int i = 0; i < columnIndexs.length() ; i++) {
            JSONArray columnIndex = columnIndexs.getJSONArray(i);
            if(sheetData.size() > rowHeaders) {
                inlineRowHeader = sheetData.get(rowHeaders);
                rowHeaders++;
            } else {
                inlineRowHeader = new ArrayList<>();
                sheetData.add(inlineRowHeader);
                rowHeaders++;
            }
            //For columns
            JSONArray columns = jsonObject.getJSONObject("definition").getJSONObject("grid").getJSONArray("columns");
            for (int k = 0; k < columnIndex.length() ; k++) {
                String headerName = columns.getJSONObject(i).getString("name");
                String headerType = columns.getJSONObject(i).getString("type");
                JSONArray elements = columns.getJSONObject(i).getJSONArray("elements");

                //Json has two different types in column data with different Json block inside it.
                //for matrix and one for attribute. Here checked for column has type of metrics.
                if (headerType.equalsIgnoreCase("templateMetrics")) {
                    String elementName = elements.getJSONObject((Integer)columnIndex.get(k)).getString("name");
                    //if Column header have more then one which is in case of Cross-tab, then add empty string for Attribute header row.
                    if((inlineRowHeader == null || inlineRowHeader.isEmpty() && columns.length() > 1)) {
                        for (int  m = 0; m < columns.length() ; m++) {
                            inlineRowHeader.add("");
                        }
                    }
                    inlineRowHeader.add(elementName);
                } else {
                    // JSONArray elements = columns.getJSONObject(i).getJSONArray("elements");
                    JSONArray elementNames = elements.getJSONObject((Integer) columnIndex.get(k)).getJSONArray("formValues");
                    //Here, always at 0 index you will get element name
                    String elementName = elementNames.getString(0);
                    inlineRowHeader.add(elementName);
                }
            }
        }

        //Combining attribute value and Metrics value in one list index by index
        //ex: list1 = [[2016Q1,Audio Eqipment],[2016Q1, Camera]] list2 = [[$2678,$2190],[$1289,$2357]]
        List<List<String>> combineAttributeMetric = IntStream.range(0, attributesList.size()).mapToObj(i -> {
            attributesList.get(i).addAll(metricData.get(i));
            return attributesList.get(i);
        }).collect(Collectors.toList());
        //Add attribute and metric values data into main sheet
        sheetData.addAll(combineAttributeMetric);


        //Root class to handle XLSX.
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create Sheet
        Sheet sheet = workbook.createSheet("MicroStrategy Report Data");

        //Creat Row
        int rowNum = 0;
        //Create Cell
        int cellNum = 0;

        //Export data from sheetData to Excel Sheet
        for (List<String> temp : sheetData) {
            Row row = sheet.createRow(rowNum++);
            for (String actualData : temp) {
                Cell cell = row.createCell(cellNum);
                cell.setCellValue(actualData);
                sheet.autoSizeColumn(cellNum);
                cellNum++;
            }
            //Set to 0 index for every new row
            cellNum = 0;
        }

        logger.info("Report data parsed successfully.");

        //get user's system default address path
        String userHome = System.getProperty("user.home");

        //Write the output to a file and save with MicroStrategy report name on user's desktop
        try(FileOutputStream out = new FileOutputStream(new File(userHome+"/Desktop/"+jsonObject.getString("name")+".xlsx"))) {
            workbook.write(out);
            out.close();
            //closing the workbook
            workbook.close();
            logger.info("Exported and saved data into Excel sheet successfully");
        } catch (Exception e) {
            logger.debug("Failed to parse report data: ", e);
        }
    }

    /**
     * Get error message from response body thrown by MicroStrategy intelligence Server
     * or MicroStrategy Library Server.
     * @param connection HTTPURLconnection
     * @return
     * @throws IOException IOException
     */
    private String getErrorMessage(HttpURLConnection connection) throws IOException {
        StringBuffer response = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        JSONObject res = new JSONObject(response.toString());
        return res.getString(ParamNames.MESSAGE);
    }

    /**
     * Get row headers of MicroStrategy report.
     * @param rows row of report
     * @param rowHeader List of row header
     */
    private void getRowHeader(JSONArray rows, List<String> rowHeader) {
        for (int i = 0; i < rows.length(); i++) {
            String headerName = rows.getJSONObject(i).getString("name");
            rowHeader.add(headerName);
            //If Row header has multiple form then, we require additional column depends on how much form is present in template.
            if (rows.getJSONObject(i).getJSONArray("forms").length() > 1) {
                for(int m = 0; m < rows.getJSONObject(i).getJSONArray("forms").length() - 1; m++) {
                    rowHeader.add("");
                }
            }
        }
    }

    /**
     * Get row data and bind row data in list form based on row indexes.
     * ex: “rows”: [
     *         [2,1],
     *         [2,2]
     *        ]
     * Index count represent definition/grid/rows[indexCount] and index value represents definition/grid/rows/elements/formValues[index value].
     * [2,1] Get 0th position of definition/grid/rows[0] & Get value of that 0th position definition/grid/rows/elements/formValues[2] --> bind to
     * --> Get 1th position of definition/grid/rows[1] & Get value of that 1th position of definition/grid/rows/elements[1]
     * @param rowMetrics row index which bind row data
     * @param rowsData row data
     * @param metricRows List of row data
     * @param attributesList List of List of row data
     */
    private void getRowData (JSONArray rowMetrics, JSONArray rowsData, List<String> metricRows, List <List<String>>  attributesList) {
        for (int i = 0; i < rowMetrics.length() ; i++) {
            JSONArray metricArray = rowMetrics.getJSONArray(i);
            for (int k = 0; k < metricArray.length() ; k++) {
                JSONArray elements = rowsData.getJSONObject(k).getJSONArray("elements");
                JSONArray elementNames = elements.getJSONObject((Integer)metricArray.get(k)).getJSONArray("formValues");
                String elementName;
                //Calculate how many forms are present in row section for multiform template
                //Based on form count, fetch value for attribute
                for(int m = 0; m < rowsData.getJSONObject(k).getJSONArray("forms").length(); m++) {
                    elementName = elementNames.getString(m);
                    metricRows.add(elementName);
                }
            }
            //add metric row data in List format
            attributesList.add(metricRows);
            //create a new list for every row data for next line
            metricRows = new ArrayList<>();
        }
    }

    /**
     * Get successful response body by MicroStrategy intelligence Server.
     * or MicroStrategy Library Server.
     * @param connection HTTPURLconnection
     * @return Response String response body
     * @throws IOException IOException
     */
    private String getResponse(HttpURLConnection connection) throws IOException {
        StringBuffer response = new StringBuffer();
        String inputLine;
        try (BufferedReader in =
                 new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        return response.toString();
    }

}
