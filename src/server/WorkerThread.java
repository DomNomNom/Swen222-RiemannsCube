package server;

import java.io.*;
import java.net.*;

import utils.Configurations;
import world.RiemannCube;
import world.events.Action;
import world.events.ChatMessage;
import world.events.Event;
import world.events.PlayerMove;

/**
 * A thread that handles a single client / player and deals with their events.
 * The thread receives changes from the player / client and adds them 
 * to the blocking queue in the world.
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
        setDaemon(true); // terminate if no other normal threads are running
    }

    public void run() {
        Object obj = null;
        ObjectInputStream input = null;

        try {
            input = new ObjectInputStream(socket.getInputStream());
            boolean exit = false;
            while (!exit) {
                // read object from socket input
                try {
                    obj = input.readObject();
                    if(Configurations.debugPrint)  System.out.println(myName() + " got my object: " + obj);
                } catch (ClassNotFoundException e) {
                    System.out.println("Problem reading from input.");
                    e.printStackTrace();
                }

                if (!(obj instanceof Event)) {
                    System.err.println("recieved non-event object!");
                    continue;
                }
                Event e = (Event) obj;

                Change c = new Change(this.playerId, e);
                this.server.changes.add(c);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** just for more readable console debug output */
    private String myName() {
        return "[serverWorker #" + playerId + "]";
    }
}