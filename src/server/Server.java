package server;

import java.io.*;
import java.net.*;

import world.RiemannCube;
import world.events.Action;
import world.events.ChatMessage;
import world.events.PlayerMove;

public class Server extends Thread {

    private int broadcastClock;
    private int uid;
    private ServerSocket socket;
    private RiemannCube world;

    public Server(RiemannCube w) {
        try {
            this.socket = new ServerSocket();
            socket.bind(new InetSocketAddress(55554));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.world = w;
    }

    public void run() {
        try {
            // First, write the period to the stream

            Object obj = null;

            boolean exit = false;
            while (!exit) {
                Socket clientSocket = socket.accept();
                // TODO create new thread here!
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                try {
                    // read object from socket input
                    try {
                        obj = input.readObject();
                    } catch (ClassNotFoundException e) {
                        System.out.println("Problem reading from input.");
                        e.printStackTrace();
                    }
                    // object is an action
                    if (obj instanceof Action) {
                        Action act = (Action) obj;
                    }
                    // object is a player move
                    else if (obj instanceof PlayerMove) {
                        PlayerMove move = (PlayerMove) obj;
                    }
                    // object is a chat message
                    else if (obj instanceof ChatMessage) {
                        ChatMessage message = (ChatMessage) obj;
                    }

                    // Now, broadcast the state of the board to client

                    // byte[] state = board.toByteArray();
                    // output.writeInt(state.length);
                    // output.write(state);

                    output.flush();
                    Thread.sleep(broadcastClock);
                } catch (InterruptedException e) {
                }
            }
            socket.close(); // release socket ... v.important!
        } catch (IOException e) {
            System.err.println("PLAYER " + uid + " DISCONNECTED");
        }
    }
}
