package client;

import gui.ChatPanel;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import world.events.Action;
import world.events.ChatMessage;
import world.events.Event;
import world.events.PlayerMove;

/**
 * A class that represents a client for every player.
 * @author feshersiva
 */
public class Client {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private int port;
    
    private final ChatPanel chat;

    public Client(String ip, ChatPanel chat) {
        this.port = 55554;  //Random port number
        this.chat = chat;
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            this.socket = new Socket(ipAddress, port);
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void push(Event event){
        try {
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }            
    }
    
    public Event pull(){
        Object obj = null;
        try {
            input = new ObjectInputStream(socket.getInputStream());
            obj = input.readObject();
            //TODO what here !
            
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
                chat.addMessage(message);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        if(obj instanceof Event){
            return (Event)obj;
        }
        return null;
    }
}
