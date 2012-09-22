package gui;



import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

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
    private boolean high; //is true if the game is running in high graphics
    
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
    
    private float lastX = 0.0f; //the x position from the previous frame
    private float lastY = 0.0f; //the y position from the previous frame
    private float lastZ = 0.0f; //the z position from the previous frame
    
    private float yRotation = 0.0f; //the y rotation of the camera
    private float zRotation = 0.0f; //the z rotation of the camera
    
    private float moveSpeed = 0.04f; //the move speed of the camera
    private float turnSpeed = 4.0f; //the turn speed of the camera
    
    //For stepping movement
    private float stepCycle = 0.0f; //where the camera is in the step
    private float stepHeight = 0.015f;

    static GLU glu = new GLU(); //for glu methods
    private Robot robot; //a robot that insures the mouse is always in the centre of the screen
    public static Animator animator; // the animator makes sure the canvas is always being updated
    
    //TODO: move these into a resources class
    private int[] texID = new int[4]; //where the texture ids are stored
    private ByteBuffer floorTex; //holds the floor texture
    private ByteBuffer wallTex; //holds the wall texture
    private ByteBuffer glassTex; //holds the glass texture
    private ByteBuffer skyTex; //holds the sky texture
    
    private int texRender_FBO;
    private int texRender_RB;
    private int texRender_32x32;
    

    // CONSTRUCTOR
    /** Creates a new view port
     * @param width the width of the view port
     * @param height the height of the view port
     * @param chat a reference to the chat panel
     */
    public ViewPort(int width, int height, boolean high) {
        addGLEventListener(this);
        this.width = width;
        this.height = height;
        this.high = high;
        mouseX = MouseInfo.getPointerInfo().getLocation().getX();
        mouseY = MouseInfo.getPointerInfo().getLocation().getY();
        addKeyListener(this);
        try {
        	robot = new Robot(); //make a new robot
        } catch (Exception e) {}
        robot.mouseMove(mouseXCentre, mouseYCentre); //move the mouse to the centre of the window
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
        
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // set the clear colour
        gl.glClearAccum(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearDepth(100.0f); // set the clear depth
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear the screen for the first time

        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0f, width/height, 0.001f, 200.0f);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        //TODO: move this into resources as well
        //Load Textures
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        
       BufferedImage floorImg = null;
        try {
			floorImg = ImageIO.read(new File("resources/gfx/floor1.png")); //open the image
			floorTex = convertImageData(floorImg); //converts the image
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        BufferedImage wallImg = null;
        try {
			wallImg = ImageIO.read(new File("resources/gfx/wall1.png")); //open the image
			wallTex = convertImageData(wallImg); //converts the image
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        BufferedImage glassImg = null;
        try {
			glassImg = ImageIO.read(new File("resources/gfx/glass.png")); //open the image
			glassTex = convertImageData(glassImg); //converts the image
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        BufferedImage skyImg = null;
        try {
			skyImg = ImageIO.read(new File("resources/gfx/skyPlane.png")); //open the image
			skyTex = convertImageData(skyImg); //converts the image
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        gl.glGenTextures(texID.length, texID, 0); //create texture names
        
        //now actually create the textures
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 350,
            350, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, floorTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 350,
            350, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, wallTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[2]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 350,
            350, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, glassTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[3]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 300,
            300, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, skyTex);
        
        gl.glEnable(GL.GL_TEXTURE_2D); //enable 2d textures
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        
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
        
        //calculate movement changes here
        float dx = xPos-lastX;
        float dy = yPos-lastY;
        float dz = zPos-lastZ;
        
        //save the last positions
        lastX = xPos;
        lastY = yPos;
        lastZ = zPos;
        
        //START DRAWING HERE
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        gl.glLoadIdentity();
        
        gl.glRotatef(yRotation, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(zRotation, 0.0f, 1.0f, 0.0f);
        
        gl.glTranslatef(xPos, yPos, zPos);
    
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[3]); //bind the sky image
        
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 7.0f); gl.glVertex3f(-100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(7.0f, 7.0f); gl.glVertex3f(-100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(7.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, 100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(100.0f, -100.0f, 100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f, -100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, 100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, -100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f, 100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, -100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, -100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(-100.0f, -100.0f, 100.0f);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(0.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, -100.0f);
        gl.glTexCoord2f(5.0f, 5.0f); gl.glVertex3f(100.0f, 100.0f, 100.0f);
        gl.glTexCoord2f(5.0f, 0.0f); gl.glVertex3f(-100.0f, 100.0f, 100.0f);
        gl.glEnd();
        
        // floor tiles
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]); //bind the floor tile image
        
        for (int i = -8; i < 1; ++i) {
            for (int j = -6; j < 6; ++j) {
                gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(j, -1.0f, i);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(j, -1.0f, i + 1);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(j + 1, -1.0f, i + 1);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(j + 1, -1.0f, i);
                gl.glEnd();
            }
        }
        // roof tiles
        for (int i = -8; i < 1; ++i) {
            for (int j = -6; j < 6; ++j) {
                gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(j, 1.0f, i);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(j, 1.0f, i + 1);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(j + 1, 1.0f, i + 1);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(j + 1, 1.0f, i);
                gl.glEnd();
            }
        }
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]); //bind the wall tile image
        // side wall tiles
        for (int i = -8; i < 1; ++i) {
            gl.glBegin(GL2.GL_QUADS);
            if (i != -4 && i != -3) {
	            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-6, 0.0f, i);
	            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-6, -1.0f, i);
	            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-6, -1.0f, i + 1);
	            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-6, 0.0f, i + 1);
	            gl.glEnd();
	            gl.glBegin(GL2.GL_QUADS);
	            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-6, 1.0f, i);
	            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-6, 0.0f, i);
	            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-6, 0.0f, i + 1);
	            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-6, 1.0f, i + 1);
	            gl.glEnd();
            }
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(6, 0.0f, i);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(6, -1.0f, i);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(6, -1.0f, i + 1);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(6, 0.0f, i + 1);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(6, 1.0f, i);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(6, 0.0f, i);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(6, 0.0f, i + 1);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(6, 1.0f, i + 1);
            gl.glEnd();
        }
        // front and back wall tiles
        for (int i = -6; i < 6; ++i) {
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(i, 0.0f, -8);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(i, -1.0f, -8);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(i + 1, -1.0f, -8);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(i + 1, 0.0f, -8);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(i, 1.0f, -8);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(i, 0.0f, -8);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(i + 1, 0.0f, -8);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(i + 1, 1.0f, -8);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(i, 0.0f, 1);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(i, -1.0f, 1);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(i + 1, -1.0f, 1);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(i + 1, 0.0f, 1);
            gl.glEnd();
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(i, 1.0f, 1);
            gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(i, 0.0f, 1);
            gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(i + 1, 0.0f, 1);
            gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(i + 1, 1.0f, 1);
            gl.glEnd();
        }
        // create middle wall
        for (int i = -6; i < 6; ++i) {
            if (i != -1 && i != 0) {
                gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(i, 0.0f, -6);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(i, -1.0f, -6);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(i + 1, -1.0f, -6);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(i + 1, 0.0f, -6);
                gl.glEnd();
                gl.glBegin(GL2.GL_QUADS);
                gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(i, 1.0f, -6);
                gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(i, 0.0f, -6);
                gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(i + 1, 0.0f, -6);
                gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(i + 1, 1.0f, -6);
                gl.glEnd();
            }
        }
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[2]); //bind the glass tile image
        // now create glass
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1, 0.0f, -6);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1, -1.0f, -6);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0, -1.0f, -6);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0, 0.0f, -6);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-1, 1.0f, -6);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-1, 0.0f, -6);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(0, 0.0f, -6);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(0, 1.0f, -6);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0, 0.0f, -6);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, -1.0f, -6);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1, -1.0f, -6);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1, 0.0f, -6);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(0, 1.0f, -6);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(0, 0.0f, -6);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(1, 0.0f, -6);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(1, 1.0f, -6);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-6, 0.0f, -3);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-6, -1.0f, -3);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-6, -1.0f, -2);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-6, 0.0f, -2);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-6, 1.0f, -3);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-6, 0.0f, -3);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-6, 0.0f, -2);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-6, 1.0f, -2);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-6, 0.0f, -4);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-6, -1.0f, -4);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-6, -1.0f, -3);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-6, 0.0f, -3);
        gl.glEnd();
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-6, 1.0f, -4);
        gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-6, 0.0f, -4);
        gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-6, 0.0f, -3);
        gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-6, 1.0f, -3);
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
	
	/**Converts a buffered image to an array of byte buffer
	 * @param bufferedImage the buffered image to convert
	 * @return the new byte buffer
	*/
	private ByteBuffer convertImageData(BufferedImage bufferedImage) {
	    ByteBuffer imageBuffer;
	    WritableRaster raster;
	    BufferedImage texImage;

	    ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace
	            .getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 },
	            true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);

	    raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
	            bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
	    texImage = new BufferedImage(glAlphaColorModel, raster, true,
	            new Hashtable());

	    //copy the source image into the produced image
	    Graphics g = texImage.getGraphics();
	    g.setColor(new Color(0f, 0f, 0f, 0f));
	    g.fillRect(0, 0, 256, 256);
	    g.drawImage(bufferedImage, 0, 0, null);

	    // build a byte buffer from the temporary image
	    byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
	    imageBuffer = ByteBuffer.allocateDirect(data.length);
	    imageBuffer.order(ByteOrder.nativeOrder());
	    imageBuffer.put(data, 0, data.length);
	    imageBuffer.flip();

	    return imageBuffer;
	}

	public void keyTyped(KeyEvent e) {}
    public void dispose(GLAutoDrawable drawable) {}
    public void reshape(GLAutoDrawable drawable, int x1, int y1, int x2, int y2) {}
}