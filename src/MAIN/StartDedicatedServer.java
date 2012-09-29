package MAIN;

import javax.swing.JFrame;

import gui.ChatPanel;
import gui.GameFrame;
import client.Client;
import server.Server;
import utils.Int3;
import world.RiemannCube;
import world.events.ChatMessage;

public class StartDedicatedServer {

    public static void main(String[] args) {
    	// TODO: move this to test code and only run the server here
        Server server = new Server(new RiemannCube(new Int3(1, 1, 1)));
        server.start();
        
        
        
    }


}
