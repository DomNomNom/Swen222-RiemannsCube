package server;

import java.io.*;
import java.net.*;

import world.RiemannCube;
import world.events.Action;
import world.events.ChatMessage;
import world.events.Event;
import world.events.PlayerMove;

/**
 * A thread that handles a client / player and deals 
 * with their events.
 * @author feshersiva
 */
public class WorkerThread extends Thread {

    private Server server;
    private int playerId;
    private Socket socket;

    public WorkerThread(Server server, int playId, Socket sock) {
        this.server = server;
        this.playerId = playId;
        this.socket = sock;
    }

    public void run() {
        Object obj = null;
        ObjectInputStream input = null;

        try {
            input = new ObjectInputStream(socket.getInputStream());
            boolean exit = false;
            while (!exit) {
                // ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                // read object from socket input
                try {
                    obj = null;

                    if(input.available() > 0){
                        System.out.println("No object yet");
                        obj = input.readObject();
                        System.out.println("I got a message");                        
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("Problem reading from input.");
                    e.printStackTrace();
                }
                
                Event e = (Event)obj;
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
                
                Change c = new Change(this.playerId, e);
                this.server.changes.add(c);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}