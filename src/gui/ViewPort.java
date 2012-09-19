package gui;



import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;

/**
 * This is the pane that displays all player's view of the game
 * 
 * @author David Saxon 300199370
 */
public class ViewPort extends GLCanvas implements GLEventListener {

    
    // FIELDS
    private static final long serialVersionUID = 1L;
    
    private int width; // the width of the window
    private int height; // the height of the window
    
    //for testing
    private float zMove = 0.5f;
    private float alpha = 0.0f;
    private boolean move = true;
    private boolean moveBack = false;
    private boolean turn = false;
    private boolean turnBack = false;

    static GLU glu = new GLU(); //for glu methods
    public static Animator animator; // the animator makes sure the canvas is always being updated

    // CONSTRUCTOR
    /** Creates a new view port
     * @param width the width of the view port
     * @param height the height of the view port
     * @param chat a reference to the chat panel
     */
    public ViewPort(int width, int height) {
        addGLEventListener(this);
        this.width = width;
        this.height = height;
    }

    // METHODS
    /**Initialises the view port
     * @param drawable
     */
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL.GL_DEPTH_TEST); // enable depth testing
        gl.glEnable(GL.GL_BLEND); // enable transparency
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); // set the blending function

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // set the transparency colour
        gl.glClearDepth(100.0f); // set the clear depth
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear the screen for the first time

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(75.0f, width/height, 0.001f, 100.0f);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**Updates the view port
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (turn) { // turn left
            if (!turnBack) {
                if (alpha < 180.0)
                    alpha += 1.0;
                else {
                    turn = false;
                    turnBack = true;
                    move = true;
                }
            } else {
                if (alpha < 360.0)
                    alpha += 1.0;
                else {
                    alpha = 0.0f;
                    turn = false;
                    turnBack = false;
                    move = true;
                }
            }
        }
        if (move) {
            if (!moveBack) {
                if (zMove < 4.0)
                    zMove += 0.03;
                else {
                    move = false;
                    turn = true;
                    moveBack = true;
                }
            } else {
                if (zMove > 0.5)
                    zMove -= 0.03;
                else {
                    move = false;
                    turn = true;
                    moveBack = false;
                }
            }
        }
        
        gl.glLoadIdentity();
        gl.glRotatef(alpha, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(0.0f, 0.0f, zMove);
        // Draw all the tiles
        // floor tiles
        for (int i = -8; i < 1; ++i) {
            for (int j = -6; j < 6; ++j) {
                gl.glBegin(GL2.GL_QUADS);
                gl.glColor4f(0.75f, 0.0f, 0.0f, 1.0f);
                gl.glVertex3f(j, -1.0f, i);
                gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                gl.glVertex3f(j, -1.0f, i + 1);
                gl.glColor4f(0.25f, 0.0f, 0.0f, 1.0f);
                gl.glVertex3f(j + 1, -1.0f, i + 1);
                gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
                gl.glVertex3f(j + 1, -1.0f, i);
                gl.glEnd();
            }
        }
        // roof tiles
        for (int i = -8; i < 1; ++i) {
            for (int j = -6; j < 6; ++j) {
                gl.glBegin(GL2.GL_QUADS);
                gl.glColor4f(0.25f, 0.25f, 0.25f, 1.0f);
                gl.glVertex3f(j, 1.0f, i);
                gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
                gl.glVertex3f(j, 1.0f, i + 1);
                gl.glColor4f(0.75f, 0.75f, 0.75f, 1.0f);
                gl.glVertex3f(j + 1, 1.0f, i + 1);
                gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
                gl.glVertex3f(j + 1, 1.0f, i);
                gl.glEnd();
            }
        }
        // side wall tiles
        for (int i = -8; i < 1; ++i) {
            gl.glBegin(GL2.GL_QUADS);
            gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
            gl.glVertex3f(-6, 0.0f, i);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(-6, -1.0f, i);
            gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
            gl.glVertex3f(-6, -1.0f, i + 1);
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(-6, 0.0f, i + 1);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
            gl.glVertex3f(-6, 1.0f, i);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(-6, 0.0f, i);
            gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
            gl.glVertex3f(-6, 0.0f, i + 1);
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(-6, 1.0f, i + 1);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
            gl.glVertex3f(6, 0.0f, i);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(6, -1.0f, i);
            gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
            gl.glVertex3f(6, -1.0f, i + 1);
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(6, 0.0f, i + 1);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
            gl.glVertex3f(6, 1.0f, i);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(6, 0.0f, i);
            gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
            gl.glVertex3f(6, 0.0f, i + 1);
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(6, 1.0f, i + 1);
            gl.glEnd();
        }
        // front and back wall tiles
        for (int i = -6; i < 6; ++i) {
            gl.glBegin(GL2.GL_QUADS);
            gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
            gl.glVertex3f(i, 0.0f, -8);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(i, -1.0f, -8);
            gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
            gl.glVertex3f(i + 1, -1.0f, -8);
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(i + 1, 0.0f, -8);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
            gl.glVertex3f(i, 1.0f, -8);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(i, 0.0f, -8);
            gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
            gl.glVertex3f(i + 1, 0.0f, -8);
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(i + 1, 1.0f, -8);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
            gl.glVertex3f(i, 0.0f, 1);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(i, -1.0f, 1);
            gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
            gl.glVertex3f(i + 1, -1.0f, 1);
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(i + 1, 0.0f, 1);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
            gl.glVertex3f(i, 1.0f, 1);
            gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(i, 0.0f, 1);
            gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
            gl.glVertex3f(i + 1, 0.0f, 1);
            gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
            gl.glVertex3f(i + 1, 1.0f, 1);
            gl.glEnd();
        }
        // create middle wall
        for (int i = -6; i < 6; ++i) {
            if (i != -1 && i != 0) {
                gl.glBegin(GL2.GL_QUADS);
                gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
                gl.glVertex3f(i, 0.0f, -6);
                gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                gl.glVertex3f(i, -1.0f, -6);
                gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
                gl.glVertex3f(i + 1, -1.0f, -6);
                gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
                gl.glVertex3f(i + 1, 0.0f, -6);
                gl.glEnd();
                gl.glBegin(GL2.GL_QUADS);
                gl.glColor4f(0.0f, 0.25f, 0.0f, 1.0f);
                gl.glVertex3f(i, 1.0f, -6);
                gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
                gl.glVertex3f(i, 0.0f, -6);
                gl.glColor4f(0.0f, 0.75f, 0.0f, 1.0f);
                gl.glVertex3f(i + 1, 0.0f, -6);
                gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
                gl.glVertex3f(i + 1, 1.0f, -6);
                gl.glEnd();
            }
        }
        // now create glass
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(0.5f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(-1, 0.0f, -6);
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(-1, -1.0f, -6);
        gl.glColor4f(0.5f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(0, -1.0f, -6);
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(0, 0.0f, -6);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(0.5f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(-1, 1.0f, -6);
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(-1, 0.0f, -6);
        gl.glColor4f(0.5f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(0, 0.0f, -6);
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(0, 1.0f, -6);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(0.5f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(0, 0.0f, -6);
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(0, -1.0f, -6);
        gl.glColor4f(0.5f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(1, -1.0f, -6);
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(1, 0.0f, -6);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor4f(0.5f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(0, 1.0f, -6);
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(0, 0.0f, -6);
        gl.glColor4f(0.5f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(1, 0.0f, -6);
        gl.glColor4f(0.0f, 1.0f, 1.0f, 0.5f);
        gl.glVertex3f(1, 1.0f, -6);
        gl.glEnd();
        
    }

    /**When the view port has been closed
     * @param drawable
     */
    public void dispose(GLAutoDrawable drawable) {
        // TODO Auto-generated method stu
    }

    /**When the view port has been resized
     * @drawable
     */
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
        // TODO Auto-generated method stub
    }
}