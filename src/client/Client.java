package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import world.events.Action;
import world.events.ChatMessage;
import world.events.Event;
import world.events.PlayerMove;

public class Client {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private int port;
    private int totalSent;

    public Client(String ip) {
        this.port = 55554;  //Random port number
        try {
            InetAddress ipAddress = InetAddress.getByName(ip);
            this.socket = new Socket(ipAddress, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void push(Event event){
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(event);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                output.close(); //close the stream.
            } catch (IOException e) {
                e.printStackTrace();
            }            
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

    /**
     * The following method calculates the rate of data received in bytes/s,
     * albeit in a rather coarse manner.
     * @param amount
     * @return
     */
    private int rate(int amount) {
        rateTotal += amount;
        long time = System.currentTimeMillis();
        long period = time - rateStart;
        if (period > 1000) {
            // more than a second since last calculation
            currentRate = (rateTotal * 1000) / (int) period;
            rateStart = time;
            rateTotal = 0;
        }

        return currentRate;
    }

    private int rateTotal = 0; // total accumulated this second
    private int currentRate = 0; // rate of reception last second
    private long rateStart = System.currentTimeMillis(); // start of this accumulation period
}
