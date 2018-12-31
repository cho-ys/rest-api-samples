## Java Project Name:
MicroStrategy Export PDF workflow

## Project Decription:
This project is a sample standalone program to show the users how to use MicroStrategy REST APIs to export pdf of dossier or documents. 

A list of REST APIs were used in the project. They were used to authenticating, creating new instance of respective dossier, then exporting to PDF. 

The REST APIs used in this demo are:
#### POST /auth/login 
Creating a MicroStrategy session given credentials and authentication mode. An authToken will be returned for latter operations.
In the project, this API was used to create a configuration session.

#### POST / documents/{id}/instances
Execute a specific dossier and create an instance of the dossier.
In the project, this API was used to create a new instance pof respective dossier.

#### POST /documents/{id}/instances/{instanceId}/pdf
Export a specific document instance to a PDF file.
In the project, this API was used to export dossier in pdf to form of octet-stream,base64 data. User has to convert base64 data into string using any library. Here, Base64 is used which part of package java.util default library.  It is used to decode to base64 to pdf string and then download in form of PDF into **user's Download folder** under the name of Microstrayegy.pdf


## How to customize this Project 
List of variables are present in **config.properties** file for this demo. You can be customized and configured for your own environment (username, password, project, dossier etc).
To see response header and body parameter which get from Microstrategy rest api, set boolean value true in ApiOperation.java class for showReponseHeader and showReponseBody  

```
BASEURL
```
Base URL of REST API. E.g. 'http://demo.microstrategy.com/MicroStrategyLibrary/api/'

```
PROJECTID
```
If you want to use a different project other than ‘MobileDossier’, you can provide the object ID of the project.
```
DOSSIERID
```
Here it is used a dossier which is under 'Public Objects' folder. If you want to use a different dossier other than ‘Public Objects’, you can provide the object ID of the configuration-level dossier id.
```
LOGINMODE
```
Login through Guest mode 8.



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
