package gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import world.RiemannCube;

import client.Client;

import com.jogamp.opengl.util.Animator;

/**Game Frame is the overall window of the game
 * 
 * @author David Saxon 300199370
 */
public class GameFrame extends JFrame {
    
    //FIELDS
    private static final long serialVersionUID = 1L;
    
    private Client client; //the game client
    private ViewPort view; //the view panel
    private ChatPanel chat; //the chat panel
    
    private String ip; //the IP address of the level
    private RiemannCube level;
    
    private boolean high; //is true when high graphics is enable
    private boolean free; //is true when free camera is enabled
    
    //CONSTUCTOR
    /**Constructs a new game frame
     * @param high is true if the game should be run at full graphics
     * @param free is true if the game should run in free camera mode*/
    public GameFrame(boolean high, boolean free, String ip, RiemannCube level) {
        super("Riemann's cube");
        this.high = high;
        this.free = free;
        this.ip = ip;
        this.level = level;
    }
    
    //METHODS
    /**Initialises the game frame by creating the panels of the game*/
    public void init() {
    	if (ip == null) { //if an IP address has not been defined get one
    		ip = (String)JOptionPane.showInputDialog(this, "Enter the Host IP", "Select Server",
    		                    JOptionPane.PLAIN_MESSAGE, null, null, null);
    		if (ip == null || ip.equals("")) ip = "localhost";
    	}
    	
    	client = new Client(ip); //create a new client with the ip
    	
    	setSize(900, 600);
        chat = new ChatPanel(this);
        view = new ViewPort(this, 700, 600, high, free, level); 
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //hide the cursor by giving it a blank image
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        getContentPane().setCursor(blankCursor);
    }

	/**Runs the game*/
    public void execute() {
        getContentPane().add(view, BorderLayout.CENTER);
        getContentPane().add(chat, BorderLayout.WEST);
        ViewPort.animator = new Animator(view); 
        ViewPort.animator.start();
        ChatPanel.animator = new Animator(chat);
        ChatPanel.animator.start();
        setVisible(true);        
    }
    
    /**Quits the game*/
    public void exit() {
    	WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }
    
    /**Returns the client
     * @return the client*/
    public Client getClient() {
    	return client;
    }
    
}
