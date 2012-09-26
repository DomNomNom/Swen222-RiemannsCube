package MAIN;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import world.RiemannCube;
import world.items.Key;

import data.XMLParser;

public class XMLTester {

    public static void main(String[] args){
        try{
            RiemannCube cube = XMLParser.readXML(new File("Levels/TestLevelForDavid.xml"));
            System.out.println("No Errors");
            
            
        }catch(IOException e){
            System.out.println("Didn't find file");
        }
    }
}
