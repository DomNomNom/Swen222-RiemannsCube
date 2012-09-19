package gui;

import java.awt.BorderLayout;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JLabel;

/**This is the chat panel that the player can use to communicate with the other players
 * 
 * @author David Saxon 300199370
 */
public class ChatPanel extends GLJPanel implements GLEventListener{
    
    
    //FIELDS
    private static final long serialVersionUID = 1L;
    
    //CONSTRUCTOR
    /**Constructs a new chat panel
     * @param width the width of the panel
     * @param height the height of the panel
     */
    public ChatPanel() {
        addGLEventListener(this);
        setLayout(new BorderLayout());
        add(new JLabel("Chat:                                      "), BorderLayout.NORTH);
    }
    
    //METHODS
    /**Initialises the chat panel*/
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.90f, 0.90f, 0.90f, 1.0f); // set the transparency colour
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear the screen for the first time
    }

    @Override
    public void display(GLAutoDrawable arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose(GLAutoDrawable arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
            int arg4) {
        // TODO Auto-generated method stub
        
    }
    
}
