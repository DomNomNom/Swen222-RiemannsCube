package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

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
    
    public static boolean high = true; //is true when high graphics is enable
    public static boolean free = false; //is true when free camera is enabled
    public static boolean noFloor = false; //is true to not render floor
    public static boolean showFps = false; //is true to display fps
    
    /**
     * Gets the TextField so that the view port can request focus on it.
     * @return
     */
    public JTextField getInputField(){
        return chat.getInputField();
    }
    
    /**
     * Get the ID of the player who is using this Frame.
     */
    public int getID(){
        //TODO Return player ID here
        return 0;
    }
    
    //CONSTUCTOR
    /**Constructs a new game frame
     @param ip the IP address of server*/
    public GameFrame(String ip) {
        super("Riemann's cube");
        this.ip = ip;
    }
    
    //METHODS
    /**Initialises the game frame by creating the panels of the game*/
    public void init() {
    	if (ip == null) { //if an IP address has not been defined get one
    		ip = (String)JOptionPane.showInputDialog(this, "Enter the Host IP", "Select Server",
    		                    JOptionPane.PLAIN_MESSAGE, null, null, null);
    		if (ip == null || ip.equals("")) ip = "localhost";
    	}
    	
    	setSize(900, 600);
        chat = new ChatPanel(this);
        client = new Client(ip, chat); //create a new client with the ip
        ViewPort.high = high;
        ViewPort.free = free;
        ViewPort.noFloor = noFloor;
        ViewPort.showFps = showFps;
        view = new ViewPort(this, 700, 600);
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //hide the cursor by giving it a blank image
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        getContentPane().setCursor(blankCursor);
    }

	/**Runs the game*/
    public void execute() {
    	//add the displays
        getContentPane().add(view, BorderLayout.CENTER);
        getContentPane().add(chat, BorderLayout.WEST);
        //create and start the animators for the displays
        ViewPort.animator = new Animator(view); 
        ViewPort.animator.start();
        ChatPanel.animator = new Animator(chat);
        ChatPanel.animator.start();
        //set the window to be visible
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
