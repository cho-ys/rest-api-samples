## Project Name:
MicroStrategy Prompt Workflow

## Project Description:
This App is a sample demo app to show the users how to use MicroStrategy REST APIs to execute prompt dossier/report and export pdf of dossier or documents. 

A list of REST APIs were used in the demo. They were used to authenticating, creating new instance of respective dossier, apply prompt on dossier then exporting to PDF. 

## APIs used/demonstrated:

The REST APIs used in this demo are:
#### POST /auth/login 
Creating a MicroStrategy session given credentials and authentication mode. An authToken will be returned for latter operations.
In the demo, this API was used to create a configuration session.

#### POST / documents/{id}/instances
Execute a specific dossier and create an instance of the dossier.
In the demo, this API was used to create a new instance of respective dossier.

#### POST / documents/{id}/instances/{instanceId}/rePrompt
Set a document instance in a specific project back to prompt status.
In the demo, this API was used to reset prompt if it already applied on documents.

#### PUT / documents/{id}/instances/{instanceId}/prompts/answers
Answer specified prompts on the document/dossier instance, prompts can either be answered with default answers(if available), the appropriate answers, or if the prompt is not required the prompt can simply be closed.
In the demo, this API was used to answer the given prompt. Here, we are used "USA" and "Canada" country for given prompt selection.

#### POST /documents/{id}/instances/{instanceId}/pdf
Export a specific document instance to a PDF file.
In the project, this API was used to export dossier in pdf to form of octet-stream,base64 data. User has to convert base64 data into string using any library. Here, Base64 is used which part of package java.util default library.  It is used to decode to base64 to pdf string and then download in form of PDF into **user's Download folder** under the name of Microstrayegy.pdf


## How to customize this App 
List of variables are present in **config.properties** file for this demo. You can be customized and configured for your own environment (username, password, project, dossier etc).
```
BASEURL
```
Base URL of REST API. E.g. 'http://localhost:8282/consume-dev/api/'

```
PROJECTID
```
You need to provide your environment ProjectId in cofig.properties
```
DOSSIERID
```
You need to provide your environment dossier Id in cofig.properties

```
USERNAME
```
Login through authorize mode 1 with your username

```
PASSWORD
```
The password you want to be login with

### How to create Prompt report
```
1. Login into MicroStrategy web with your credential
2. Choose Project "MicroStrategy tutorial"
```
![alt text](https://github.com/MicroStrategy/rest-api-samples/tree/master/java-rest/java-rest-prompts/ScreenShot/Project_Name.png)
```
3. Right Click on Create --> New Prompt --> Attribute Element List
```
![alt text](https://github.com/MicroStrategy/rest-api-samples/tree/master/java-rest/java-rest-prompts/ScreenShot/Prompt.png)
```
4. Choose from Attribute --> Customers --> Customer Country from the list
```
![alt text](https://github.com/MicroStrategy/rest-api-samples/tree/master/java-rest/java-rest-prompts/ScreenShot/ElementPrompt.png)
```
5. Save it as "Element of Customer Country" under Shared Report
6. Right Click on Report --> Blank Report
7. From left Panel navigate till Element of Customer Country
8. Add prompt in Filter Section
9. from left panel select Customer Country and Customer State as attribute and Profit, Revenue as metric
```
![alt text](https://github.microstrategy.com/neelpatel/PromptGradleWorkFlow/blob/master/ScreenShot/ApplyPrompt.png)
```
10. Save this Report as Attribute Element Prompt Report
11. Right click on this report--> Properties --> Note Down ID
```
![alt text](https://github.com/MicroStrategy/rest-api-samples/tree/master/java-rest/java-rest-prompts/ScreenShot/ReportId.png)

You may try to run this report for better understanding. 
![alt text](https://github.com/MicroStrategy/rest-api-samples/tree/master/java-rest/java-rest-prompts/ScreenShot/Run%20Prompt.png)

For more information about prompt you may visit [this](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/RESTSDK/Content/topics/REST_API/REST_API_Workflow_Prompts.htm).

### Configuration required for cross origin in Library:
1. Set `auth.cors.origins=*` in MicroStrategyLibrary/WEB-INF/classes/config/configOverride.properties to enable cross origin for rest server.
2. - For 11.x and later Library, remove `X-Frame-Options : SAMEORIGIN` from <MicroStrategyLibrary-Installation-Directory>/WEB-INF/web.xml 
   - For 10.x Library, remove the line of `X-Frame-Options=SAMEORIGIN` from <MicroStrategyLibrary-Installation-Directory>/WEB-INF/classes/config/security_headers-index.properties to allow embedding a iframe with CORS

OR

You can use Microstrategy REST API `PUT /admin/restServerSettings/security` and Set `"allowAllOrigins": true`
Ex:

```json
{
  "allowAllOrigins": true
}
```


