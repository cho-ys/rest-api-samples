package com.microstrategy;




/**
 * Entry point of java program.
 */
public class Main {

    public static void main(String[] args) {

        ApiOperation apiOperation = new ApiOperation();

        //Login
        String auth =  apiOperation.login();
        System.out.println("AuthToken is:  \n"+auth +"\n\n");

        //create instance of report and Get report data
        String instanceId = apiOperation.getReportInstance();
        System.out.println("Instance id of report: \n" + instanceId +"\n\n");


        //Get report data of that instance
        String reportData = apiOperation.getReportByInstance();

        //Export data into Excel sheet
        apiOperation.parseReport(reportData);

    }

}
