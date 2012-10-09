package gui;

import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import utils.Int3;
import utils.Pair;
import world.RiemannCube;
import world.cubes.Cube;
import world.cubes.Floor;
import world.cubes.Glass;
import world.cubes.Wall;
import world.events.PlayerMove;
import world.objects.Player;

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
public class ViewPort extends GLCanvas implements GLEventListener, KeyListener, MouseListener {
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
    private Player player; //the player associated with this level
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
    private boolean leftMouse = false; //is true if the left mouse has been released
    private boolean rightMouse = false; //is true if right mouse has been released
    private boolean exit = false; //is true when to exit
    private boolean pause = false; //is true when the game is paused
    private boolean firstFocus = false; //waits for the first focus
    
    private Float3 camPos = new Float3(); //the position of the camera
    
    private Float2 rotation = new Float2(); //the x rotation of the camera
    private Float3 viewRot = new Float3(); //the rotation of the view camera
    
    private Float3 rotateTo = new Float3(); //the rotation the player needs to rotate to
    private Float3 rotateView = new Float3();
    
    private float moveSpeed = 0.04f; //the move speed of the camera
    private float turnSpeed = 10.0f; //the turn speed of the camera
    private int rotationSpeed = 2; //the speed that the rotation animation happens
    
    //For stepping movement
    private float stepCycle = 0.0f; //where the camera is in the step
    private float stepHeight = 0.003f;
    
    //animation flags;
    private boolean rotationAni = false; //is true when the game is rotating

    static GLU glu = new GLU(); //for GLU methods
    private Robot robot; //a robot that insures the mouse is always in the centre of the screen
    public static Animator animator; // the animator makes sure the canvas is always being updated
    
    private List<Float3> glassRender; //a list that hold all the glass to render
    private List<Pair<Float3, Integer>> playerRender; // a list of players to be rendered

    // CONSTRUCTOR
    /** Creates a new view port
     * @param frame the window this is enclosed in
     * @param width the width of the view port
     * @param height the height of the view port
     */
    public ViewPort(GameFrame frame, int width, int height) {
        addGLEventListener(this);
        addMouseListener(this);
        this.frame = frame;
        windowDim = new Int2(width, height);
        
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

        gl.glEnable(GL.GL_DEPTH_TEST); // enable depth testing
        gl.glEnable(GL.GL_BLEND); // enable transparency
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); // set the blending function
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
        
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // set the clear colour
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
        
        //get the level and the player from the client from the client
        frame.getClient().update(17); //update the client for the first time
        level = frame.getClient().getWorld();
        player = frame.getClient().player();
        
        currentTime = System.currentTimeMillis(); //update the time before starting
        
        updateCamera(); //update the camera position
        
        requestFocus();   
        
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();

        if (exit) frame.exit(); 
        
        if(pause && space) frame.exit(); //quit the game
        
        if (!frame.isActive()) {
        	if(firstFocus) pause = true;
        }
        if (!firstFocus && frame.isActive()) firstFocus = true;
        
        //check if a frame has passed and if so update the events
        long newTime = System.currentTimeMillis(); //get the time at this point
        int frameTime = (int) (newTime-currentTime); //find the length of this frame
        
        if (showFps) printFps(frameTime);
        
        if (frameTime > 55) frameTime = 55; //limit the max frame time
        currentTime = newTime; //update the current time
        accumTime += frameTime; //Accumulate the frame time
        
        while (accumTime >= frameLength) { //a frame has passed
        	//update the world
            frame.getClient().update(frameLength);
        	level = frame.getClient().getWorld();
        	player = frame.getClient().player();
        	
        	if (!pause) {
				//Process movement and rotation
        		processRotation();
			    if (!rotationAni) processMovement();//if rotating you can't move
			    updateCamera(); //update the camera position
			    processTurning();
			    
			    robot.mouseMove(mouseCentre.x, mouseCentre.y); //move the mouse to the centre of the window
        	}
		    
		    accumTime -= frameLength;
        }
        if (pause) { //clear mouse presses when paused
			rightMouse = false;
			leftMouse = false;
        }
        
        //START DRAWING
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); //clear the screen
        
        gl.glLoadIdentity(); //load the identity matrix
        
    	gl.glRotatef(rotation.x, 1.0f, 0.0f, 0.0f); //apply the x rotation
    	gl.glRotatef(rotation.y, 0.0f, 1.0f, 0.0f); //apply the y rotation
    	
    	
    	
    	gl.glRotatef(-viewRot.x, 1.0f, 0.0f, 0.0f); //apply the world x rotation
    	gl.glRotatef(-viewRot.z, 0.0f, 0.0f, 1.0f); //apply the world z rotation
    	gl.glRotatef(-viewRot.y, 0.0f, 1.0f, 0.0f); //apply the world y rotation
    	
    	
    	
        gl.glTranslatef(-camPos.x, -camPos.y, -camPos.z); //apply the translations
        
        if (high) drawSpaceBoxHigh(gl); //draw the space box in high graphics
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
        
        glassRender = new ArrayList<Float3>(); //create the glass render list
        playerRender = new ArrayList<Pair<Float3, Integer>>(); //create the player render list
        
        //iterate through the level and draw all the tiles
        for (int x = 0; x < level.size.x; ++x) {
        	for (int y = 0; y < level.size.y; ++y) {
        		for (int z = 0; z < level.size.z; ++z) {
        			Cube c = level.getCube(x, y, z); //get the current cube
        			Float3 v = new Float3(x*2, y*2, z*2); //find the position vector
        			
        			//draw the cubes
        			if (high) { //draw the high graphics cubes
        				if (c instanceof Wall) drawWallHigh(gl, v); //draw a wall
        				if (c instanceof Floor) drawFloorHigh(gl, v); //draw a floor
        				
        			}
        			else { //draw the low graphics alternatives
        				if (c instanceof Floor) drawFloorLow(gl, v); //draw a floor cube
        				else if (c instanceof Wall) drawWallLow(gl, v); //draw a wall cube
        			}
        			
        			//draw players
        			Player p = c.player(); //get the player in the cube
        			
        			if (p != null) {
        				if (p.id != player.id) //only render the other players
        					playerRender.add(new Pair<Float3, Integer>(v.copy().add(p.relPos), p.id));
        			}
        			
        			if (c instanceof Glass) glassRender.add(v);
        		}
        	}
        }
        
        //draw the players
        for (Pair<Float3, Integer> p : playerRender) {
        	drawPlayer(gl, p.first(), p.second());
        }
        
        //draw the glass last
        for (Float3 p: glassRender) {
        	if (high) drawGlassHigh(gl, p);
        	else drawGlassLow(gl, p);
        }
        
        //draw the glass around the outside of the cube
        if (high) drawOuterGlassHigh(gl);
        
        //draw the pause box over the screen
        if(pause) drawPause(gl);
    }

    /**update the camera's position from the player's position*/
    private void updateCamera() {
    	camPos.x = (player.pos().x*2)+player.relPos.x;
    	camPos.y = (player.pos().y*2)+player.relPos.y;
    	camPos.z = (player.pos().z*2)+player.relPos.z;
    }
    
    /**Process the movement*/
    private void processMovement() {
		boolean move = false; //is true when the player is moving
		Float3 newPos = new Float3(); //the new position to move to
		float straff = 1.0f; //straffing multiple
		
		if (forBack != 0 && leftRight != 0) straff = 0.5f;
		
		if (forBack == 1) { //move forward
			if (!free) {
		       	newPos.x += moveSpeed*Math.sin(rotation.y*(Math.PI/180.0))*straff;
		       	newPos.z -= moveSpeed*Math.cos(rotation.y*(Math.PI/180.0))*straff;
		   	}
		   	if (free) {
		   		newPos.y -= moveSpeed*Math.sin(rotation.x*(Math.PI/180.0));
		   		newPos.x += moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.sin(rotation.y*(Math.PI/180.0))*straff;
		   		newPos.z -= moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.cos(rotation.y*(Math.PI/180.0))*straff;
		   	}
		   	move = true;
		}
		else if (forBack == 2) { //move backwards
			if (!free) {
				newPos.x -= moveSpeed*Math.sin(rotation.y*(Math.PI/180.0))*straff;
				newPos.z += moveSpeed*Math.cos(rotation.y*(Math.PI/180.0))*straff;
			}
			if (free) {
				newPos.y += moveSpeed*Math.sin(rotation.x*(Math.PI/180.0));
				newPos.x -= moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.sin(rotation.y*(Math.PI/180.0))*straff;
				newPos.z += moveSpeed*Math.cos(rotation.x*(Math.PI/180.0))*Math.cos(rotation.y*(Math.PI/180.0))*straff;
			}
			move = true;
		}
		
		if (leftRight == 1) { //move left
			newPos.x -= moveSpeed*Math.cos(rotation.y*(Math.PI/180.0))*straff;
			newPos.z -= moveSpeed*Math.sin(rotation.y*(Math.PI/180.0))*straff;
		   	move = true;
		}
		else if (leftRight == 2) { //move right
			newPos.x += moveSpeed*Math.cos(rotation.y*(Math.PI/180.0))*straff;
			newPos.z += moveSpeed*Math.sin(rotation.y*(Math.PI/180.0))*straff;
		   	move = true;
		}
		
        //Create the stepping motion
        if (!free) {
	        if (move) {
		        if (stepCycle < Math.PI) stepCycle += 0.08f;
		        else stepCycle = 0.0f;
		        
		        newPos.y -= (float) (stepHeight*Math.cos(stepCycle));
	        }
	        
	        //if the player is rotated shift their direction based on rotation
	        if (player.orientation() == 1) {
	        	newPos.x = -newPos.x;
	        }
	        else if (player.orientation() == 2) {
	        	float temp = newPos.y;
	        	newPos.y = newPos.x;
	        	newPos.x = temp;
	        }
	        else if (player.orientation() == 3) {
	        	float temp = -newPos.y;
	        	newPos.y = -newPos.x;
	        	newPos.x = temp;
	        }
	        else if (player.orientation() == 4) {
	        	float temp = newPos.y;
	        	newPos.y = newPos.z;
	        	newPos.z = temp;
	        }
	        else if (player.orientation() == 5) {
	        	float temp = -newPos.y;
	        	newPos.y = -newPos.z;
	        	newPos.z = temp;
	        }
	        
	        //find whether the player is being blocked or not, if not send to server
	        boolean canMove = true; //is true if the player can move
        	Int3 cubeMove = new Int3();
        	
        	if (player.relPos.x+newPos.x >= 1.0f) cubeMove = new Int3(1, 0, 0);
        	else if (player.relPos.x+newPos.x <= -1.0f) cubeMove = new Int3(-1, 0, 0);
        	else if (player.relPos.y+newPos.y >= 1.0f) cubeMove = new Int3(0, 1, 0);
        	else if (player.relPos.y-newPos.y <= -1.0f) cubeMove = new Int3(0, -1, 0);
        	else if (player.relPos.z+newPos.z >= 1.0f) cubeMove = new Int3(0, 0, 1);
        	else if (player.relPos.z+newPos.z <= -1.0f) cubeMove = new Int3(0, 0, -1);
        	
        	Int3 zeroInt = new Int3(0, 0, 0);
        	
        	if (!cubeMove.equals(zeroInt)) {
        		Int3 newCube = player.pos().add(cubeMove);
	        	if (level.isValidAction(new PlayerMove(player.id, newCube))) {
	        		frame.getClient().push(new PlayerMove(player.id, newCube));
	        	}
	        	else canMove = false;
        	}
    		
        	if (canMove) {
        		player.relPos.x += newPos.x;
        		player.relPos.y += newPos.y;
        		player.relPos.z += newPos.z;
        	}
        }
        else { //in free camera mode
        	if (shift) moveSpeed = 0.16f; //move at double speed if shift
        	else moveSpeed = 0.04f;
        	if (space) player.relPos.y += moveSpeed; //move straight up
        	else if (ctrl) player.relPos.y -= moveSpeed; //move straight down
        	
        	player.relPos.x += newPos.x;
    		player.relPos.y += newPos.y;
    		player.relPos.z += newPos.z;
        }
    }
    
    /**Process the turning*/
    private void processTurning() {
        mouse = new Float2((float) MouseInfo.getPointerInfo().getLocation().getX(),
				   (float) MouseInfo.getPointerInfo().getLocation().getY());
        
        if (mouse.x != mouseCentre.x) { //turn right or left
        	rotation.y += (float) ((mouse.x-mouseCentre.x)/turnSpeed);
        }
        if (mouse.y != mouseCentre.y) { //turn up or down
        	rotation.x += (float) (((mouse.y-mouseCentre.y)/turnSpeed));
        }
        
        //keep within 0 to 360
        if (rotation.y > 360) rotation.y -= 360;
        else if (rotation.y < 0) rotation.y += 360;
    }
    
    /**Process any rotations*/
    private void processRotation() {
    	if (leftMouse || rightMouse) { //if the right mouse is down rotate towards correct angle
    		if (!rotationAni) { //don't rotate while rotating
    			rotateTo = player.rotation.copy(); //the angle the player is to rotate to
    			rotateView = viewRot.copy(); //what to rotate the view to
    			Float2 rot = rotation; //short hand
    			
    			//HERE LIES THE BEAST THAT IS ROTATION. DO NOT LOOK FOR FEAR OF YOUR EYES BURNING OUT.
    			//search for the correct rotation based on the players current rotation
    			if (player.orientation() == 0) { //player is on the ground
	    			if (rot.y > 45 && rot.y <= 135) {
						if (leftMouse) {rotateTo.x -= 90; rotateView.x += 90; player.orientation(4);}
						else {rotateTo.x += 90; rotateView.x -= 90; player.orientation(5);}
	    			}
	    			else if (rot.y > 225 && rot.y <= 315) {
	    				if (leftMouse) {rotateTo.x += 90; rotateView.x -= 90; player.orientation(5);}
	    				else {rotateTo.x -= 90; rotateView.x += 90; player.orientation(4);}
	    			}
	    			else if(rot.y > 135 && rot.y <= 225) {
	    				if (leftMouse) {rotateTo.z -= 90; rotateView.z += 90; player.orientation(3);}
	    				else {rotateTo.z += 90; rotateView.z -= 90; player.orientation(2);}
	    			}
	    			else {
	    				if (leftMouse) {rotateTo.z += 90; rotateView.z -= 90; player.orientation(2);}
	    				else {rotateTo.z -= 90; rotateView.z += 90; player.orientation(3);}
	    			}
    			}
    			else if (player.orientation() == 1) { //player is on the right wall
	    			if (rot.y > 45 && rot.y <= 135) {
						if (leftMouse) {rotateTo.x += 90; rotateView.x += 90; player.orientation(4);}
						else {rotateTo.x -= 90; rotateView.x -= 90; player.orientation(5);}
	    			}
	    			else if (rot.y > 225 && rot.y <= 315) {
	    				if (leftMouse) {rotateTo.x -= 90; rotateView.x -= 90;  player.orientation(5);}
	    				else {rotateTo.x += 90; rotateView.x += 90; player.orientation(4);}
	    			}
	    			else if(rot.y > 135 && rot.y <= 225) {
	    				if (leftMouse) {rotateTo.z -= 90; rotateView.z += 90; player.orientation(2);}
	    				else {rotateTo.z += 90; rotateView.z -= 90; player.orientation(3);}
	    			}
	    			else {
	    				if (leftMouse) {rotateTo.z += 90; rotateView.z -= 90; player.orientation(3);}
	    				else {rotateTo.z -= 90; rotateView.z += 90; player.orientation(2);}
	    			}
    			}
    			else if (player.orientation() == 2) { //player is on the wall
	    			if (rot.y > 45 && rot.y <= 135) {
						if (leftMouse) {rotateTo.y -= 90; rotateView.y += 90; player.orientation(4);}
						else {rotateTo.y += 90; rotateView.y -= 90; player.orientation(5);}
	    			}
	    			else if (rot.y > 225 && rot.y <= 315) {
	    				if (leftMouse) {rotateTo.y += 90; rotateView.y -= 90; player.orientation(5);}
	    				else {rotateTo.y -= 90; rotateView.y += 90; player.orientation(4);}
	    			}
	    			else if(rot.y > 135 && rot.y <= 225) {
	    				if (leftMouse) {rotateTo.z -= 90; rotateView.z += 90;  player.orientation(0);}
	    				else {rotateTo.z += 90; rotateView.z -= 90; player.orientation(1);}
	    			}
	    			else {
	    				if (leftMouse) {rotateTo.z += 90; rotateView.z -= 90; player.orientation(1);}
	    				else {rotateTo.z -= 90; rotateView.z += 90; player.orientation(0);}
	    			}
    			}
    			else if (player.orientation() == 3) { //player is on the right wall
	    			if (rot.y > 45 && rot.y <= 135) {
						if (leftMouse) {rotateTo.y += 90; rotateView.y -= 90; player.orientation(4);}
						else {rotateTo.y -= 90; rotateView.y += 90; player.orientation(5);}
	    			}
	    			else if (rot.y > 225 && rot.y <= 315) {
	    				if (leftMouse) {rotateTo.y -= 90; rotateView.y += 90; player.orientation(5);}
	    				else {rotateTo.y += 90; rotateView.y -= 90; player.orientation(4);}
	    			}
	    			else if(rot.y > 135 && rot.y <= 225) {
	    				if (leftMouse) {rotateTo.z -= 90; rotateView.z += 90; player.orientation(1);}
	    				else {rotateTo.z += 90; rotateView.z -= 90; player.orientation(0);}
	    			}
	    			else {
	    				if (leftMouse) {rotateTo.z += 90; rotateView.z -= 90; player.orientation(0);}
	    				else {rotateTo.z -= 90; rotateView.z += 90; player.orientation(1);}
	    			}
    			}
    			else if (player.orientation() == 4) { //player is on the front wall
	    			if (rot.y > 45 && rot.y <= 135) {
						if (leftMouse) {rotateTo.x -= 90; rotateView.x += 90; player.orientation(1);}
						else {rotateTo.x += 90; rotateView.x -= 90; player.orientation(0);}
	    			}
	    			else if (rot.y > 225 && rot.y <= 315) {
	    				if (leftMouse) {rotateTo.x += 90; rotateView.x -= 90; player.orientation(0);}
	    				else {rotateTo.x -= 90; rotateView.x += 90; player.orientation(1);}
	    			}
	    			else if(rot.y > 135 && rot.y <= 225) {
	    				if (leftMouse) {rotateTo.y -= 90; rotateView.y += 90; player.orientation(3);}
	    				else {rotateTo.y += 90; rotateView.y -= 90; player.orientation(2);}
	    			}
	    			else {
	    				if (leftMouse) {rotateTo.z += 90; rotateView.y += 90; player.orientation(2);}
	    				else {rotateTo.z -= 90; rotateView.y -= 90; player.orientation(3);}
	    			}
    			}
    			else if (player.orientation() == 5) { //player is on the back wall
	    			if (rot.y > 45 && rot.y <= 135) {
						if (leftMouse) {rotateTo.x -= 90; rotateView.x += 90; player.orientation(0);}
						else {rotateTo.x += 90; rotateView.x -= 90; player.orientation(1);}
	    			}
	    			else if (rot.y > 225 && rot.y <= 315) {
	    				if (leftMouse) {rotateTo.x += 90; rotateView.x -= 90; player.orientation(1);}
	    				else {rotateTo.x -= 90; rotateView.x += 90; player.orientation(0);}
	    			}
	    			else if(rot.y > 135 && rot.y <= 225) {
	    				if (leftMouse) {rotateTo.y += 90; rotateView.y -= 90; player.orientation(3);}
	    				else {rotateTo.y -= 90; rotateView.y += 90; player.orientation(2);}
	    			}
	    			else {
	    				if (leftMouse) {rotateTo.y -= 90; rotateView.y += 90; player.orientation(2);}
	    				else {rotateTo.y += 90; rotateView.y -= 90; player.orientation(3);}
	    			}
    			}
    			//THE BEAST HAS ENDED	
    			
	    		//reset the relative position
	    		player.relPos.x = player.relPos.y = player.relPos.z = 0;
	    		
	    		//start the rotation animation
	    		rotationAni = true;
    		}

    		rightMouse = false;
    		leftMouse = false;
    	}
    	if (rotationAni) { //animate the rotation
    		//increase the world rotations
    		if (rotateTo.x > player.rotation.x) player.rotation.x += rotationSpeed; //increase the rotation
    		else if (rotateTo.x < player.rotation.x) player.rotation.x -= rotationSpeed; //decrease the rotation
    		if (rotateTo.y > player.rotation.y) player.rotation.y += rotationSpeed; //increase the rotation
    		else if (rotateTo.y < player.rotation.y) player.rotation.y -= rotationSpeed; //decrease the rotation
    		if (rotateTo.z > player.rotation.z) player.rotation.z += rotationSpeed; //increase the rotation
    		else if (rotateTo.z < player.rotation.z) player.rotation.z -= rotationSpeed; //decrease the rotation
    		
    		//increase the view rotations
    		if (viewRot.x > rotateView.x) viewRot.x += rotationSpeed;
    		else if (viewRot.x < rotateView.x) viewRot.x -= rotationSpeed;
    		if (viewRot.y > rotateView.y) viewRot.y += rotationSpeed;
    		else if (viewRot.y < rotateView.y) viewRot.y -= rotationSpeed;
    		if (viewRot.z > rotateView.z) viewRot.z += rotationSpeed;
    		else if (viewRot.z < rotateView.z) viewRot.z -= rotationSpeed;
    		
    		if (rotateTo.equals(player.rotation)) {
    			rotationAni = false; //the animation is finished
    			
    			//make sure x rotations are within 0 to 360
    			if (player.rotation.x >= 360) player.rotation.x = 0;
    			else if (player.rotation.x < 0) player.rotation.x = 360+player.rotation.x;
    			else if (player.rotation.x < 0) player.rotation.x = 360+player.rotation.x;
    			if (player.rotation.y >= 360) player.rotation.y = 0;
    			else if (player.rotation.y < 0) player.rotation.y = 360+player.rotation.y;
    			if (player.rotation.z >= 360) player.rotation.z = 0;
    			else if (player.rotation.z < 0) player.rotation.z = 360+player.rotation.z;
    			
    			//switch the player's rotation for an equivalent rotation
    			if (player.orientation() == 0) {
    				player.rotation.x = 0;
    				player.rotation.y = 0;
    				player.rotation.z = 0;
    				viewRot = new Float3(0, 0, 0);
    			}
    			else if (player.orientation() == 1) {
    				//rotation.y -= player.rotation.x-90.0f;
    				player.rotation.x = 0;
    				player.rotation.y = 0;
    				player.rotation.z = 180;
    				viewRot = new Float3(0, 0, 180);
    				if (rotateTo.x == 180) rotation.y += 180;
    			}
    			else if (player.orientation() == 2) {
    				player.rotation.x = 0;
    				player.rotation.y = 0;
    				player.rotation.z = 90;
    				viewRot = new Float3(0, 0, 90);
    				if (rotateTo.x == 90) rotation.y += 90;
    				if (rotateTo.x == 270) rotation.y -= 90;
    			}
    			else if (player.orientation() == 3) {
    				player.rotation.x = 0;
    				player.rotation.y = 0;
    				player.rotation.z = 270;
    				viewRot = new Float3(0, 0, 270);
    				if (rotateTo.x == 90) rotation.y -= 90;
    				if (rotateTo.x == 270) rotation.y += 90;
    			}
    			else if (player.orientation() == 4) {
    				player.rotation.x = 270;
    				player.rotation.y = 0;
    				player.rotation.z = 0;
    				viewRot = new Float3(270, 0, 0);
    				if (rotateTo.z == 90) rotation.y += 90;
    				if (rotateTo.z == 180) rotation.y += 180;
    				if (rotateTo.z == 270) rotation.y -= 90;
    			}
    			else if (player.orientation() == 5) {
    				player.rotation.x = 90;
    				player.rotation.y = 0;
    				player.rotation.z = 0;
    				viewRot = new Float3(90, 0, 0);
    				if (rotateTo.z == 90) rotation.y -= 90;
    				if (rotateTo.z == 180) rotation.y += 180;
    				if (rotateTo.z == 270) rotation.y += 90;
    			}
    		}
    	}
    }
    
    
    private void drawPlayer(GL2 gl, Float3 v, int playerId) {
    	if (high) {
	    	if (playerId == 0) {
	    		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[4]); //bind the player1 texture
	    	}
	    	else if (playerId == 1) {
	    		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[5]); //bind the player2 texture
	    	}
	    	else if (playerId == 2) {
	    		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[6]); //bind the player3 texture
	    	}
	    	else if (playerId == 3) {
	    		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[7]); //bind the player4 texture
	    	}
    	}
    	else {
    		gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    	}
    	
    	gl.glPushMatrix(); //push a new matrix
    	
    	gl.glTranslatef(v.x, v.y, v.z); //translate to world position
    	
    	//find the angle to rotate by
    	Float2 vectorBetween = new Float2(camPos.x-v.x, camPos.z-v.z);
    	float yAngle = (float) (vectorBetween.heading()*(180.0f/Math.PI));
    	
    	gl.glRotatef(-(yAngle+90.0f), 0.0f, 1.0f, 0.0f); //apply the y rotation
    	
    	//draw the player onto a quad
    	gl.glBegin(GL2.GL_QUADS);
    	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-0.5f,  0.5f, 0);
    	gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f( 0.5f,  0.5f, 0);
    	gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 0.5f, -0.5f, 0);
    	gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-0.5f, -0.5f, 0);
    	gl.glEnd();
    	
    	gl.glPopMatrix(); //pop the matrix
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
    	
    	//apply the world orientation rotation
    	gl.glRotatef(player.rotation.y, 0.0f, 1.0f, 0.0f);
    	gl.glRotatef(player.rotation.x, 1.0f, 0.0f, 0.0f);
    	
    	
    	
    	gl.glRotatef(player.rotation.z, 0.0f, 0.0f, 1.0f);
    	
    	//apply the rotations
    	gl.glRotatef(zRot, 0.0f, 0.0f, 1.0f);
    	gl.glRotatef(yRot, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(xRot, 1.0f, 0.0f, 0.0f);
    	
    	if (!inside) gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f); //flip if outside
    	
    	//translate to the edge of the cube
    	if (inside) gl.glTranslatef(0, -0.995f, 0);
    	else gl.glTranslatef(0, 0.995f, 0);
    	
    	//now draw the quad
    	gl.glBegin(GL2.GL_QUADS);
    	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1.0f, 0, -1.0f);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1.0f, 0,  1.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1.0f, 0,  1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1.0f, 0, -1.0f);
        gl.glEnd();
        
        gl.glPopMatrix(); //pop the matrix
    }
    
    /**Draws a openGL coloured quad
     * @param gl
     * @param v the position vector of the cube
     * @param n the normal vector of the quad
     * @param col = the rgb colour of the quad
     * @param a the alpha value of the colour of the quad*/
    private void drawQuadCol(GL2 gl, Float3 v, Float3 n, boolean inside, Float3 col, float a) {
    	//find the difference between the point and the normal
    	Float3 dv = new Float3((n.x-v.x), (n.y-v.y), (n.z-v.z));
    	
    	//Find the rotation amounts
    	float xRot = Math.abs(dv.y)*(90.0f+dv.y*90.0f);
    	float yRot = Math.abs(dv.z)*(dv.z*90.0f);
    	float zRot = Math.abs(dv.x)*(dv.x*90.0f);

    	gl.glPushMatrix(); //push new matrix
    	
    	gl.glTranslatef(v.x, v.y, v.z); //translate world to position
    	
    	//apply the world orientation rotation
    	gl.glRotatef(-player.rotation.x, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(-player.rotation.z, 0.0f, 0.0f, 1.0f);
    	
    	//apply the rotations
    	gl.glRotatef(zRot, 0.0f, 0.0f, 1.0f);
    	gl.glRotatef(yRot, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(xRot, 1.0f, 0.0f, 0.0f);
    	
    	if (!inside) gl.glRotatef(180.0f, 1.0f, 0.0f, 0.0f); //flip if outside
    	
    	//translate to the edge of the cube
    	if (inside) gl.glTranslatef(0, -1, 0);
    	else gl.glTranslatef(0, 1, 0);
    	
    	//now draw the quad
    	gl.glColor4f(col.x, col.y, col.z, a);
    	gl.glBegin(GL2.GL_QUADS);
    	gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-1, 0, -1);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1, 0,  1);
        gl.glColor4f(col.x*0.75f, col.y*0.75f, col.z*0.75f, a);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f( 1, 0,  1);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 1, 0, -1);
        gl.glEnd();
        
        gl.glPopMatrix(); //pop the matrix
    }
    
    /**Draw a floor cube in high graphics
     * @param gl
     * @param v the position vector of the cube*/
    private void drawFloorHigh(GL2 gl, Float3 v) {
    	if (!noFloor) {
	    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
	    	drawQuadTex(gl, v, new Float3(v.x, v.y-1, v.z), true);
			drawQuadTex(gl, v, new Float3(v.x, v.y+1, v.z), true);
    	}
    }
    
    /**Draw a wall cube in high graphics
     * @param gl
     * @param v the position vector of the cube*/
    private void drawWallHigh(GL2 gl, Float3 v) {
    	//draw the 4 walls
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[1]); //bind the wall tile texture
		drawQuadTex(gl, v, new Float3(v.x-1, v.y, v.z  ), false);
		drawQuadTex(gl, v, new Float3(v.x+1, v.y, v.z  ), false);
		drawQuadTex(gl, v, new Float3(v.x,   v.y, v.z+1), false);
		drawQuadTex(gl, v, new Float3(v.x,   v.y, v.z-1), false);
		//draw the floor
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
		drawQuadTex(gl, v, new Float3(v.x, v.y-1, v.z), false);
		drawQuadTex(gl, v, new Float3(v.x, v.y+1, v.z), false);
    }
    
    /**Draw a glass cube in high graphics
     * @param gl
     * @param v the position vector of the cube*/
    private void drawGlassHigh(GL2 gl, Float3 v) {
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
    
    /**Draw a floor cube in low graphics
     * @param gl
     * @param v the position vector of the cube*/
    private void drawFloorLow(GL2 gl, Float3 v) {
    	if (!noFloor) {
    		gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    		Float3 colour = new Float3(0.0f, 1.0f, 1.0f); //set the colour of the floor
	    	drawQuadCol(gl, v, new Float3(v.x, v.y-1, v.z), true, colour, 1.0f);
			drawQuadCol(gl, v, new Float3(v.x, v.y+1, v.z), true, colour, 1.0f);
    	}
    }
    
    /**Draw a wall cube in low graphics
     * @param gl
     * @param v the position vector of the cube*/
    private void drawWallLow(GL2 gl, Float3 v) {
    	//draw the 4 walls
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	Float3 colour = new Float3(1.0f, 0.0f, 1.0f); //set the colour of the wall
		drawQuadCol(gl, v, new Float3(v.x-1, v.y, v.z  ), false, colour, 1.0f);
		drawQuadCol(gl, v, new Float3(v.x+1, v.y, v.z  ), false, colour, 1.0f);
		drawQuadCol(gl, v, new Float3(v.x,   v.y, v.z+1), false, colour, 1.0f);
		drawQuadCol(gl, v, new Float3(v.x,   v.y, v.z-1), false, colour, 1.0f);
		//draw the floor
		colour = new Float3(0.0f, 1.0f, 1.0f); //set the colour of the floor
		drawQuadCol(gl, v, new Float3(v.x, v.y-1, v.z), false, colour, 1.0f);
		drawQuadCol(gl, v, new Float3(v.x, v.y+1, v.z), false, colour, 1.0f);
    }
    
    /**Draw a glass cube in low graphics
     * @param gl
     * @param v the position vector of the cube*/
    private void drawGlassLow(GL2 gl, Float3 v) {
    	//draw the floor
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
		Float3 colour = new Float3(0.0f, 1.0f, 1.0f); //set the colour of the floor
		drawQuadCol(gl, v, new Float3(v.x, v.y-1, v.z), true,  colour, 1.0f);
		drawQuadCol(gl, v, new Float3(v.x, v.y+1, v.z), true,  colour, 1.0f);
		drawQuadCol(gl, v, new Float3(v.x, v.y-1, v.z), false, colour, 1.0f);
		drawQuadCol(gl, v, new Float3(v.x, v.y+1, v.z), false, colour, 1.0f);
    	//draw the 4 walls
		colour = new Float3(1.0f, 1.0f, 1.0f);
		drawQuadCol(gl, v, new Float3(v.x-1, v.y, v.z), true,  colour, 0.4f);
		drawQuadCol(gl, v, new Float3(v.x+1, v.y, v.z), true,  colour, 0.4f);
		drawQuadCol(gl, v, new Float3(v.x, v.y, v.z+1), true,  colour, 0.4f);
		drawQuadCol(gl, v, new Float3(v.x, v.y, v.z-1), true,  colour, 0.4f);
		drawQuadCol(gl, v, new Float3(v.x-1, v.y, v.z), false, colour, 0.4f);
		drawQuadCol(gl, v, new Float3(v.x+1, v.y, v.z), false, colour, 0.4f);
		drawQuadCol(gl, v, new Float3(v.x, v.y, v.z+1), false, colour, 0.4f);
		drawQuadCol(gl, v, new Float3(v.x, v.y, v.z-1), false, colour, 0.4f);
    }
    
    /**Draws an outer box of glass around the level in high graphics
     * @param gl*/
    private void drawOuterGlassHigh(GL2 gl) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[2]); //bind the glass texture
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,         0.0f        ); gl.glVertex3f(-1.01f,  level.size.y*2-1, -1.0f            );
        gl.glTexCoord2f(0.0f,         level.size.z); gl.glVertex3f(-1.01f,  level.size.y*2-1,  level.size.z*2-1);
        gl.glTexCoord2f(level.size.y, level.size.z); gl.glVertex3f(-1.01f, -1.0f,              level.size.z*2-1);
        gl.glTexCoord2f(level.size.y, 0.0f        ); gl.glVertex3f(-1.01f, -1.0f,             -1.0f            );
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,         0.0f        ); gl.glVertex3f(level.size.x*2-0.99f,  level.size.y*2-1, -1.0f            );
        gl.glTexCoord2f(0.0f,         level.size.z); gl.glVertex3f(level.size.x*2-0.99f, -1.0f,             -1.0f            );
        gl.glTexCoord2f(level.size.y, level.size.z); gl.glVertex3f(level.size.x*2-0.99f, -1.0f,              level.size.z*2-1);
        gl.glTexCoord2f(level.size.y, 0.0f        ); gl.glVertex3f(level.size.x*2-0.99f,  level.size.y*2-1,  level.size.z*2-1);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,         0.0f        ); gl.glVertex3f(-1.0f,              level.size.y*2-1, level.size.z*2-0.99f);
        gl.glTexCoord2f(0.0f,         level.size.y); gl.glVertex3f( level.size.x*2-1,  level.size.y*2-1, level.size.z*2-0.99f);
        gl.glTexCoord2f(level.size.x, level.size.y); gl.glVertex3f( level.size.x*2-1, -1.0f,             level.size.z*2-0.99f);
        gl.glTexCoord2f(level.size.x, 0.0f        ); gl.glVertex3f(-1.0f,             -1.0f,             level.size.z*2-0.99f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,         0.0f        ); gl.glVertex3f(-1.0f,             level.size.y*2-1, -1.01f);
        gl.glTexCoord2f(0.0f,         level.size.y); gl.glVertex3f(-1.0f,            -1.0f,             -1.01f);
        gl.glTexCoord2f(level.size.x, level.size.y); gl.glVertex3f(level.size.x*2-1, -1.0f,             -1.01f);
        gl.glTexCoord2f(level.size.x, 0.0f        ); gl.glVertex3f(level.size.x*2-1,  level.size.y*2-1, -1.01f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,         0.0f        ); gl.glVertex3f(-1.0f,            level.size.y*2-0.99f,  level.size.z*2-1);
        gl.glTexCoord2f(0.0f,         level.size.z); gl.glVertex3f(-1.0f,            level.size.y*2-0.99f, -1.0f            );
        gl.glTexCoord2f(level.size.x, level.size.z); gl.glVertex3f(level.size.x*2-1, level.size.y*2-0.99f, -1.0f            );
        gl.glTexCoord2f(level.size.x, 0.0f        ); gl.glVertex3f(level.size.x*2-1, level.size.y*2-0.99f,  level.size.z*2-1);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f,         0.0f        ); gl.glVertex3f( level.size.x*2-1, -1.01f, -1.0f            );
        gl.glTexCoord2f(0.0f,         level.size.z); gl.glVertex3f(-1.0f,             -1.01f, -1.0f            );
        gl.glTexCoord2f(level.size.x, level.size.z); gl.glVertex3f(-1.0f,             -1.01f,  level.size.z*2-1);
        gl.glTexCoord2f(level.size.x, 0.0f        ); gl.glVertex3f( level.size.x*2-1, -1.01f,  level.size.z*2-1);
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
    
    public void drawPause(GL2 gl) {
    	gl.glDisable(GL.GL_DEPTH_TEST);
    	gl.glLoadIdentity(); //load the identity matrix
    	//draw the paused background
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	gl.glColor4f(0.0f, 0.0f, 0.0f, 0.85f);
    	gl.glBegin(GL2.GL_QUADS);
    	gl.glVertex3f(-1.0f, 1.0f, -1.0f);
    	gl.glVertex3f(-1.0f, -1.0f, -1.0f);
    	gl.glVertex3f( 1.0f, -1.0f, -1.0f);
    	gl.glVertex3f( 1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        //draw the paused title
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[8]);
    	gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.3f,  0.37f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.3f,  0.25f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 0.3f,  0.25f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 0.3f,  0.37f, -1.0f);
        gl.glEnd();
        //draw the pause resume text
        gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[9]);
    	gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.3f,  0.15f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.3f,  0.07f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 0.3f,  0.07f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 0.3f,  0.15f, -1.0f);
        gl.glEnd();
        //draw the exit text
        gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[10]);
    	gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-0.3f,   0.00f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-0.3f,  -0.08f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( 0.3f,  -0.08f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( 0.3f,   0.00f, -1.0f);
        gl.glEnd();
    	gl.glEnable(GL.GL_DEPTH_TEST);
    	
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
		if (keyDown == 118) exit = true; //f7 is down
		if (keyDown == 87 && forBack == 0) forBack = 1; //w is down
		if (keyDown == 83 && forBack == 0) forBack = 2; //s is down
		if (keyDown == 65 && leftRight == 0) leftRight = 1; //a is down
		if (keyDown == 68 && leftRight == 0) leftRight = 2; //d is down
		if (keyDown == 16) shift = true; //shift is down
		if (keyDown == 32) space = true; //space is down
		if (keyDown == 17) ctrl = true; //ctrl is down
		if (keyDown == 27) {
			pause = !pause; //pause or unpause the game
			frame.showMouse(pause);
			robot.mouseMove(mouseCentre.x, mouseCentre.y); //move the mouse to the centre of the window
		}
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
		if (keyUp == 10) frame.getInputField().requestFocus(); // enter is released
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		int button = e.getButton();
		if (button == 1) leftMouse = true; //left mouse has been be released
		else if (button == 3) rightMouse = true; //right mouse has been released
	}

	public void keyTyped(KeyEvent e) {}
    public void dispose(GLAutoDrawable drawable) {}
    public void reshape(GLAutoDrawable drawable, int x1, int y1, int x2, int y2) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}