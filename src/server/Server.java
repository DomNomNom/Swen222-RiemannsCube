package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import client.Client;

import utils.Int3;
import world.RiemannCube;
import world.events.Action;
import world.events.ChatMessage;
import world.events.PlayerMove;

/**
 * A server for the game that holds all of the clients 
 * and there allocated sockets. 
 * @author feshersiva
 *
 */
public class Server extends Thread {

    private int uid;
    private ServerSocket socket;
    private RiemannCube world;
    
    protected Map<Integer, RemotePlayer> clientsList;
    protected BlockingQueue<Change> changes = new LinkedBlockingQueue<Change>();

    public Server(RiemannCube w) {
        try {
            this.socket = new ServerSocket();
            socket.bind(new InetSocketAddress(55554));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.world = w;
        this.clientsList = new HashMap<Integer, RemotePlayer>();
    }

    public void run() {
        try {
            int id = 1;
            ChangeThread changeT = new ChangeThread(this);
            changeT.start();
            
            boolean exit = false;
            while (!exit) {
                Socket clientSocket = socket.accept();
                clientsList.put(id, new RemotePlayer(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream())));
                WorkerThread wThread = new WorkerThread(this, id, clientSocket);
                wThread.start();
                id++;
            }
            socket.close(); // release socket ... v.important!
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("PLAYER " + uid + " DISCONNECTED");
        }
    }

    public static void main(String[] args) {
        Thread t = new Server(new RiemannCube(new Int3(1, 1, 1)));
        t.start();
        Client c = new Client("127.0.0.1");
        PlayerMove pm = new PlayerMove(0, new Int3());
        c.push(pm);
    }
}
