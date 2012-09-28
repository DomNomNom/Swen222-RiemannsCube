package MAIN;

import javax.swing.JFrame;

import gui.ChatPanel;
import gui.GameFrame;
import client.Client;
import server.Server;
import utils.Int3;
import world.RiemannCube;
import world.events.ChatMessage;

public class StartCubeServer {

    public static void main(String[] args) {
        Server server = new Server(new RiemannCube(new Int3(1, 1, 1)));
        server.start();
        
        
        Client client = new Client("localhost", new MockChatPanel());
        System.out.println("started server and client");

        client.push(new ChatMessage("HI! :D", 0));
        
        client.pull();
        
        
        System.exit(0); // force exit of all threads
        /*
        try {
            server.interrupt();
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finished");
        */
    }

    private static class MockChatPanel extends ChatPanel {

        public MockChatPanel() {
            super(new GameFrame(""));
        }
        
        @Override
        public void addMessage(ChatMessage message) {
            System.out.println("[ChatPanel] Chat object recieved a message! :D  here it is:");
            System.out.println("   [" +message.speakerID+ "] " +message.message);
        }
    }
}
