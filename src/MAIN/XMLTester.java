package MAIN;

import java.io.File;
import java.io.IOException;

import data.XMLParser;

public class XMLTester {

    public static void main(String[] args){
        try{
            XMLParser.readXML(new File("Test.xml"));
            
        }catch(IOException e){
            System.out.println("Didn't find file");
        }
    }
}
