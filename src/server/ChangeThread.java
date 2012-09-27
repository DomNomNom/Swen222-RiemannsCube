package server;

import java.net.Socket;

import world.RiemannCube;

/**
 * 
 * @author feshersiva
 */
public class ChangeThread extends Thread {
    
    private Server parentServer;
    
    public ChangeThread(Server s){
        this.parentServer = s;
    }
    
    public void run(){
        Change c = null;
        while(true)
        {
            try {
                 c = this.parentServer.changes.take();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            System.out.printf("Got a change from client #%d\n", c.clientId);
            // process the change and apply it to the world
            
            // send the changes to everyone else
            // TODO: Error checking - what if the client isn't in the list
            for(RemotePlayer rp : parentServer.clientsList.values())
            {
                if(rp.equals(parentServer.clientsList.get(c.clientId)))
                    continue;
                
                //  rp.out is a object out stream that you can write the objects out too
            }
        }
    }

}
