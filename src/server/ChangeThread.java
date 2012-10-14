package server;


import java.io.IOException;
import java.io.StringWriter;

import utils.Configurations;
import utils.Int3;
import world.events.Action;
import world.events.ActivateTrap;
import world.events.ChatEvent;
import world.events.ChatMessage;
import world.events.Event;
import world.events.FullStateUpdate;
import world.events.PlayerAssign;
import world.events.PlayerRelPos;
import world.events.PlayerSpawning;
import world.events.RequestPlayer;
import data.LevelPipeline;

/**
 * A thread that is responsible of handling the changes made to the 
 * world instance. It handles Actions, Chat events and player requests.   
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
            //if(Configurations.debugPrint)  System.out.printf(myName() + " Got a change from client #%d\n", c.clientId);
            
            // process the change and apply it to the world
            Event e = c.event;
            if (e instanceof Action) {    // object is an action
                Action act = (Action)e;
                if (!parentServer.world.applyAction(act)) {
                    System.err.println(myName() + " client["+c.clientId+"] sent a invalid action! " + act);
                    continue; // don't broadcast
                }
                
                // send the change back since it's valid
                if (act instanceof PlayerRelPos)    sendToEveryoneExcept(e, parentServer.clientsList.get(c.clientId)); // TODO maybe a nicer way of getting the remote player
                else                                sendToEveryone(e);
            }
            else if (e instanceof ChatEvent) { // object is a chat events
              //test printout
                if(Configurations.debugPrint)  System.out.println(myName() + " Chat message has happened.");
                sendToEveryone(e);
            }
            else if (e instanceof RequestPlayer) { //object is  a player request
              //test printout
                if(Configurations.debugPrint)  System.out.println(myName() + "player has been requested");
            	int newPlayerID = c.clientId; // this should be expanded if we want re-loading of levels
            	String playerName = ((RequestPlayer)e).playerName();
            	PlayerSpawning spawnEvent = new PlayerSpawning(newPlayerID, new Int3(1, 1, 6), playerName);
            	if (!parentServer.world.applyAction(spawnEvent)) {
                    System.err.println(myName() + " OMG, the player though this action was right: " + spawnEvent);
                    continue; // don't broadcast
                }
                sendToEveryone(spawnEvent);
                sendToClient(new PlayerAssign(newPlayerID), parentServer.clientsList.get(c.clientId));
            }
            else if (e instanceof FullStateUpdate)
            	sendToEveryone(e);
            else if (e instanceof ActivateTrap)
                sendToEveryone(new ChatMessage("Activated a Trap!", ((ActivateTrap)e).playerID));
            else {
                System.err.println(myName() + " Unknown event has been sent by the player: " + e);
                //sendToEveryone(e);
            }
        }
    }

    /** returns a FullStateUpdate of the current state of the world */
    public FullStateUpdate generateFullStateUpdate() {
        LevelPipeline saveLoader = new LevelPipeline();
        StringWriter out = new StringWriter();
        saveLoader.save(parentServer.world, out);
        return new FullStateUpdate(out.toString());
    }
    
    /** Sends a an event to the client that made it */
    public void sendToClient(Event e, RemotePlayer rp) {
        try {
            rp.out.writeObject(e); // rp.out is a object out stream that you can write the objects out too
        } catch (IOException e1) {
            System.err.println(myName() + " Coluldn't send information to client " + rp.out);
            //e1.printStackTrace();
        }
    }
    
    /** send the changes to everyone */
    public void sendToEveryone(Event e) {
        // TODO: Error checking - what if the sending client isn't in the list
        for(RemotePlayer rp : parentServer.clientsList.values())
            sendToClient(e, rp);
    }
    
    /** sends the given event to all players except the one specified */
    public void sendToEveryoneExcept(Event e, RemotePlayer exception) {
        for(RemotePlayer rp : parentServer.clientsList.values())
            if (!rp.equals(exception))
                sendToClient(e, rp);
    }

    /** just for more readable console debug output */
    private String myName() { return "[changeThread]"; }
}
