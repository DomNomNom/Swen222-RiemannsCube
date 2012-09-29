package client;

import gui.ChatPanel;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import data.XMLParser;

import utils.Int3;
import world.RiemannCube;
import world.events.Action;
import world.events.ChatMessage;
import world.events.Event;
import world.events.PlayerAssign;
import world.events.PlayerMove;
import world.events.RequestPlayer;
import world.objects.Player;

/**
 * A class that represents a client for every player.
 * @author feshersiva
 */
public class Client {

    
    private final ChatPanel chat;
    private RiemannCube world;

    private Player player;
    public  Player player(){ return player; }
    
    private ClientNetworking networking;
    //public ClientNetworking networking() { return networking; }
    
    
    /**@return the current world*/
    public RiemannCube getWorld() {
    	return world;
    }
    
    public Client(String ip, ChatPanel chat) {
        networking = new ClientNetworking(ip);
        networking.start();
        this.chat = chat;
        
        //read the world from a file
        try {
			this.world = XMLParser.readXML(new File("Levels/Test.xml"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        
        
        
        //request a player
        networking.push(new RequestPlayer());
    }
    
    
    
    public void update(int dt) {
        for (Event e : networking.poll()) {
            System.out.println(myName() + "recieved a event");
            if (e instanceof Action) {
                if (!world.applyAction((Action) e) ) {
                    System.err.println(myName() + " wasnt able to apply the servers action! D:");
                    // TODO: request a full state update and apply it
                }
            }
            else if (e instanceof ChatMessage)
                chat.addMessage((ChatMessage) e);
            else if (e instanceof PlayerAssign) {
                int id = ((PlayerAssign)e).playerID;
            }
            else 
                System.err.println(myName() + " has recieved a unhandeled event: " + e);
        }
    }
    
    public void push(Event e) {
        networking.push(e);
    }
    
    private String myName() { return "[Client #" + ((player==null)? null : player.id()) + "]"; }
}
