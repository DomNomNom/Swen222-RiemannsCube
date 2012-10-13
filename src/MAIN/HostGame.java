package MAIN;

import gui.GameFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import data.XMLParser;
import server.Server;
import utils.Configurations;
import utils.Int3;
import world.RiemannCube;

/**This hosts a new game by creating a new server and then a new client
 * that is connected to the server using localhost
 *
 * @author David Saxon*/
public class HostGame {
	
	
	
	public static void main(String[] args) {
	    Configurations.debugPrint = true;
	    
		//load the level for the server
		RiemannCube world = null;
        try {   //read the world from a file
			world = XMLParser.readXML(new FileInputStream(new File("Levels/Hub.xml")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		//create the server
		Server server = new Server(world);
	    server.start();
	    
	    //create a game frame that will connect to the server
		GameFrame.high = true;
		GameFrame.free = false;
		GameFrame.noFloor = false;
		GameFrame.showFps = false;
		GameFrame.sound = false;
    	GameFrame window = new GameFrame("localhost");
        window.init();
        window.execute();
	}
}
