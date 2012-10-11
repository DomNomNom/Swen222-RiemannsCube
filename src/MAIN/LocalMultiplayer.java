package MAIN;

import gui.GameFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import server.Server;
import utils.Configurations;
import world.RiemannCube;
import data.XMLParser;


/**
 * starts a server and 2 clients connected to it.
 *
 * @author dom
 */
public class LocalMultiplayer {
    public static void main(String[] args) {
        // settings
        Configurations.debugPrint = true;
        GameFrame.high = true;
        GameFrame.free = false;
        GameFrame.noFloor = false;
        GameFrame.showFps = false;
        
        //load the level for the server
        RiemannCube world = null;
        try {   //read the world from a file
            world = XMLParser.readXML(new FileInputStream(new File("Levels/Test.xml")));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        
        //create the server
        Server server = new Server(world);
        server.start();
        
        //create a game frames that will connect to the server
        GameFrame window = new GameFrame("localhost");
        window.init();
        window.execute();
        GameFrame window2 = new GameFrame("localhost");
        window2.init();
        window2.execute();
    }
}
