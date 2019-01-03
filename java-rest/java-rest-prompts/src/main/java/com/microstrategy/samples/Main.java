package com.microstrategy.samples;



import org.json.simple.parser.ParseException;
import java.io.IOException;

/**
 * @author patelNeel
 *
 * Created on 13- Dec- 2019
 *
 * Main class to demonstrate how to use of Microstrategy rest api to export prompt dossier/document.
 *
 */
public class Main {

    public static void main(String[] args){
        try{
            ApiOperation apiOperation = new ApiOperation();

            //Login
            String auth =  apiOperation.login();
            System.out.println("AuthToken is:  "+auth +"\n\n");

            //Get Instance of object prompt
            String instanceId =  apiOperation.getObjectPromptDossierInstance();
            System.out.println("Instance Value is:  "+instanceId+"\n\n");

            //Reset prompt value
            apiOperation.reprompt();

            //Update Prompt Answer
            apiOperation.updatΩeDossierWithPrompt();

            //Export PDF into Base64
            String encode = apiOperation.exportPDF();
            System.out.println("Encode String is:  "+encode+"\n\n");

            //Decode into PDF
            apiOperation.decodeIntoPDF(encode);
        }catch(ParseException px){
            px.printStackTrace();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

}
