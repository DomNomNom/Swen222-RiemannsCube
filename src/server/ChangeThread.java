package server;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;

import utils.Configurations;
import utils.Int3;
import world.RiemannCube;
import world.events.Action;
import world.events.ChatEvent;
import world.events.ChatMessage;
import world.events.Event;
import world.events.FullStateUpdate;
import world.events.LevelChange;
import world.events.PlayerAssign;
import world.events.PlayerRelPos;
import world.events.PlayerSpawning;
import world.events.RequestPlayer;
import world.objects.Player;
import data.LevelPipeline;
import data.XMLParser;

/**
 * A thread that is responsible of handling the changes made to the 
 * world instance. It handles Actions, Chat events and player requests.   
 * @author feshersiva, schmiddomi
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
            
            // process the change and apply it to the world
            Event e = c.event;
            if (e instanceof Action) {    // object is an action
                Action act = (Action)e;
                if (!parentServer.world.applyAction(act)) {
                    System.err.println(myName() + " client["+c.clientId+"] sent a invalid action! " + act);
                    continue; // don't broadcast
                }
                
                // send the change back since it's valid
                if (act instanceof PlayerRelPos)    sendToEveryoneExcept(e, parentServer.clientsList.get(c.clientId));
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
            	PlayerSpawning spawnEvent = new PlayerSpawning(newPlayerID, new Int3(0, 0, 0), playerName);
            	if (!parentServer.world.applyAction(spawnEvent)) {
                    System.err.println(myName() + " OMG, the player could not spawn: " + spawnEvent);
                    continue; // don't broadcast
                }
                sendToEveryone(spawnEvent);
                sendToClient(new PlayerAssign(newPlayerID), parentServer.clientsList.get(c.clientId));
            }
            else if (e instanceof LevelChange) {
                LevelChange l = (LevelChange) e;
                
                try {
                    RiemannCube oldWord = parentServer.world;
                    parentServer.world = XMLParser.readXML(new FileInputStream(new File("Levels/"+l.levelName)));
                    RiemannCube newWord = parentServer.world;
                    
                    // spawn the players in the new Riemann 
                    for (Player p : oldWord.players.values())
                        newWord.applyAction(new PlayerSpawning(p.id, p.pos(), p.name)); // assuming the level can hold the players
                        
                    sendToEveryone(new FullStateUpdate(newWord.toString()));
                    // TODO? send a player assign maybe
                }
                catch (FileNotFoundException e1) {    e1.printStackTrace();   }

            }
            else {
                System.err.println(myName() + " Unknown event has been sent by the player: " + e);
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
