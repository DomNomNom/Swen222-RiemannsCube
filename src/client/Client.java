package client;

import gui.ChatPanel;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import data.XMLParser;

import utils.Configurations;
import utils.Int3;
import world.RiemannCube;
import world.events.Action;
import world.events.ChatMessage;
import world.events.Event;
import world.events.FullStateUpdate;
import world.events.PlayerAssign;
import world.events.PlayerMove;
import world.events.RequestPlayer;
import world.objects.Player;

/**
 * A class that represents a client for every player.
 * @author feshersiva
 */
public class Client {

    
    private ChatPanel chat;
    private RiemannCube world;

    private int playerID = -1;
    public  Player player(){ 
    	if (world == null) return null;
    	return world.players.get(playerID);
    	}
    private String playerName;
    
    private ClientNetworking networking;
    //public ClientNetworking networking() { return networking; }
    
    private boolean myFirstFullStateUpdate = true;
    
    /**@return the current world*/
    public RiemannCube getWorld() {
    	return world;
    }
    
    public Client(String ip, String playerName) {
        this.playerName = playerName;
        networking = new ClientNetworking(ip);
        networking.start();
    }
    
    /** used to initialize chat. this is not in the constructor to resolve a circular dependency */
    public void setChat(ChatPanel chat) {
        this.chat = chat;
    }
    
    /**
     * A method that updates the state of the world with
     * the events that exists in the Client Networking.
     * @param dt
     */
    public void update(int dt) {
        for (Event e : networking.poll()) {
          //test printout
            if (Configurations.debugPrint)  System.out.println(myName() + "recieved a event: " + e);   
            
            if (e instanceof Action) {
                if (!world.applyAction((Action) e) ) {
                    System.err.println(myName() + " wasnt able to apply the servers action: " + e);
                }
            }
            else if (e instanceof ChatMessage) {
                chat.addMessage((ChatMessage) e);
            }
            else if (e instanceof PlayerAssign) {
                playerID = ((PlayerAssign)e).playerID;
            }
            else if(e instanceof FullStateUpdate){
            	this.world = XMLParser.readXML(new ByteArrayInputStream(((FullStateUpdate)e).level.getBytes()));
            	if (myFirstFullStateUpdate){ // request a player when we first connect
            	    networking.push(new RequestPlayer(playerName)); 
            	    myFirstFullStateUpdate = false;
            	}
            }
            else 
                System.err.println(myName() + " has recieved a unhandeled event: " + e);
        }
    }
    
    /**
     * A method that adds an event to the client networking.
     * @param e
     */
    public void push(Event e) {
        networking.push(e);
        System.out.println(myName()+ " pushing: " + e);
    }
    
    private String myName() { return "[Client #" + playerID + "]"; }
}
