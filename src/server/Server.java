package server;

import java.io.*;
import java.net.Socket;

public class Server extends Thread {

    private final int broadcastClock;
    private final int uid;
    private final Socket socket;

    public Server(Socket socket, int uid, int broadcastClock) {
        this.broadcastClock = broadcastClock;
        this.uid = uid;
        this.socket = socket;
    }

    public void run() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(
                    socket.getOutputStream());
            // First, write the period to the stream
            output.writeInt(uid);

            // output.writeInt(board.width());
            // output.writeInt(board.height());
            // output.write(board.wallsToByteArray());

            boolean exit = false;
            while (!exit) {
                try {
                    if (input.available() != 0) {
                        // read direction event from client.
                        int dir = input.readInt();
                        switch (dir) {
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            break;
                        }
                    }

                    // Now, broadcast the state of the board to client

                    // byte[] state = board.toByteArray();
                    // output.writeInt(state.length);
                    // output.write(state);

                    output.flush();
                    Thread.sleep(broadcastClock);
                } catch (InterruptedException e) {
                }
            }
            socket.close(); // release socket ... v.important!
        } catch (IOException e) {
            System.err.println("PLAYER " + uid + " DISCONNECTED");
        }
    }

}
