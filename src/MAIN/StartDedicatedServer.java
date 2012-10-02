package MAIN;

import javax.swing.JFrame;

import gui.ChatPanel;
import gui.GameFrame;
import client.Client;
import server.Server;
import utils.Configurations;
import utils.Int3;
import world.RiemannCube;
import world.events.ChatMessage;

public class StartDedicatedServer {

    public static void main(String[] args) {
        Configurations.debugPrint = true;
        
        Server server = new Server(new RiemannCube(new Int3(8,8,8)));
        server.start();
        
    }


}
