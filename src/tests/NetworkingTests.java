package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import gui.ChatPanel;
import gui.GameFrame;

import org.junit.Test;

import server.Server;
import utils.Int3;
import world.RiemannCube;
import world.events.ChatMessage;
import client.Client;

public class NetworkingTests {

	@Test
	public void testChatMEssage() {
		Server server = new Server(new RiemannCube(new Int3(1, 1, 1)));
        server.start();
        
        MockChatPanel mockOutput = new MockChatPanel();
        Client client = new Client("localhost", mockOutput);
        System.out.println("started server and client");

        String content = "HI! :D  <>(){}' \";`~ DROP TABLE LEVELS"; // tests for SQL injection, because why not?
        int speaker = 3;
        ChatMessage message = new ChatMessage(content, speaker);
        client.push(message);
        
        client.pull();
        assertTrue(mockOutput.log.size() == 1);
        assertTrue(message.equals(mockOutput.log.get(0)));
        
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
	
	private static class MockChatPanel extends ChatPanel {

		public List<ChatMessage> log = new ArrayList<ChatMessage>();
		public List<Integer> log_speaker = new ArrayList<Integer>();
		
        public MockChatPanel() {
            super(new GameFrame(""));
        }
        
        @Override
        public void addMessage(ChatMessage message) {
        	log.add(message);
        }
    }

}
