package gui;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import utils.Float2;
import utils.Float3;
import utils.Int2;
import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.cubes.Floor;
import world.cubes.Glass;
import world.cubes.Wall;

import com.jogamp.opengl.util.Animator;

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
    
    private GLCapabilities caps; //the capabilities
    private GameFrame frame;
    private boolean high; //is true if the game is running in high graphics
    private boolean free; //is true when free camera is enabled
    
    private RiemannCube level; //the level
    private Resources resources; //the resources
    
    private Int2 windowDim; //the window dimension
    
    private Float2 mouse = new Float2(0f, 0f); //the current mouse x position
    private Int2 mouseCentre = new Int2(450, 300); //the mouse x centre
    private int keyDown = 0; //an integer representing the current key being pressed
    
    private Float3 pos = new Float3(0.0f, 0.0f, 0.0f); //the position of the camera
    
    private Float2 rotation = new Float2(0.0f, 0.0f); //the x rotation of the camera
    
    private float moveSpeed = 0.04f; //the move speed of the camera
    private float turnSpeed = 4.0f; //the turn speed of the camera
    
    //TODO: a better way that incorporates slicing
    private int floor = 1; //the floor the player is on
    
    //For stepping movement
    private float stepCycle = 0.0f; //where the camera is in the step
    private float stepHeight = 0.015f;

    static GLU glu = new GLU(); //for glu methods
    private Robot robot; //a robot that insures the mouse is always in the centre of the screen
    public static Animator animator; // the animator makes sure the canvas is always being updated
    
    public List<Int3> glassRender; //a list that hold all the glass to render

    // CONSTRUCTOR
    /** Creates a new view port
     * @param frame the window this is enclosed in
     * @param width the width of the view port
     * @param height the height of the view port
     * @param high true to enable high graphics
     * @param free true to enable free camera mode
     * @param level the current level
     */
    public ViewPort(GameFrame frame, int width, int height, boolean high, boolean free, RiemannCube level) {
        addGLEventListener(this);
        this.frame = frame;
        windowDim = new Int2(width, height);
        this.high = high;
        this.free = free;
        this.level = level;
        mouse = new Float2((float) MouseInfo.getPointerInfo().getLocation().getX(),
        				   (float) MouseInfo.getPointerInfo().getLocation().getY());
        addKeyListener(this);
        try {
        	robot = new Robot(); //make a new robot
        } catch (Exception e) {}
        robot.mouseMove(mouseCentre.x, mouseCentre.y); //move the mouse to the centre of the window
    }

    // METHODS
    @Override
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        
        //get the capabilities
        caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));

        gl.glEnable(GL.GL_DEPTH_TEST); // enable depth testing
        gl.glEnable(GL.GL_BLEND); // enable transparency
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); // set the blending function
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
        
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // set the clear colour
        gl.glClearAccum(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(1000.0f); // set the clear depth
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear the screen for the first time

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0f, windowDim.x/windowDim.y, 0.001f, 200.0f);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        //Create the resources
        resources = new Resources(drawable);
        
        gl.glEnable(GL.GL_TEXTURE_2D); //enable 2d textures
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        
        if (keyDown == 27) frame.exit(); //quit the game
        
        //Process movement
        processMovement();
        
        processRotation();
        
        robot.mouseMove(mouseCentre.x, mouseCentre.y); //move the mouse to the centre of the window
        
        //START DRAWING
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); //clear the screen
        
        gl.glLoadIdentity(); //load the identity matrix
        
        gl.glRotatef(rotation.x, 1.0f, 0.0f, 0.0f); //apply the x rotation
        gl.glRotatef(rotation.y, 0.0f, 1.0f, 0.0f); //apply the y rotation
        
        gl.glTranslatef(pos.x-5f, pos.y-3f, pos.z-9f); //apply the translations
        
        if (high) drawSpaceBoxHigh(gl); //draw the space box in high graphics
        //TODO: draw low graphics space
        
        glassRender = new ArrayList<Int3>(); //create the glass render list
        
        //TODO: make this more efficient so that is doesn't always draw the entire cube
        //iterate through the level and draw all the tiles
        for (int x = 0; x < level.width; ++x) {
        	for (int y = 0; y < level.height; ++y) {
        		if (free || y == floor || (y == floor-1) || y == floor+1) { //only draw if this is the current floor
	        		for (int z = 0; z < level.depth; ++z) {
	        			Cube c = level.getCube(x, y, z); //gets the cube from the level
	        			if (high) { //draw the high graphics cubes
		        			if (c instanceof Floor) drawFloorHigh(gl, x*2, y*2, z*2); //draw a floor cube
		        			else if (c instanceof Wall) drawWallHigh(gl, x*2, y*2, z*2); //draw a wall cube
		        			else if (c instanceof Glass) glassRender.add(new Int3(x*2, y*2, z*2));
	        			}
	        			else { //draw the low graphics alternatives
	        				if (c instanceof Floor) drawFloorLow(gl, x*2, y*2, z*2); //draw a floor cube
	        				else if (c instanceof Wall) drawWallLow(gl, x*2, y*2, z*2); //draw a wall cube
	        				else if (c instanceof Glass) glassRender.add(new Int3(x*2, y*2, z*2));
	        			}
	        		}
        		}
        	}
        }
        
        //draw the glass last
        for (Int3 i: glassRender) {
        	if (high) drawGlassHigh(gl, i.x, i.y, i.z);
        	else drawGlassLow(gl, i.x, i.y, i.z);
        }
    }

    /**Process the movement*/
    private void processMovement() {
		boolean move = false; //is true when the player is moving
		switch (keyDown) {
			case 87: //move forward
			   	if (!free) {
			       	pos.x -= moveSpeed*Math.sin(rotation.y*(Math.PI/180.0));
			       	pos.z += moveSpeed*Math.cos(rotation.y*(Math.PI/180.0));
			   	}
			   	if (free) {
			   		pos.y += moveSpeed*Math.sin(rotation.x*(Math.PI/180.0));
			   		pos.x -= moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.sin(rotation.y*(Math.PI/180.0));
			   		pos.z += moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.cos(rotation.y*(Math.PI/180.0));
			   	}
			   	move = true;
			   	break;
			case 83: //move backwards
				if (!free) {
				   	pos.x += moveSpeed*Math.sin(rotation.y*(Math.PI/180.0));
				   	pos.z -= moveSpeed*Math.cos(rotation.y*(Math.PI/180.0));
				}
				if (free) {
					pos.y -= moveSpeed*Math.sin(rotation.x*(Math.PI/180.0));
					pos.x += moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.sin(rotation.y*(Math.PI/180.0));
					pos.z -= moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.cos(rotation.y*(Math.PI/180.0));
				}
				move = true;
				break;
			case 65: //move left
			   	pos.x += moveSpeed*Math.cos(rotation.y*(Math.PI/180.0));
			   	pos.z += moveSpeed*Math.sin(rotation.y*(Math.PI/180.0));
			   	move = true;
			   	break;
		   	case 68: //move right
			   	pos.x -= moveSpeed*Math.cos(rotation.y*(Math.PI/180.0));
			   	pos.z -= moveSpeed*Math.sin(rotation.y*(Math.PI/180.0));
			   	move = true;
		}
		
        //Create the stepping motion
        if (!free) {
	        if (move) {
		        if (stepCycle < Math.PI) stepCycle += 0.14f;
		        else stepCycle = 0.0f;
		        
		        pos.y = (float) (stepHeight*Math.cos(stepCycle+(Math.PI/2)));
	        }
        }
    }
    
    /**Process the rotation*/
    private void processRotation() {
        mouse = new Float2((float) MouseInfo.getPointerInfo().getLocation().getX(),
				   (float) MouseInfo.getPointerInfo().getLocation().getY());
        
        if (mouse.x != mouseCentre.x) { //turn right or left
        	rotation.y += (float) ((mouse.x-mouseCentre.x)/turnSpeed);
        }
        if (mouse.y != mouseCentre.y && rotation.x > -90  && rotation.x < 90) { //turn up or down
        	rotation.x += (float) (((mouse.y-mouseCentre.y)/turnSpeed));
        	//bounce back a little when looking straight up or down
        	if (rotation.x <= -90) rotation.x = -87;
        	else if (rotation.x >= 90) rotation.x = 87;
        }
    }
    
	/**Draws the space box in high graphics
	 * @gl*/
    private void drawSpaceBoxHigh(GL2 gl) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[3]); //bind the space texture
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 7.0f); gl.glVertex3f(-100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(7.0f, 7.0f); gl.glVertex3f(-100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(7.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, 100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, -100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(100.0f, 100.0f, -100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f, -100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, 100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(-100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(100.0f, -100.0f, 100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(-100.0f, -100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, -100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(100.0f, -100.0f, -100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f, 100.0f);
        gl.glEnd();
    }
    
    /**Draws a floor cube in high graphics
     * @param gl
     * @param x the x position
     * @param y the y position
     * @param z the z position*/
    private void drawFloorHigh(GL2 gl, int x, int y, int z) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
    	gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x,   y, z);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x,   y, z+2);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y, z+2);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y, z);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x,   y+2, z);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x+2,   y+2, z);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y+2, z+2);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x, y+2, z+2);
        gl.glEnd();
    }
    
    /**Draw a wall cube in high graphics
     * @param gl
     * @param x the x position
     * @param y the y position
     * @param z the z position
     */
    private void drawWallHigh(GL2 gl, int x, int y, int z) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[1]); //bind the wall tile texture
		//draw front part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y+2,   z);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x, y, z+2);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x, y+2,   z+2);
		gl.glEnd();
		//draw back part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x+2, y+2, z+2);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x+2, y, z+2);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y, z);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y+2,   z);
		gl.glEnd();
		//draw left part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y,   z);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y+2, z);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y+2,   z);
		gl.glEnd();
		//draw right part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y+2,   z+2);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y, z+2);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y, z+2);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y+2,   z+2);
		gl.glEnd();
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
    	gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x,   y, z);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x+2,   y, z);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y, z+2);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x, y, z+2);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x,   y+2, z);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x,   y+2, z+2);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y+2, z+2);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y+2, z);
        gl.glEnd();
    }
    
    /**Draw a glass cube in high graphics*/
    private void drawGlassHigh(GL2 gl, int x, int y, int z) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[2]); //bind the glass tile texture
		//draw front part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y+2,   z);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x, y, z+2);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x, y+2,   z+2);
		gl.glEnd();
		//draw back part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x+2, y+2, z+2);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x+2, y, z+2);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y, z);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y+2,   z);
		gl.glEnd();
		//draw left part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y,   z);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y+2, z);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y+2,   z);
		gl.glEnd();
		//draw right part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y+2,   z+2);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y, z+2);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y, z+2);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y+2,   z+2);
		gl.glEnd();
    }
    
    /**Draws a floor cube in low graphics
     * @param gl
     * @param x the x position
     * @param y the y position
     * @param z the z position*/
    private void drawFloorLow(GL2 gl, int x, int y, int z) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
    	gl.glBegin(GL2.GL_QUADS);
    	gl.glVertex3f(x,   y, z);
    	gl.glVertex3f(x,   y, z+2);
    	gl.glColor4f(0.0f, 0.5f, 1.0f, 1.0f);
    	gl.glVertex3f(x+2, y, z+2);
    	gl.glVertex3f(x+2, y, z);
        gl.glEnd();
        gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(x,   y+2, z);
        gl.glVertex3f(x+2,   y+2, z);
        gl.glColor4f(0.0f, 0.5f, 1.0f, 1.0f);
        gl.glVertex3f(x+2, y+2, z+2);
        gl.glVertex3f(x, y+2, z+2);
        gl.glEnd();
    }
    
    /**Draw a wall cube in low graphics
     * @param gl
     * @param x the x position
     * @param y the y position
     * @param z the z position
     */
    private void drawWallLow(GL2 gl, int x, int y, int z) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
		gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(x, y+2,   z);
		gl.glVertex3f(x, y, z);
		gl.glColor4f(1.0f, 0.0f, 0.5f, 1.0f);
		gl.glVertex3f(x, y, z+2);
		gl.glVertex3f(x, y+2,   z+2);
		gl.glEnd();
		//draw back part
		gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(x+2, y+2, z+2);
		gl.glVertex3f(x+2, y, z+2);
		gl.glColor4f(1.0f, 0.0f, 0.5f, 1.0f);
		gl.glVertex3f(x+2, y, z);
		gl.glVertex3f(x+2, y+2,   z);
		gl.glEnd();
		//draw left part
		gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(x+2, y,   z);
		gl.glVertex3f(x, y, z);
		gl.glColor4f(1.0f, 0.0f, 0.5f, 1.0f);
		gl.glVertex3f(x, y+2, z);
		gl.glVertex3f(x+2, y+2,   z);
		gl.glEnd();
		//draw right part
		gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(x, y+2,   z+2);
		gl.glVertex3f(x, y, z+2);
		gl.glColor4f(1.0f, 0.0f, 0.5f, 1.0f);
		gl.glVertex3f(x+2, y, z+2);
		gl.glVertex3f(x+2, y+2,   z+2);
		gl.glEnd();
		//draw the floor
		gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
    	gl.glBegin(GL2.GL_QUADS);
    	gl.glVertex3f(x,   y+2, z);
    	gl.glVertex3f(x,   y+2, z+2);
    	gl.glColor4f(0.0f, 0.5f, 1.0f, 1.0f);
    	gl.glVertex3f(x+2, y+2, z+2);
    	gl.glVertex3f(x+2, y+2, z);
        gl.glEnd();
        gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(x,   y, z);
        gl.glVertex3f(x+2,   y, z);
        gl.glColor4f(0.0f, 0.5f, 1.0f, 1.0f);
        gl.glVertex3f(x+2, y, z+2);
        gl.glVertex3f(x, y, z+2);
        gl.glEnd();
    }
    
    /**Draw a glass cube in low graphics*/
    private void drawGlassLow(GL2 gl, int x, int y, int z) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	gl.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);
		//draw front part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y+2,   z);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x, y, z+2);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x, y+2,   z+2);
		gl.glEnd();
		//draw back part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x+2, y+2, z+2);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x+2, y, z+2);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y, z);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y+2,   z);
		gl.glEnd();
		//draw left part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y,   z);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y, z);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y+2, z);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y+2,   z);
		gl.glEnd();
		//draw right part
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(x, y+2,   z+2);
		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(x, y, z+2);
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(x+2, y, z+2);
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(x+2, y+2,   z+2);
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