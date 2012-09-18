package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

public class Client extends Thread implements KeyListener {

    private final Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private int uid;
    private int totalSent;

    public Client(Socket socket) {
        this.socket = socket;
    }
    
    public void run() {
        try {            
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            
            // First job, is to read the period so we can create the clock                
            uid = input.readInt();                    
            int width = input.readInt();
            int height = input.readInt();    
            int bitwidth = width%8 == 0 ? width : width+8;
            int bitsize = (bitwidth/8)*height;
            byte[] wallBytes = new byte[bitsize];
            input.read(wallBytes);            
            System.out.println("Game Client UID: " + uid);
            
//            game = new Board(width,height);
//            game.wallsFromByteArray(wallBytes);            
//            BoardFrame display = new BoardFrame("Pacman (client@" + socket.getInetAddress() + ")",game,uid,this);            
            
            boolean exit=false;
            long totalRec = 0;

            while(!exit) {
                // read event
                int amount = input.readInt();
                byte[] data = new byte[amount];
                input.readFully(data);
                
//                game.fromByteArray(data);                
//                display.repaint();
                
                totalRec += amount;
                // print out some useful information about the amount of data
                // sent and received
                System.out.print("\rREC: " + (totalRec / 1024) + "KB ("
                        + (rate(amount) / 1024) + "KB/s) TX: " + totalSent
                        + " Bytes");            
            }
            socket.close(); // release socket ... v.important!
        } catch(IOException e) {
            System.err.println("I/O Error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
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

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }
}
