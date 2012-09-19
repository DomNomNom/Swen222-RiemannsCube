package gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;

import com.jogamp.opengl.util.Animator;

/**Game Frame is the overall window of the game
 * 
 * @author David Saxon 300199370
 */
public class GameFrame extends JFrame {
    
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
        chat = new ChatPanel();
        view = new ViewPort(700, 600);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**Runs the game*/
    public void execute() {
        getContentPane().add(view, BorderLayout.CENTER);
        getContentPane().add(chat, BorderLayout.WEST);
        ViewPort.animator = new Animator(view); 
        ViewPort.animator.start();
        setVisible(true);
    }
    
}
