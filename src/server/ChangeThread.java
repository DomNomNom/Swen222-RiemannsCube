package server;

import java.io.IOException;
import java.net.Socket;

import utils.Int3;
import world.RiemannCube;
import world.events.Action;
import world.events.ChatEvent;
import world.events.ChatMessage;
import world.events.Event;
import world.events.PlayerAssign;
import world.events.PlayerMove;
import world.events.PlayerSpawning;
import world.events.RequestPlayer;

/**
 * 
 * @author feshersiva
 */
public class ChangeThread extends Thread {
    
    private Server parentServer;
    
    public ChangeThread(Server s){
        this.parentServer = s;
        setDaemon(true); // terminate if no other normal threads are running
    }
    
    public void run(){
        Change c = null;
        while(true)
        {
            try {
                 c = this.parentServer.changes.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //test printout
            System.out.printf(myName() + " Got a change from client #%d\n", c.clientId);
            
            // process the change and apply it to the world
            Event e = c.event;
            if (e instanceof Action) {    // object is an action
                Action act = (Action)e;
                if (!parentServer.world.applyAction(act)) {
                    System.err.println(myName() + " client["+c.clientId+"] sent a invalid action! " + act);
                    continue; // don't broadcast
                }
                sendToEveryone(e);
            }
            else if (e instanceof ChatEvent) { // object is a chat events
                System.out.println(myName() + " Chat message has happened.");
                sendToEveryone(e);
            }
            else if (e instanceof RequestPlayer) { //object is  a player request
            	System.out.println(myName() + "player has been requested");
            	int newPlayerID = c.clientId; // this should be expanded if we want re-loading of levels
                sendToEveryone(new PlayerSpawning(newPlayerID, new Int3(1,1,1)));
                sendToClient(new PlayerAssign(newPlayerID), parentServer.clientsList.get(c.clientId));
            }
            else {
                System.err.println(myName() + " Unknown event has been sent by the player: " + e);
                sendToEveryone(e);
            }
        }
    }

    
    /** Sends a */
    private void sendToClient(Event e, RemotePlayer rp) {
        try {
            rp.out.writeObject(e); // rp.out is a object out stream that you can write the objects out too
        } catch (IOException e1) {
            System.err.println(myName() + " Coluldn't send information to client " + rp.out);
            //e1.printStackTrace();
        }
    }
    
    
    /** send the changes to everyone */
    private void sendToEveryone(Event e) {
        // TODO: Error checking - what if the sending client isn't in the list
        for(RemotePlayer rp : parentServer.clientsList.values())
            sendToClient(e, rp);
    }

    /** just for more readable console debug output */
    private String myName() { return "[workerThread]"; }
}
