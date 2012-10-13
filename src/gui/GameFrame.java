package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

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
    private JFrame splash; //the splash screen
    private ViewPort view; //the view panel
    private ChatPanel chat; //the chat panel
    
    private UIManager ui = new UIManager(); //UI manager to set background color
    private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); //for setting the font

    
    private String ip; //the IP address of the level
    private String playerName; //Name of the player who ran this frame
    
    //FLAGS
    public static boolean high = true; //is true when high graphics is enable
    public static boolean free = false; //is true when free camera is enabled
    public static boolean noFloor = false; //is true to not render floor
    public static boolean showFps = false; //is true to display fps
    public static boolean waitAtSplash = true;
    public static boolean sound = true;
    
    public ViewPort getViewPort(){
        return view;
    }
    
    /**Gets the TextField so that the view port can request focus on it.
     * @return
     */
    public JTextField getInputField(){
        return chat.getInputField();
    }
    
    /**
     * Get the ID of the player who is using this Frame.
     */
    public int getID(){
    	if (view == null || view.player() == null) return -1;
        return view.player().id;
    }
    
    //CONSTUCTOR
    /**Constructs a new game frame
     @param ip the IP address of server*/
    public GameFrame(String ip) {
        super("Riemann's cube");
        this.ip = ip;
        //set option pane to be dark gray
        ui.put("OptionPane.background", Color.GRAY.darker());
        ui.put("Panel.background", Color.GRAY.darker());
        ui.put("OptionPane.messageForeground", Color.GREEN);
        //set option pane font
        ui.put("OptionPane.messageFont", new Font("xirod", Font.BOLD, 12));
        ui.put("OptionPane.buttonFont", new Font("xirod", Font.ITALIC, 10));
        
        try {
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/xirod.ttf")));

        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
    //Alternate contructor for the host
    /**Constructs a new game frame
     @param ip the IP address of server*/
    public GameFrame(String ip, String name) {
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

    	if(playerName == null){
    	    playerName = (String)JOptionPane.showInputDialog(this, "Your Name", "Enter your name:",
                    JOptionPane.PLAIN_MESSAGE, null, null, null);
    	}
    	
    	//first create and draw the splash screen
    	KeyListener splashListner = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent arg0) {GameFrame.waitAtSplash = false;}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyTyped(KeyEvent arg0) {}
    	};
    	
    	splash = new JFrame();
    	splash.setUndecorated(true);
    	JLabel splashImage = new JLabel(new ImageIcon("resources/gfx/splash.png"));
    	splash.addKeyListener(splashListner);
    	splash.add(splashImage, BorderLayout.CENTER);
    	//get screen and image size
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	Dimension labelSize = splashImage.getPreferredSize();
    	//set the splash to the middle of the screen
    	splash.setLocation(screenSize.width/2 - (labelSize.width/2),
    	                    screenSize.height/2 - (labelSize.height/2));
    	splash.pack();
    	splash.setVisible(true);
    	splash.requestFocus();
    	
    	//set window properties
    	setSize(900, 600);
        
    	//create the viewport
        ViewPort.high = high;
        ViewPort.free = free;
        ViewPort.noFloor = noFloor;
        ViewPort.showFps = showFps;
        ViewPort.sound = sound;
        view = new ViewPort(this, 700, 600);
        
        client = new Client(ip, playerName); //create a new client with the ip
        chat = new ChatPanel(this);
        
        client.setChat(chat);
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        showMouse(false); //hide the mouse
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
        
        while(!view.waitForData() || waitAtSplash);
        
        //set the window to be visible
        setVisible(true);
        
        //remove the splash screen
        splash.setVisible(false);
        splash = null;
    }
    
    /**Removes the splash screen and makes the game window visible*/
    public void begin() {
        
    }
    
    /**Quits the game*/
    public void exit() {
    	WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
    }
    
    /**Shows or hides the mouse in the game window
     * @param show true to show mouse, false to hide it*/
    public void showMouse(boolean show) {
    	if (show) {
            Cursor cursor = Cursor.getDefaultCursor();
            getContentPane().setCursor(cursor);
    	}
    	else { //hide the cursor by giving it a blank image
            BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
            getContentPane().setCursor(blankCursor);
    	}
    }
    
    /**Returns the client
     * @return the client*/
    public Client getClient() {
    	return client;
    }
    
}
