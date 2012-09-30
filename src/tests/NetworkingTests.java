package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import gui.ChatPanel;
import gui.GameFrame;

import org.junit.Test;

import server.Server;
import utils.Configurations;
import utils.Int3;
import world.RiemannCube;
import world.events.ChatMessage;
import client.Client;
import client.ClientNetworking;

public class NetworkingTests {

	@Test
	public void testChatMEssage() {
	    Configurations.debugPrint = false;
		Server server = new Server(new RiemannCube(new Int3(1, 1, 1)));
        server.start();
        
        ClientNetworking client = new ClientNetworking("localhost");
        System.out.println("started server and client");

        String content = "HI! :D  <>(){}' \";`~ DROP TABLE LEVELS"; // tests for SQL injection, because why not?
        int speaker = 3;
        ChatMessage message = new ChatMessage(content, speaker);
        client.push(message);
        
        ChatMessage response = (ChatMessage) client.nextEvent();
        assertTrue(message.equals(response));
        
        //client.pull();
        //assertTrue(mockOutput.log.size() == 1);
        //assertTrue(message.equals(mockOutput.log.get(0)));
        
        //System.exit(0); // force exit of all threads

        /*
        System.out.println("nearly finished");
        try {
            server.interrupt();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        System.out.println("finished");
        	
    }

}
