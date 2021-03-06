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

import sounds.Music;
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
import world.events.ItemDrop;
import world.events.ItemUseStop;
import world.events.ItemPickup;
import world.events.ItemUseStart;
import world.events.LevelChange;
import world.events.MarkerCreated;
import world.events.PlayerMove;
import world.events.PlayerRelPos;
import world.objects.Button;
import world.objects.Container;
import world.objects.GameObject;
import world.objects.LightSource;
import world.objects.Lock;
import world.objects.Player;
import world.objects.doors.Door;
import world.objects.doors.EntranceDoor;
import world.objects.doors.ExitDoor;
import world.objects.items.Key;
import world.objects.items.Token;

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
    public static boolean sound = true; //is true when to play sound
    
    //openGL
    private GL2 ogl;
    static GLU glu = new GLU(); //for GLU methods
    
    //tools
    private Robot robot; //a robot that insures the mouse is always in the centre of the screen
    public static Animator animator; // the animator makes sure the canvas is always being updated
    
    //fps management
    private long currentTime = System.currentTimeMillis(); //get the current time
    private int accumTime = 0; //the amount of time accumulated since the last frame
    private final int frameLength = 17; //the time of a frame
    
    //references
    private RiemannCube level; //the level
    private Player player; //the player associated with this level
    private Resources resources; //the resources
    
    private Float2 windowDim; //the window dimension
    
    private Float2 mouse = new Float2(0f, 0f); //the current mouse x position
    private Int2 mouseCentre = new Int2(450, 300); //the mouse x centre
    
    //keys
    private int forBack = 0; //0: none, 1: forwards, 2: backwards
    private int leftRight = 0; //0: none, 1: left, 2: right
    private boolean quit; //is true when to quit the game
    private boolean shift = false; //is true if shift is pressed
    private boolean space = false; //is true if space is pressed
    private boolean spaceHeld = false;
    private boolean spaceReleased = false;
    private boolean ctrl = false; //is true if crtl is pressed
    private boolean eDown = false; //if the key is pressed
    private boolean fDown = false; //if the f key is pressed
    private boolean mDown = false;
    private boolean leftMouse = false; //is true if the left mouse has been released
    private boolean rightMouse = false; //is true if right mouse has been released
    private boolean pause = false; //is true when the game is paused
    private boolean firstFocus = false; //waits for the first focus
    private boolean regain = false; //is true when focus has just been regained
    
    private Float3 camPos = new Float3(); //the position of the camera
    
    private Float2 rotation = new Float2(); //the x rotation of the camera
    private Float3 viewRot = new Float3(); //the rotation of the view camera
    
    private Float3 rotateTo = new Float3(); //the rotation the player needs to rotate to
    private Float3 rotateView = new Float3();
    
    private float moveSpeed = 0.045f; //the move speed of the camera
    private float turnSpeed = 10.0f; //the turn speed of the camera
    private int rotationSpeed = 2; //the speed that the rotation animation happens
    
    //For stepping movement
    private float stepCycle = 0.0f; //where the camera is in the step
    private float stepHeight = 0.003f;
    
    //animation flags;
    private boolean rotationAni = false; //is true when the game is rotating
    
    //rendering lists
    private List<Float3> glassRender; //a list that hold all the glass to render
    private List<Pair<Float3, Integer>> playerRender; // a list of players to be rendered
    private List<Pair<Float3, Container>> containerRender; //a list of containers to be rendered
    private List<Float3> flareRender;

    //CONSTRUCTOR
    /** Creates a new view port
     * @param frame the window this is enclosed in
     * @param width the width of the view port
     * @param height the height of the view port
     */
    public ViewPort(GameFrame frame, int width, int height) {
        addGLEventListener(this);
        addMouseListener(this);
        this.frame = frame;
        windowDim = new Float2(width, height);
        
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
        ogl = gl;

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
        resources = new Resources(gl);
        
        gl.glEnable(GL.GL_TEXTURE_2D); //enable 2d textures
        
        
        //get the level and the player from the client from the client
        frame.getClient().update(17); //update the client for the first time
        level = frame.getClient().getWorld();
        player = frame.getClient().player();
        
        //set the graphics fields
        Graphics.setResources(resources);
        Graphics.setHigh(high);
        
        currentTime = System.currentTimeMillis(); //update the time before starting
        
        updateCamera(); //update the camera position
        
        if (sound) { //play the music
            Music music = new Music(64000L);
            music.playSound("resources/audio/music/Cubism.wav");
        }
        
        requestFocus();
    }
    
    public boolean waitForData() {
        frame.getClient().update(17);
        level = frame.getClient().getWorld();
        player = frame.getClient().player();
        
        if (level != null) Graphics.setLevel(level);
        if (player != null) Graphics.setPlayer(player);
        
        if (player == null) return false; //still waiting for data
        
        return true;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        Graphics.setGL(gl); //pass the gl to the graphics
        
        if(pause && quit) frame.exit(); //quit the game
        
        if (!frame.isActive()) {
            if(firstFocus) pause = true;
        }
        if (!firstFocus && frame.isActive()) firstFocus = true;
        
        //check if a frame has passed and if so update the events
        long newTime = System.currentTimeMillis(); //get the time at this point
        int frameTime = (int) (newTime-currentTime); //find the length of this frame
        
        if (showFps) Graphics.printFps(frameTime);
        
        currentTime = newTime; //update the current time
        accumTime += frameTime; //Accumulate the frame time
        
        while (accumTime >= frameLength) { //a frame has passed
            //update the world
            frame.getClient().update(frameLength);
            level = frame.getClient().getWorld();
            player = frame.getClient().player();
            
            if (!pause && player!=null) {
                //Process movement and rotation
                processAction();
                processRotation();
                if (!rotationAni && !spaceHeld) processMovement();//if rotating you can't move
                updateCamera(); //update the camera position
                processTurning();
                
                updateWorld(); //update the world

                robot.mouseMove(mouseCentre.x, mouseCentre.y); //move the mouse to the centre of the window
                quit = false;
                mDown = false;
            }

            accumTime -= frameLength;
        }
        if (pause) { //clear mouse presses when paused
            rightMouse = false;
            leftMouse = false;
        }
        
        if (level != null) Graphics.setLevel(level);
        if (player != null) Graphics.setPlayer(player);
        
        if (player == null) return; //if there is no player yet don't start drawing
        
        drawWorld(gl); //draw the world

    }
    
    /**updates the world*/
    private void updateWorld() {        
        //iterate through the level
        for (int x = 0; x < level.size.x; ++x) {
            for (int y = 0; y < level.size.y; ++y) {
                for (int z = 0; z < level.size.z; ++z) {
                    Cube c = level.getCube(x, y, z); //get the current cube
                    
                    //get the objects in the cubes
                    for (GameObject obj : c.objects()) { //gets all the objects that the cube contains
	                    if (obj instanceof Door) {
	                        if (!((Door) obj).isClosed()) {
	                            ((Door) obj).animate(); //animate the door
	                            if (((Door) obj).playSound()) { //play door sound
	                                if (sound) {
	                                    Music doorSound = new Music();
	                                    doorSound.playSound("resources/audio/fx/door.wav");
	                                }
	                                ((Door) obj).soundPlayed();
	                            }
	                        }
                        }
	                    else if (obj instanceof Key) {
	                    	((Key) obj).rotate();
	                    }
	                    else if (obj instanceof Container) {
	                    	((Container) obj).animate();
	                    	GameObject go = ((Container) obj).containers.get(((Container) obj).color()).getItem();
	                    	if (go instanceof Key) {
	                    		((Key) go).rotate();
	                    	}
	                    }
                    }
                }
            }
        }
        Graphics.rotatePortal();
        Graphics.rotateToken();
    }
    
    /**draws the world*/
    private void drawWorld(GL2 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); //clear the screen
        
        gl.glLoadIdentity(); //load the identity matrix
        
        gl.glRotatef(rotation.x, 1.0f, 0.0f, 0.0f); //apply the x rotation
        gl.glRotatef(rotation.y, 0.0f, 1.0f, 0.0f); //apply the y rotation
        
        gl.glRotatef(-viewRot.x, 1.0f, 0.0f, 0.0f); //apply the world x rotation
        gl.glRotatef(-viewRot.z, 0.0f, 0.0f, 1.0f); //apply the world z rotation
        gl.glRotatef(-viewRot.y, 0.0f, 1.0f, 0.0f); //apply the world y rotation
        
        gl.glTranslatef(-camPos.x, -camPos.y, -camPos.z); //apply the translations
        
        if (high) Graphics.drawSpaceBoxHigh(); //draw the space box in high graphics
        
        //initialise rendering lists
        glassRender = new ArrayList<Float3>(); //create the glass render list
        playerRender = new ArrayList<Pair<Float3, Integer>>(); //create the player render list
        containerRender = new ArrayList<Pair<Float3, Container>>(); //create the container render list
        flareRender = new ArrayList<Float3>();
        
        //iterate through the level and draw all the tiles
        for (int x = 0; x < level.size.x; ++x) {
            for (int y = 0; y < level.size.y; ++y) {
                for (int z = 0; z < level.size.z; ++z) {
                    Cube c = level.getCube(x, y, z); //get the current cube
                    Float3 v = new Float3(x*2, y*2, z*2); //find the position vector
                    
                    //draw the cubes
                    if (high) { //draw the high graphics cubes
                        if (c instanceof Wall) Graphics.drawWallHigh(v); //draw a wall
                        if (c instanceof Floor && !noFloor) Graphics.drawFloorHigh(v); //draw a floor
                        
                    }
                    else { //draw the low graphics alternatives
                        if (c instanceof Floor && !noFloor) Graphics.drawFloorLow(v); //draw a floor cube
                        else if (c instanceof Wall) Graphics.drawWallLow(v); //draw a wall cube
                    }
                    
                    if (c instanceof Glass) {
                        if (high) Graphics.drawFloorHigh(v);
                        else Graphics.drawFloorLow(v); //draw a floor cube
                        glassRender.add(v);
                    }
                    
                    //draw players
                    Player p = c.player(); //get the player in the cube
                    
                    if (p != null) {
                        if (p == player && p.item() instanceof Key) {
                        	if (high) Graphics.drawPlayerKeyHigh(v, ((Key) p.item()).color()); //draw the key the player is holding
                        	else Graphics.drawPlayerKeyLow(v, ((Key) p.item()).color());
                        }
                        if (p == player && p.item() instanceof Token) {
                        	if (high) Graphics.drawPlayerTokenHigh(v);
                        	else Graphics.drawPlayerTokenLow(v);
                        }
                        if (p != player && p.item() instanceof Key) {
                        	if (high) Graphics.drawOtherPlayerKey(v.copy().add(p.relPos), camPos, player.orientation(), ((Key) p.item()).color());
                        }
                        if (p != player && p.item() instanceof Token)
                        	if (high) Graphics.drawOtherPlayerToken(v, camPos.copy().add(p.relPos), player.orientation());
                        if (p.id != player.id) //only render the other players
                            playerRender.add(new Pair<Float3, Integer>(v.copy().add(p.relPos), p.id));
                    }
                    
                    //draw the objects in the cubes
                    for (GameObject obj : c.objects()) { //gets all the objects that the cube contains
                    
                    if (obj instanceof Door) {
	                        if (((Door) obj).isClosed()) {
	                            if (high) Graphics.drawDoorHigh(v, ((Door) obj).color(), 1.0f);
	                            else Graphics.drawDoorLow(v, ((Door) obj).color());
	                        }
	                        else {
	                            if (((Door) obj).getAnimate() != -1) Graphics.drawDoorHigh(v, ((Door) obj).color(), ((Door) obj).getAnimate());
	                            else if (obj instanceof EntranceDoor || obj instanceof ExitDoor) { //draw a portal
	                                if (high) Graphics.drawPortal(v, ((Door) obj).color()); 
	                            }
	                        }
	                    }
	                    else if (obj instanceof Key) {
	                    	if (high) Graphics.drawKeyHigh(v, ((Key) obj));
	                    	else Graphics.drawKeyLow(v, ((Key) obj));
	                    }
	                    else if (obj instanceof Button) {
	                    	if (high) Graphics.drawButtonHigh(v,((Button) obj).color());
	                    	else Graphics.drawButtonLow(v,((Button) obj).color());
	                    }
	                    else if(obj instanceof Lock) {
	                    	if (high) Graphics.drawLockHigh(v,((Lock) obj).color());
	                    	else Graphics.drawLockLow(v,((Lock) obj).color());
	                    }
	                    else if (obj instanceof LightSource && high) {
	                    	if (high) {
	                    		Graphics.drawLight(v);
	                    		flareRender.add(v.copy());
	                    	}
	                    }
	                    else if (obj instanceof Container) {
	                    	containerRender.add(new Pair<Float3, Container>(v, (Container) obj));
	                    	GameObject go = ((Container) obj).containers.get(((Container) obj).color()).getItem();
	                    	if (go instanceof Key) {
	                    		if (high) Graphics.drawKeyContainerHigh(v, (Key) go);
	                    		else Graphics.drawKeyContainerLow(v, (Key) go);
	                    	}
	                    	else if (go instanceof Token) {
	                    		if (high) Graphics.drawContainerTokenHigh(v);
	                    		else Graphics.drawContainerTokenLow(v);
	                    	}
	                    }
	                    else if (obj instanceof Token) {
	                    	if (high) Graphics.drawTokenHigh(v);
	                    	else Graphics.drawTokenLow(v);
	                    }
                    }
                }
            }
        }
        
        //draw the marker
        for (Player p : level.players.values()) {
            Int3 markerPos = p.markerPos; // TODO
            if (markerPos.x > -1) Graphics.drawMarker(p);
        }
            
        //draw some planets and star
        if (high) {
            Graphics.drawPlanet(new Float3(-13, -48, 17), new Float3(90, 0, 0), 8, 13);
            Graphics.drawPlanet(new Float3(76, -23, 64), new Float3(0, 45, 0), 10, 14);
            Graphics.drawPlanet(new Float3(12, 54, -76), new Float3(0, 0, 0), 12, 15);
            Graphics.drawPlanet(new Float3(-98, -16, 12), new Float3(0, 270, 0), 38, 16);
            Graphics.drawStar(new Float3(-95, 5, -19));
        }
        
        //draw the glass around the outside of the cube
        if (high) Graphics.drawOuterGlassHigh();
        
        //search flares
        for (Float3 p : flareRender) {
        	Graphics.drawLightFlare(p, camPos, player.orientation());
        }
        
        //for transparent objects
        while (!playerRender.isEmpty() || !glassRender.isEmpty() || !containerRender.isEmpty()) { //loop until all glass and players have been drawn
            Int3 playerPos = new Int3(player.pos().x*2, player.pos().y*2, player.pos().z*2);
            Pair<Float3, Integer> furthestPlayer = null; //the furthest player
            float playerMag = -1;
            Float3 furthestGlass = null; //the furthest glass
            float glassMag = -1;
            Pair<Float3, Container> furthestCon = null; //the furthest container
            float conMag = -1;
            Float3 furthestFlare = null;
            float flareMag = 0;
            
            //search players
            for (Pair<Float3, Integer> p : playerRender) {
                if (furthestPlayer == null) {
                    furthestPlayer = p;
                    playerMag = p.first().copy().sub(playerPos).mag();
                }
                else if (p.first().copy().sub(playerPos).mag() < playerMag) {
                    furthestPlayer = p;
                    playerMag = p.first().copy().sub(playerPos).mag();
                }
            }
            
            //search glass
            for (Float3 p : glassRender) {
                if (furthestGlass == null) {
                    furthestGlass = p;
                    glassMag = p.copy().sub(playerPos).mag();
                }
                else if (p.copy().sub(playerPos).mag() > glassMag) {
                    furthestGlass = p;
                    glassMag = p.copy().sub(playerPos).mag();
                }
            }
            
            //search containers
            for (Pair<Float3, Container> p : containerRender) {
                if (furthestCon == null) {
                    furthestCon = p;
                    conMag = p.first().copy().sub(playerPos).mag();
                }
                else if (p.first().copy().sub(playerPos).mag() < conMag) {
                    furthestCon = p;
                    conMag = p.first().copy().sub(playerPos).mag();
                }
            }
            
            //search flares
            for (Float3 p : flareRender) {
                if (furthestFlare == null) {
                    furthestFlare = p;
                    flareMag = p.copy().sub(playerPos).mag();
                }
                else if (p.copy().sub(playerPos).mag() > flareMag) {
                    furthestFlare = p;
                    flareMag = p.copy().sub(playerPos).mag();
                }
            }
            
            //now draw
            float[] mags = {playerMag, glassMag, conMag, flareMag};
            float maxMag = -1;
            float maxIndex = -1;
            for (int i = 0; i < mags.length; ++i) { //find the maximum magnitude
            	if (mags[i] > maxMag){
            		maxMag = mags[i];
            		maxIndex = i;
            	}
            }
            
            if (maxIndex == 0) {
            	if (high) Graphics.drawPlayer(furthestPlayer.first(), camPos, player.orientation(), furthestPlayer.second());
                playerRender.remove(furthestPlayer);
            }
            else if (maxIndex == 1) {
            	if (high) Graphics.drawGlassHigh(furthestGlass);
                else Graphics.drawGlassLow(furthestGlass);
                glassRender.remove(furthestGlass);
            }
            else if (maxIndex == 2) {
            	if (high) Graphics.drawContainerHigh(furthestCon.first(), furthestCon.second());
            	else Graphics.drawContainerLow(furthestCon.first(), furthestCon.second());
            	containerRender.remove(furthestCon);
            }
            else if (maxIndex == 3) {
            	if (high) Graphics.drawLightFlare(furthestFlare, camPos, player.orientation());
            	flareRender.remove(furthestFlare);
            }
        }

        
        //draw the pause box over the screen
        if(pause && high) Graphics.drawPause();
        
        gl.glFlush();
    }

    /**update the camera's position from the player's position*/
    private void updateCamera() {
        if (player == null) return;
        camPos.x = (player.pos().x*2)+player.relPos.x;
        camPos.y = (player.pos().y*2)+player.relPos.y;
        camPos.z = (player.pos().z*2)+player.relPos.z;
    }
    
    /**processes the player's actions like picking up and dropping items*/
    private void processAction() {
        if (eDown) { //pick up and object
            if (level.isValidAction(new ItemPickup(player.id))) {
                frame.getClient().push(new ItemPickup(player.id));
            }
        }
        if (fDown) { //drop an object into a square
            if (level.isValidAction(new ItemDrop(player.id))) { //check if drop is valid
                frame.getClient().push(new ItemDrop(player.id));
            }
        }
        if (mDown) {
            //if (player.pos().equals(markerPos)) markerPos = new Int3(-1, -1, -1);
            //else markerPos = new Int3(player.pos().x, player.pos().y, player.pos().z);
            // create a marker created event
            if (player.pos().equals(player.markerPos)) frame.getClient().push(new MarkerCreated(player.id(), new Int3(-1, -1, -1)));
            else frame.getClient().push(new MarkerCreated(player.id(),new Int3(player.pos().x, player.pos().y, player.pos().z)));
        }
        if (space && !spaceHeld) {
            if (level.isValidAction(new ItemUseStart(player.id))) { //check if drop is valid
                frame.getClient().push(new ItemUseStart(player.id));
                if (sound) { //play lock sound
                    Music lockSound = new Music();
                    lockSound.playSound("resources/audio/fx/lock.wav");
                }
            }
            spaceHeld = true;
        }
        if (spaceReleased) { //release space
            if (level.isValidAction(new ItemUseStop(player.id))) { //check if drop is valid
                frame.getClient().push(new ItemUseStop(player.id));
            }
        }
        eDown = false;
        fDown = false;
        space = false;
        spaceReleased = false;
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
                
                Graphics.bobItem();
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
            
                 if (player.relPos.x+newPos.x >=  1.0f) cubeMove = new Int3( 1,  0,  0);
            else if (player.relPos.x+newPos.x <= -1.0f) cubeMove = new Int3(-1,  0,  0);
            else if (player.relPos.y+newPos.y >=  1.0f) cubeMove = new Int3( 0,  1,  0);
            else if (player.relPos.y-newPos.y <= -1.0f) cubeMove = new Int3( 0, -1,  0);
            else if (player.relPos.z+newPos.z >=  1.0f) cubeMove = new Int3( 0,  0,  1);
            else if (player.relPos.z+newPos.z <= -1.0f) cubeMove = new Int3( 0,  0, -1);
            
            
            Int3 newCubePos = player.pos().add(cubeMove);
            canMove = level.isValidAction(new PlayerMove(player.id, newCubePos));
            
            if (canMove && !newPos.isZero()) {
                player.relPos.add(newPos);
                // send a small update to everyone
                frame.getClient().push(new PlayerRelPos(player.id, player.relPos));
                // note: we (kindof) applied this event straight away to reduce felt lag
            }
            if (canMove && !cubeMove.equals(Int3.ZERO)) {
                frame.getClient().push(new PlayerMove(player.id, newCubePos));
                
                // send the levelchange if we are on a EntranceDoor
                for (GameObject go : frame.getClient().getWorld().getCube(newCubePos).objects()) {
                    if (go instanceof EntranceDoor) {
                        EntranceDoor e = (EntranceDoor) go;
                        frame.getClient().push(new LevelChange(e.levelName()));
                    }
                    else if (go instanceof ExitDoor) {
                    	if(player.item() instanceof Token){
                    		ExitDoor e = (ExitDoor) go;
                    		frame.getClient().push(new LevelChange(e.levelName()));
                    	}
                    }
                }
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
        	if ((mouse.y-mouseCentre.y > 0 && rotation.x < 90) || 
            	(mouse.y-mouseCentre.y < 0 && rotation.x > -90))
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
                        if (leftMouse) {rotateTo.y += 90; rotateView.y -= 90; player.orientation(5); }
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
                        if (leftMouse) {rotateTo.z += 90; rotateView.y -= 90; player.orientation(2);}
                        else {rotateTo.z -= 90; rotateView.y += 90; player.orientation(3);}
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
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyDown = e.getKeyCode(); //get the key code
        if (keyDown == 87 && forBack == 0) forBack = 1; //w is down
        if (keyDown == 83 && forBack == 0) forBack = 2; //s is down
        if (keyDown == 65 && leftRight == 0) leftRight = 1; //a is down
        if (keyDown == 68 && leftRight == 0) leftRight = 2; //d is down
        if (keyDown == 81) quit = true; //q is down
        if (keyDown == 16) shift = true; //shift is down
        if (keyDown == 32) {
            space = true; //space is down
        }
        if (keyDown == 17) ctrl = true; //ctrl is down
        if (keyDown == 69) eDown = true; //the e key is down
        if (keyDown == 70) fDown = true; //the f key is down
        if (keyDown == 77) mDown = true;
        if (keyDown == 27) {
            pause = !pause; //pause or unpause the game
            frame.showMouse(pause);
            robot.mouseMove(mouseCentre.x, mouseCentre.y); //move the mouse to the centre of the window
        }
        if (keyDown != 32) {
            space = false;
            spaceHeld = false;
            spaceReleased = true;
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
        if (keyUp == 17) ctrl = false; //ctrl is released
        if (keyUp == 77) mDown = false;
        if (keyUp == 10) {
            if (sound) { //play chat sound
                Music chatSound = new Music();
                chatSound.playSound("resources/audio/fx/chat.wav");
            }
            if (regain) regain = false; //can now lose focus again
            else {
                regain = true; //has lost focus and will regain on return
                frame.getInputField().requestFocus(); // enter is released
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int button = e.getButton();
        if (sound && !pause && !rotationAni) { //play rotation sound
            Music rotateSound = new Music();
            rotateSound.playSound("resources/audio/fx/rotate.wav");
        }
        if (button == 1) leftMouse = true; //left mouse has been be released
        else if (button == 3) rightMouse = true; //right mouse has been released
    }
    
    /**Return the player
     * @return*/
    public Player player() {
        return player;
    }
    
    /**Return the gl
     * @return*/
    public GL2 gl() {
        return ogl;
    }

    public void keyTyped(KeyEvent e) {}
    public void dispose(GLAutoDrawable drawable) {}
    public void reshape(GLAutoDrawable drawable, int x1, int y1, int x2, int y2) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}