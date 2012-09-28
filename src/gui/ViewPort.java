package gui;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import utils.Float2;
import utils.Float3;
import utils.Int2;
import utils.Pair;
import world.RiemannCube;
import world.cubes.Cube;
import world.cubes.Floor;
import world.cubes.Glass;
import world.cubes.Wall;

import com.jogamp.opengl.util.Animator;

import data.XMLParser;

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
    
    private GameFrame frame;
    public static boolean high = true; //is true if the game is running in high graphics
    public static boolean free = false; //is true when free camera is enabled
    public static boolean noFloor = false; //is true when the floor should not show
    public static boolean showFps = false; //is true to display fps
    
    //fps management
    private long currentTime = System.currentTimeMillis(); //get the current time
    private int accumTime = 0; //the amount of time accumulated since the last frame
    private final int frameLength = 17; //the time of a frame
    
    private RiemannCube level; //the level
    private Resources resources; //the resources
    
    private Int2 windowDim; //the window dimension
    
    private Float2 mouse = new Float2(0f, 0f); //the current mouse x position
    private Int2 mouseCentre = new Int2(450, 300); //the mouse x centre
    
    //keys
    private int forBack = 0; //0: none, 1: forwards, 2: backwards
    private int leftRight = 0; //0: none, 1: left, 2: right
    private boolean shift = false; //is true if shift is pressed
    private boolean space = false; //is true if space is pressed
    private boolean ctrl = false; //is true if crtl is pressed
    private boolean exit = false; //is true when to exit
    
    private Float3 pos = new Float3(0.0f, 0.0f, 0.0f); //the position of the camera
    
    private Float2 rotation = new Float2(0.0f, 0.0f); //the x rotation of the camera
    
    private Float3 normal = new Float3(0.0f, 1.0f, 0.0f); //the normal of the player
    
    private float moveSpeed = 0.04f; //the move speed of the camera
    private float turnSpeed = 4.0f; //the turn speed of the camera
    
    //TODO: a better way that incorporates slicing
    private int floor = 1; //the floor the player is on
    
    //For stepping movement
    private float stepCycle = 0.0f; //where the camera is in the step
    private float stepHeight = 0.015f;

    static GLU glu = new GLU(); //for GLU methods
    private Robot robot; //a robot that insures the mouse is always in the centre of the screen
    public static Animator animator; // the animator makes sure the canvas is always being updated
    
    public List<Pair<Float3, Float3>> glassRender; //a list that hold all the glass to render

    // CONSTRUCTOR
    /** Creates a new view port
     * @param frame the window this is enclosed in
     * @param width the width of the view port
     * @param height the height of the view port
     */
    public ViewPort(GameFrame frame, int width, int height) {
        addGLEventListener(this);
        this.frame = frame;
        windowDim = new Int2(width, height);
        
        
        try {
			this.level = XMLParser.readXML(new File("Levels/Test.xml"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        
        
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
        final GL2 gl = drawable.getGL().getGL2();;

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
        
        currentTime = System.currentTimeMillis(); //update the time before starting
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        if (exit) frame.exit(); //quit the game
        
        
        //check if a frame has passed and if so update the events
        
        
        long newTime = System.currentTimeMillis(); //get the time at this point
        int frameTime = (int) (newTime-currentTime); //find the length of this frame
        
        if (showFps) printFps(frameTime);
        
        if (frameTime > 25) frameTime = 30; //limit the max frame time
        currentTime = newTime; //update the current time
        accumTime += frameTime; //Accumulate the frame time
        
        if (accumTime >= frameLength) { //a frame has passed
			//Process movement and rotation
		    processMovement();
		    processRotation();
		    
		    robot.mouseMove(mouseCentre.x, mouseCentre.y); //move the mouse to the centre of the window
		    
		    accumTime -= frameLength;
        }
        
        //START DRAWING
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); //clear the screen
        
        gl.glLoadIdentity(); //load the identity matrix
        
        gl.glRotatef(rotation.x, 1.0f, 0.0f, 0.0f); //apply the x rotation
        gl.glRotatef(rotation.y, 0.0f, 1.0f, 0.0f); //apply the y rotation
        
        gl.glTranslatef(pos.x-2f, pos.y-2*floor, pos.z-2f); //apply the translations
        
        if (high) drawSpaceBoxHigh(gl); //draw the space box in high graphics
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
        
        glassRender = new ArrayList<Pair<Float3, Float3>>(); //create the glass render list
        
        //iterate through the level and draw all the tiles
        for (int x = 0; x < level.size.x; ++x) {
        	for (int y = 0; y < level.size.y; ++y) {
        		if (free || y == floor || (y == floor-1) || y == floor+1) { //only draw if this is the current floor
	        		for (int z = 0; z < level.size.z; ++z) {
	        			Cube c = level.getCube(x, y, z); //get the current cube
	        			Float3 v = new Float3(x*2, y*2, z*2); //find the position vector
	        			Float3 n = new Float3(x*2+normal.x, y*2+normal.y, z*2+normal.z); //find the normal
	        			
	        			if (high) { //draw the high graphics cubes
	        				if (c instanceof Wall) drawWallHigh(gl, v, n); //draw a wall
	        				if (c instanceof Floor) drawFloorHigh(gl, v, n); //draw a floor
	        			}
	        			else { //draw the low graphics alternatives
	        				//if (c instanceof Floor) drawFloorLow(gl, x*2, y*2, z*2); //draw a floor cube
	        				//else if (c instanceof Wall) drawWallLow(gl, x*2, y*2, z*2); //draw a wall cube
	        			}
	        			
	        			if (c instanceof Glass) glassRender.add(new Pair<Float3, Float3>(v, n));
	        		}
        		}
        	}
        }
        
        //draw the glass last
        for (Pair<Float3, Float3> p: glassRender) {
        	if (high) drawGlassHigh(gl, p.first(), p.second());
        	//else drawGlassLow(gl, i.x, i.y, i.z);
        }
        
        //draw the glass around the outside of the cube
        if (high) drawOuterGlassHigh(gl);
    }

    /**Process the movement*/
    private void processMovement() {
		boolean move = false; //is true when the player is moving
		float straff = 1.0f; //straffing multiple
		if (forBack != 0 && leftRight != 0) straff = 0.5f;
		
		if (forBack == 1) { //move forward
			if (!free) {
		       	pos.x -= moveSpeed*Math.sin(rotation.y*(Math.PI/180.0))*straff;
		       	pos.z += moveSpeed*Math.cos(rotation.y*(Math.PI/180.0))*straff;
		   	}
		   	if (free) {
		   		pos.y += moveSpeed*Math.sin(rotation.x*(Math.PI/180.0));
		   		pos.x -= moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.sin(rotation.y*(Math.PI/180.0))*straff;
		   		pos.z += moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.cos(rotation.y*(Math.PI/180.0))*straff;
		   	}
		   	move = true;
		}
		else if (forBack == 2) { //move backwards
			if (!free) {
			   	pos.x += moveSpeed*Math.sin(rotation.y*(Math.PI/180.0))*straff;
			   	pos.z -= moveSpeed*Math.cos(rotation.y*(Math.PI/180.0))*straff;
			}
			if (free) {
				pos.y -= moveSpeed*Math.sin(rotation.x*(Math.PI/180.0));
				pos.x += moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.sin(rotation.y*(Math.PI/180.0))*straff;
				pos.z -= moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.cos(rotation.y*(Math.PI/180.0))*straff;
			}
			move = true;
		}
		
		if (leftRight == 1) { //move left
			pos.x += moveSpeed*Math.cos(rotation.y*(Math.PI/180.0))*straff;
		   	pos.z += moveSpeed*Math.sin(rotation.y*(Math.PI/180.0))*straff;
		   	move = true;
		}
		else if (leftRight == 2) { //move right
			pos.x -= moveSpeed*Math.cos(rotation.y*(Math.PI/180.0))*straff;
		   	pos.z -= moveSpeed*Math.sin(rotation.y*(Math.PI/180.0))*straff;
		}
		
        //Create the stepping motion
        if (!free) {
	        if (move) {
		        if (stepCycle < Math.PI) stepCycle += 0.14f;
		        else stepCycle = 0.0f;
		        
		        pos.y = (float) (stepHeight*Math.cos(stepCycle+(Math.PI/2)));
	        }
        }
        else { //in free camera mode
        	if (shift) moveSpeed = 0.08f; //move at double speed if shift
        	else moveSpeed = 0.04f;
        	if (space) pos.y -= moveSpeed; //move straight up
        	else if (ctrl) pos.y += moveSpeed; //move straight down
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
    
    /**Draws a openGL textured quad
     * @param gl
     * @param v the position vector of the cube
     * @param n the normal vector of the quad*/
    private void drawQuadTex(GL2 gl, Float3 v, Float3 n, boolean inside) {
    	//find the difference between the point and the normal
    	Float3 dv = new Float3((n.x-v.x), (n.y-v.y), (n.z-v.z));
    	
    	//Find the rotation amounts
    	float xRot = Math.abs(dv.y)*(90.0f+dv.y*90.0f);
    	float yRot = Math.abs(dv.z)*(dv.z*90.0f);
    	float zRot = Math.abs(dv.x)*(dv.x*90.0f);

    	gl.glPushMatrix(); //push new matrix
    	
    	gl.glTranslatef(v.x, v.y, v.z); //translate world to position
    	
    	//apply the rotations
    	gl.glRotatef(zRot, 0.0f, 0.0f, 1.0f);
    	gl.glRotatef(yRot, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(xRot, 1.0f, 0.0f, 0.0f);
    	
    	if (!inside) gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f); //flip if outside
    	
    	//translate to the edge of the cube
    	if (inside) gl.glTranslatef(0, -1, 0);
    	else gl.glTranslatef(0, 1, 0);
    	
    	//now draw the quad
    	gl.glBegin(GL2.GL_QUADS);
    	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1, 0, -1);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1, 0,  1);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1, 0,  1);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1, 0, -1);
        gl.glEnd();
        
        gl.glPopMatrix(); //pop the matrix
    }
    
    /**Draw a floor cube in high graphics
     * @param gl
     * @param v the position vector of the cube
     * @param n the normal vector of the cube*/
    private void drawFloorHigh(GL2 gl, Float3 v, Float3 n) {
    	if (!noFloor) {
	    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
	    	drawQuadTex(gl, v, new Float3(v.x, v.y-1, v.z), true);
			drawQuadTex(gl, v, new Float3(v.x, v.y+1, v.z), true);
    	}
    }
    
    /**Draw a wall cube in high graphics
     * @param gl
     * @param v the position vector of the cube
     * @param n the normal vector of the cube*/
    private void drawWallHigh(GL2 gl, Float3 v, Float3 n) {
    	//draw the 4 walls
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[1]); //bind the wall tile texture
		drawQuadTex(gl, v, new Float3(v.x-1, v.y, v.z), false);
		drawQuadTex(gl, v, new Float3(v.x+1, v.y, v.z), false);
		drawQuadTex(gl, v, new Float3(v.x, v.y, v.z+1), false);
		drawQuadTex(gl, v, new Float3(v.x, v.y, v.z-1), false);
		//draw the floor
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
		drawQuadTex(gl, v, new Float3(v.x, v.y-1, v.z), false);
		drawQuadTex(gl, v, new Float3(v.x, v.y+1, v.z), false);
    }
    
    /**Draw a glass cube in high graphics
     * @param gl
     * @param v the position vector of the cube
     * @param n the normal vector of the cube*/
    private void drawGlassHigh(GL2 gl, Float3 v, Float3 n) {
    	//draw the floor
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
		drawQuadTex(gl, v, new Float3(v.x, v.y-1, v.z), true);
		drawQuadTex(gl, v, new Float3(v.x, v.y+1, v.z), true);
		drawQuadTex(gl, v, new Float3(v.x, v.y-1, v.z), false);
		drawQuadTex(gl, v, new Float3(v.x, v.y+1, v.z), false);
    	//draw the 4 walls
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[2]); //bind the glass texture
		drawQuadTex(gl, v, new Float3(v.x-1, v.y, v.z), true);
		drawQuadTex(gl, v, new Float3(v.x+1, v.y, v.z), true);
		drawQuadTex(gl, v, new Float3(v.x, v.y, v.z+1), true);
		drawQuadTex(gl, v, new Float3(v.x, v.y, v.z-1), true);
		drawQuadTex(gl, v, new Float3(v.x-1, v.y, v.z), false);
		drawQuadTex(gl, v, new Float3(v.x+1, v.y, v.z), false);
		drawQuadTex(gl, v, new Float3(v.x, v.y, v.z+1), false);
		drawQuadTex(gl, v, new Float3(v.x, v.y, v.z-1), false);	
    }
    
    /**Draws an outer box of glass around the level in high graphics
     * @param gl*/
    private void drawOuterGlassHigh(GL2 gl) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[2]); //bind the glass texture
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,         0.0f       ); gl.glVertex3f(-1.0f, level.size.y*2-1, -1.0f         );
        gl.glTexCoord2f(0.0f,         level.size.z); gl.glVertex3f(-1.0f, level.size.y*2-1, level.size.z*2-1);
        gl.glTexCoord2f(level.size.y, level.size.z); gl.glVertex3f(-1.0f, -1.0f,            level.size.z*2-1);
        gl.glTexCoord2f(level.size.y, 0.0f       ); gl.glVertex3f(-1.0f, -1.0f,            -1.0f         );
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,         0.0f       ); gl.glVertex3f(level.size.x*2-1, level.size.y*2-1, -1.0f          );
        gl.glTexCoord2f(0.0f,         level.size.z); gl.glVertex3f(level.size.x*2-1, -1.0f,            -1.0f          );
        gl.glTexCoord2f(level.size.y, level.size.z); gl.glVertex3f(level.size.x*2-1, -1.0f,            level.size.z*2-1);
        gl.glTexCoord2f(level.size.y, 0.0f       ); gl.glVertex3f(level.size.x*2-1, level.size.y*2-1, level.size.z*2-1);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,        0.0f        ); gl.glVertex3f(-1.0f,           level.size.y*2-1, level.size.z*2-1);
        gl.glTexCoord2f(0.0f,        level.size.y); gl.glVertex3f(level.size.x*2-1, level.size.y*2-1, level.size.z*2-1);
        gl.glTexCoord2f(level.size.x, level.size.y); gl.glVertex3f(level.size.x*2-1, -1.0f,            level.size.z*2-1);
        gl.glTexCoord2f(level.size.x, 0.0f        ); gl.glVertex3f(-1.0f,           -1.0f,            level.size.z*2-1);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,        0.0f        ); gl.glVertex3f(-1.0f,           level.size.y*2-1, -1.0f);
        gl.glTexCoord2f(0.0f,        level.size.y); gl.glVertex3f(-1.0f,           -1.0f,            -1.0f);
        gl.glTexCoord2f(level.size.x, level.size.y); gl.glVertex3f(level.size.x*2-1, -1.0f,            -1.0f);
        gl.glTexCoord2f(level.size.x, 0.0f        ); gl.glVertex3f(level.size.x*2-1, level.size.y*2-1, -1.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,        0.0f       ); gl.glVertex3f(-1.0f,           level.size.y*2-1, level.size.z*2-1);
        gl.glTexCoord2f(0.0f,        level.size.z); gl.glVertex3f(-1.0f,           level.size.y*2-1, -1.0f          );
        gl.glTexCoord2f(level.size.x, level.size.z); gl.glVertex3f(level.size.x*2-1, level.size.y*2-1, -1.0f          );
        gl.glTexCoord2f(level.size.x, 0.0f       ); gl.glVertex3f(level.size.x*2-1, level.size.y*2-1, level.size.z*2-1);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,        0.0f       ); gl.glVertex3f(level.size.x*2-1, -1.0f, -1.0f          );
        gl.glTexCoord2f(0.0f,        level.size.z); gl.glVertex3f(-1.0f,           -1.0f, -1.0f          );
        gl.glTexCoord2f(level.size.x, level.size.z); gl.glVertex3f(-1.0f,           -1.0f, level.size.z*2-1);
        gl.glTexCoord2f(level.size.x, 0.0f       ); gl.glVertex3f(level.size.x*2-1, -1.0f, level.size.z*2-1);
        gl.glEnd();
    }
    
	/**Draws the space box in high graphics
	 * @gl*/
    private void drawSpaceBoxHigh(GL2 gl) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[3]); //bind the space texture
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 7.0f); gl.glVertex3f(-100.0f,  100.0f, -100.0f);
        gl.glTexCoord2f(7.0f, 7.0f); gl.glVertex3f(-100.0f,  100.0f,  100.0f);
        gl.glTexCoord2f(7.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f,  100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, -100.0f,  100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f,  100.0f,  100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(100.0f,  100.0f, -100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f( 100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f( 100.0f,  100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(-100.0f,  100.0f, -100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, 100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(-100.0f,  100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f( 100.0f,  100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f( 100.0f, -100.0f, 100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(-100.0f, -100.0f,  100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f( 100.0f, -100.0f,  100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f( 100.0f, -100.0f, -100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f( 100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f( 100.0f, 100.0f,  100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f,  100.0f);
        gl.glEnd();
    }
    
    /**Prints the current fps to the screen*/
    public void printFps(int frameTime) {
    	double currentFps = 60;
    	if (frameTime > 0) currentFps = 1000/frameTime;
    	if (currentFps >= 60) currentFps = 60;
    	System.out.println(currentFps);
    }
    
	@Override
	public void keyPressed(KeyEvent e) {
		int keyDown = e.getKeyCode(); //get the key code
		if (keyDown == 27) exit = true; //esc is down
		if (keyDown == 87 && forBack == 0) forBack = 1; //w is down
		if (keyDown == 83 && forBack == 0) forBack = 2; //s is down
		if (keyDown == 65 && leftRight == 0) leftRight = 1; //a is down
		if (keyDown == 68 && leftRight == 0) leftRight = 2; //d is down
		if (keyDown == 16) shift = true; //shift is down
		if (keyDown == 32) space = true; //space is down
		if (keyDown == 17) ctrl = true; //ctrl is down
		if (keyDown == 38); //up key is down
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int keyUp = e.getKeyCode();
		if (keyUp == 87 && forBack == 1) forBack = 0; //w is released
		if (keyUp == 83 && forBack == 2) forBack = 0; //s is released
		if (keyUp == 65 && leftRight == 1) leftRight = 0; //a is released
		if (keyUp == 68 && leftRight == 2) leftRight = 0; //d is released
		if (keyUp == 16) shift = false; //shift is released
		if (keyUp == 32) space = false; //space is released
		if (keyUp == 17) ctrl = false; //ctrl is released
		if (keyUp == 10) frame.getInputField().requestFocus(); //enter is released
	}

	public void keyTyped(KeyEvent e) {}
    public void dispose(GLAutoDrawable drawable) {}
    public void reshape(GLAutoDrawable drawable, int x1, int y1, int x2, int y2) {}
}