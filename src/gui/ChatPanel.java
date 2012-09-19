package gui;

import java.awt.BorderLayout;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.jogamp.opengl.util.Animator;

/**This is the chat panel that the player can use to communicate with the other players
 * 
 * @author David Saxon 300199370
 */
public class ChatPanel extends GLJPanel implements GLEventListener {
    
    
    //FIELDS
    private static final long serialVersionUID = 1L;
    
    private JFrame frame; //the JFrame containing this panel
    private int width; //the current width of the window
    private double panelScale = 0.22; //the scale of the panel to the whole level
    
    public static Animator animator; // the animator makes sure the chat is always being updated
    
    //CONSTRUCTOR
    /**Constructs a new chat panel
     * @param width the width of the overall JFrame
     * @param height the height of the overall JFrame
     */
    public ChatPanel(JFrame frame) {
        addGLEventListener(this);
        this.frame = frame;
        width = frame.getSize().width;
        setPreferredSize(new java.awt.Dimension((int) (width*panelScale), frame.getSize().height));
        setLayout(new BorderLayout());
        add(new JLabel("Chat:"), BorderLayout.NORTH);
    }
    
    //METHODS
    /**Initialises the chat panel*/
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.90f, 0.90f, 0.90f, 1.0f); // set the transparency colour
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear the screen for the first time
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
    	if (frame.getSize().width != width) { //if the width has changed then scale the chat panel
    		width = frame.getSize().width;
    		setPreferredSize(new java.awt.Dimension((int) (width*panelScale), frame.getSize().height));
    	}
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        //DO NOTHING
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x1, int y1, int x2, int y2) {
    	//DO NOTHING
    }
    
}
