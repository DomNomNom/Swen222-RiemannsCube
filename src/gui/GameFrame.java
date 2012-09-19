package gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import com.jogamp.opengl.util.Animator;

/**Game Frame is the overall window of the game
 * 
 * @author David Saxon 300199370
 */
public class GameFrame extends JFrame{
    
    //FIELDS
    private static final long serialVersionUID = 1L;
    
    private ViewPort view; //the view panel
    private ChatPanel chat; //the chat panel
    
    //CONSTUCTOR
    /**Constructs a new game frame*/
    public GameFrame() {
        super("Riemann's cube");
    }
    
    //METHODS
    /**Initialises the game frame by creating the panels of the game*/
    public void init() {
    	setSize(900, 600);
        chat = new ChatPanel(this);
        view = new ViewPort(700, 600); 
        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //hide the curosor by giving it a blank image
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
    	System.exit(0);
    }
}
