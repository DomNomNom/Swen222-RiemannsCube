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

/**
 * A class that represents a thread that controls 
 * the network interaction for the client.
 * @author feshersiva
 */
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

            System.out.println(myName() + ipAddress.toString());
            this.socket = new Socket(ipAddress, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * A method that return a list of events which 
     *  were added science the the last frame.
     * @return the List of events.
     */
    public List<Event> poll() {
        int items = events.size();
        List<Event> ret = new ArrayList<Event>(items);
        events.drainTo(ret, items);
        return ret;
    }
    
    /**
     * A method that outputs an event.
     * @param event
     */
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
            Event currentEvent = nextEvent();
            if (currentEvent == null) {
                System.err.println(myName() + "recieved null! D:");
                continue;
            }
            events.offer(currentEvent);
        }
    }
    
    /**
     * A method that receives the next event from 
     * the input stream.
     * @return
     */
    public Event nextEvent(){
        Object obj = null;
        
        try {
            obj = input.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        
        if (!(obj instanceof Event)){
            System.err.println(myName() + " Recieved an object that is not an event: " + obj);
            return null;
        }
        
        return (Event) obj;
    }
    

    private String myName() { return "[Client]"; }
}
