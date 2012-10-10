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
import world.events.Event;
import world.events.FullStateUpdate;
import client.Client;
import client.ClientNetworking;

public class NetworkingTests {

	@Test
	public void testChatMEssage() {
	    Configurations.debugPrint = false;
		Server server = new Server(new RiemannCube(new Int3(1, 1, 1)));
        server.start();
        
        ClientNetworking client = new ClientNetworking("localhost");

        String content = "HI! :D  <>(){}' \";`~ DROP TABLE LEVELS"; // tests for SQL injection, because why not?
        int speaker = 3;
        ChatMessage message = new ChatMessage(content, speaker);
        client.push(message);
        
        Event response = client.nextEvent();
        assertTrue(response instanceof FullStateUpdate); // since this is always the first thing that gets sent
        response = client.nextEvent();
        assertTrue(message.equals(response));
        
    }
}
