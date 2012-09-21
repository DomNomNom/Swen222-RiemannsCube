package MAIN;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import world.items.Key;

import data.XMLParser;

public class XMLTester {

    public static void main(String[] args){
        try{
            XMLParser.readXML(new File("Levels/Test.xml"));
            
        }catch(IOException e){
            System.out.println("Didn't find file");
        }
    }
}
