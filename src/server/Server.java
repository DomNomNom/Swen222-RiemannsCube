package server;

import gui.GameFrame;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import client.Client;
import data.LevelPipeline;

import utils.Int3;
import world.RiemannCube;
import world.events.Action;
import world.events.ChatMessage;
import world.events.FullStateUpdate;
import world.events.PlayerMove;

/**
 * A server for the game that holds all of the clients 
 * and only handles initial contact with them. (sets up WorkerThreads) 
 *
 * @author feshersiva, schmiddomi
 */
public class Server extends Thread {

    private ServerSocket socket;
    protected RiemannCube world;
    
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
            int id = 0;
            ChangeThread changeT = new ChangeThread(this);
            changeT.start();
            boolean exit = false;
            
            while (!exit) {
                // create a new WorkerThread for each client
                Socket clientSocket = socket.accept();
                clientsList.put(id, new RemotePlayer(clientSocket, new ObjectOutputStream(clientSocket.getOutputStream())));
                WorkerThread wThread = new WorkerThread(this, id, clientSocket);
                wThread.start();
                changeT.sendToClient(changeT.generateFullStateUpdate(), clientsList.get(id));  // send the current world state
                ++id;
            }
            socket.close(); // release socket ... v.important!
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread t = new Server(new RiemannCube(new Int3(1, 1, 1)));
        t.start();
        GameFrame.high = false;
        GameFrame.free = true;
        GameFrame.noFloor = true;
        GameFrame window = new GameFrame(null);
        window.init();
        window.execute();
        
//        Client c = new Client("127.0.0.1", null);
//        PlayerMove pm = new PlayerMove(0, new Int3());
//        c.push(pm);
        
    }
}
