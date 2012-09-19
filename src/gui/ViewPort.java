package gui;



import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.newt.event.InputEvent;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.Texture;

/**
 * This is the pane that displays all player's view of the game
 * The view port is essentially the player's camera
 * View port handles all input because it is always updating so
 * always has the input. The view port also passes the needed 
 * events to the chat box.
 * 
 * @author David Saxon 300199370
 */
public class ViewPort extends GLCanvas implements GLEventListener, KeyListener{

    
    // FIELDS
    private static final long serialVersionUID = 1L;
    
    private int width; // the width of the window
    private int height; // the height of the window
    
    private int keyDown = 0; //an integer representing the current key being pressed
    //TODO:Change these to a vector
    private double mouseX = 0; //the current mouse x position
    private double mouseY = 0; //the current mouse y position
    private int mouseXCentre = 450; //the mouse x centre
    private int mouseYCentre = 300; //the mouse y centre
    
    private float xPos = 0.0f; //the x position of the camera
    private float yPos = 0.0f; //the y position of the camera
    private float zPos = 0.5f; //the z position of the camera
    
    private float yRotation = 0.0f; //the y rotation of the camera
    private float zRotation = 0.0f; //the z rotation of the camera
    
    private float moveSpeed = 0.04f; //the move speed of the camera
    private float turnSpeed = 4.0f; //the turn speed of the camera
    
    //For stepping movement
    private float stepCycle = 0.0f; //where the camera is in the step
    private float stepHeight = 0.03f;

    static GLU glu = new GLU(); //for glu methods
    private Robot robot; //a robot that insures the mouse is always in the centre of the screen
    public static Animator animator; // the animator makes sure the canvas is always being updated
    
    //TODO: move these into a resources class
    Texture floorTexture;

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
        mouseX = MouseInfo.getPointerInfo().getLocation().getX();
        mouseY = MouseInfo.getPointerInfo().getLocation().getY();
        addKeyListener(this);
        try {
        	robot = new Robot(); //make a new robot
        } catch (Exception e) {}
        robot.mouseMove(mouseXCentre, mouseYCentre); //move the mouse to the centre of the window
        requestFocus();
        
    }

    // METHODS
    @Override
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

        glu.gluPerspective(45.0f, width/height, 0.001f, 100.0f);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        //TODO: move this into resouces aswell
        
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        //Process movement
        boolean move = false; //is true when the player is moving
        switch (keyDown) {
        	case 27: //exit the game
        		System.exit(0);
        		break;
	        case 38: //move forward
	        	zPos += moveSpeed*Math.cos(zRotation*(Math.PI/180.0));
	        	xPos -= moveSpeed*Math.sin(zRotation*(Math.PI/180.0));
	        	move = true;
	        	break;
	        case 40: //move backwards
	        	zPos -= moveSpeed*Math.cos(zRotation*(Math.PI/180.0));
	        	xPos += moveSpeed*Math.sin(zRotation*(Math.PI/180.0));
	        	move = true;
	        	break;
	        case 37: //move left
	        	xPos += moveSpeed*Math.cos(zRotation*(Math.PI/180.0));
	        	zPos += moveSpeed*Math.sin(zRotation*(Math.PI/180.0));
	        	move = true;
	        	break;
	        case 39: //move right
	        	xPos -= moveSpeed*Math.cos(zRotation*(Math.PI/180.0));
	        	zPos -= moveSpeed*Math.sin(zRotation*(Math.PI/180.0));
	        	move = true;
        }
        
        //Process turning
        mouseX = MouseInfo.getPointerInfo().getLocation().getX();
        mouseY = MouseInfo.getPointerInfo().getLocation().getY();
        
        if (mouseX != mouseXCentre) { //turn right or left
        	zRotation += (float) ((mouseX-mouseXCentre)/turnSpeed);
        }
        if (mouseY != mouseYCentre && yRotation > -90  && yRotation < 90) { //turn up or down
        	yRotation += (float) (((mouseY-mouseYCentre)/turnSpeed));
        	//bounce back a little when looking straight up or down
        	if (yRotation <= -90) yRotation = -87;
        	else if (yRotation >= 90) yRotation = 87;
        }
        
        robot.mouseMove(mouseXCentre, 300); //move the mouse to the centre of the window
        
        //Create the stepping motion
        if (move) {
	        if (stepCycle < Math.PI) stepCycle += 0.14f;
	        else stepCycle = 0.0f;
	        
	        yPos = (float) (stepHeight*Math.cos(stepCycle+(Math.PI/2)));
        }
        
        gl.glLoadIdentity();
        gl.glRotatef(yRotation, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(zRotation, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(xPos, yPos, zPos);
        
        //FOR TESTING
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

	@Override
	public void keyPressed(KeyEvent e) {
		keyDown = e.getKeyCode();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == keyDown) keyDown = 0;
	}

	public void keyTyped(KeyEvent e) {}
    public void dispose(GLAutoDrawable drawable) {}
    public void reshape(GLAutoDrawable drawable, int x1, int y1, int x2, int y2) {}
}