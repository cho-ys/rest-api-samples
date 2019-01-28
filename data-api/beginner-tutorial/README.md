# Tutorial common workflow for MicroStrategy Data API
## Overview

Here we will introduce a common workflow for MicroStrategy Data API, and we provide different implementations for this workflow, currently, we support Java, NodeJS and curl implementations. 

## MicroStrategy Data API common workflow  
1.	**Logging in**: Authenticate a user
2.	**Publishing cube(optional)**: If cube is not republished, this part can be applied to publish an cube
3.	**Retrieving cube definition**:  cube definition includes attribute and metrics info in the cube  
4.	**Retrieving cube raw data**: different body parameter with different behavior, the body will include requestObjects, sorting, viewFilter, metricLimits json data.  
5.	**Retrieving cube data with existing instanceId**: Get the results of a previously created instance of cube.  
6.	**Logging out**: Close all existing sessions for the authenticated user  
  
## MicroStrategy sample information
We will use the sample cube **“Intelligent Cube - Drilling outside the cube is disabled”** to implement common workflow in following steps, details as below:  
* **Cube ID**: 8CCD8D9D4051A4C533C719A6590DEED8  
* **Project ID**: "B7CA92F04B9FAE8D941C3E9B7E0CD754",  
* **Project Name**: "MicroStrategy Tutorial",  
* **Demo REST Server swagger UI**:  [**https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html?**](https://demo.microstrategy.com/MicroStrategyLibrary2/api-docs/index.html) 

## Different code implementation(Getting Started)   
### [Curl implementation](curl/tutorial-curl-implementation.md)   
### [Java code implementation](java/README.md)    
### [NodeJs implementation](nodejs/README.md)  

## Tree raw cube/report json data Transformation
Since raw json data from response of the endpoints _POST /cubes/{cubeId}/instances, POST /reports/{reportId}/instances,  GET /cubes/{cubeId}/instances/{instanceId} and GET /reports/{reportId}/instances/{instanceId}_ are tree json, it is not intuitionistic to show, so we provide tree data transformation code with Java and Javascript implementations, code links as below：  
* [Javascript transformation code](../grid-data-transformer/js/gridDataTransformer.js)   
* [Java transformation code](../gridDataTransformer/java/src/main/java/com/microstrategy/samples/JsonTreeDataTransformation)  
