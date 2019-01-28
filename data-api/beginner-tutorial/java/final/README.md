# Java_Restful_API_Invokation_Tutorial

1. Get Sample Project Code
   use command git clone https://github.microstrategy.com/chehu/Java_Restful_API_Invokation_Tutorial.git to get sample project repository
	

2. Sample Project Structure
   
                    | -- src/main/java/com/demo
						   | -- RequestParameters.java					 
						   | -- RequestHandler.java
						   | -- Tutorial.java  (main class, entry point of project)
	    
	                | -- build.gradle (gradle is used as the build tool for this project, this file simply declare dependencies used by sample project)
	
	
3. Just run Tutorial.main(), following steps defined in workflow will be executed. 

   1. Login In
   2. Publish Cube 
   3. Retrieving cube definition
   4. Retrieving cube raw data 
         
		 4.1 limit & offset feature
		 
		 4.2 Retrieving cube data with requestObjects
	                   
         4.3 Retrieving cube data with sorting					   
	
	     4.4 Retrieving cube data with metricLimits
		 
		 4.5 Retrieving cube data with viewFilter
		 
   5. Retrieving cube data with aboved created instanceId
   
   6. Logout 