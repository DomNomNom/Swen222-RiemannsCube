package server;

import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * A helper class that holds a player's socket and output stream
 * for easier handling. 
 * @author feshersiva
 */
public class RemotePlayer {
    
    public Socket socket;
    public ObjectOutputStream out;
    
    public RemotePlayer(Socket s, ObjectOutputStream o){
        this.socket = s;
        this.out = o;
    }
}
