package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import world.events.Action;
import world.events.ChatMessage;
import world.events.Event;

public class ClientNetworking extends Thread {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private int port;
    
    private boolean running = true;
    
    private final LinkedBlockingQueue<Event> events = new LinkedBlockingQueue<Event>();
    
    public ClientNetworking(String ip) {
        this.port = 55554;  //Random port number
        
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            System.out.println(ipAddress.toString());
            this.socket = new Socket(ipAddress, port);
            output = new ObjectOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public List<Event> poll() {
        int items = events.size();
        List<Event> ret = new ArrayList<Event>(items);
        events.drainTo(ret, items);
        return ret;
    }
    
    public void push(Event event){
        try {
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }            
    }
    
    public void run() {
        while (running) {
            
        }
    }
    
    public Event nextEvent(){
        Object obj = null;
        try {
            input = new ObjectInputStream(socket.getInputStream());
            obj = input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        if (!(obj instanceof Event)){
            System.err.println("Recieved an event that is not ");
            return null;
        }
        
        
        
        System.out.println(myName()+" I got an object !");
        
        
        return null;
    }
    

    private String myName() { return "[Client]"; }
}
