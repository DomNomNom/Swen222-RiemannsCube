package gui;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import utils.Float3;
import utils.Int2;
import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.cubes.Glass;
import world.cubes.Wall;
import world.objects.Player;

import client.Client;

public class Minimap extends GLJPanel implements GLEventListener {

	//FIELDS
	private GameFrame frame; //the window this is in
	private Int2 dimensions = new Int2(); //the windows dimensions
	private RiemannCube level; //the current level
	
	static GLU glu = new GLU(); //for GLU methods
	
	//CONSTRUCTOR
	public Minimap(GameFrame frame, int width, int height) {
		//addGLEventListener(this);
		this.frame = frame;
		dimensions.x = width;
		dimensions.y = height;
		level = frame.getClient().getWorld(); //get the level
	}
	
	//METHODS
	@Override
	public void init(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL.GL_DEPTH_TEST); // enable depth testing
        gl.glEnable(GL.GL_BLEND); // enable transparency
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA); // set the blending function
        gl.glShadeModel(GL2.GL_SMOOTH);
        
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // set the clear colour
        gl.glClearDepth(100.0f); // set the clear depth
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear the screen for the first time
        
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0f, dimensions.x/dimensions.y, 0.001f, 200.0f);

        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
		level = frame.getClient().getWorld(); //update the level
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); //clear the screen
		
		drawBackground(gl); //draw the background
		
		gl.glLoadIdentity(); //load the identity matrix
		
		gl.glRotatef(45, 1.0f, 0.0f, 0.0f); //rotate the map around x
		gl.glRotatef(-45, 0.0f, 1.0f, 0.0f); //rotate the map around y
		
		gl.glTranslatef(-0.82f, -1.1f, 0.55f); //translate back into a viewable position
		
		drawOutline(gl);
		drawFloor(gl);
	}
	
	/**Draws the outline of the cube in the minimap
	 * @param gl*/
	public void drawOutline(GL2 gl) {
		//Draw the front
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f( 0.38f,  0.38f, -1.0f);
		gl.glVertex3f(-0.38f,  0.38f, -1.0f);
		gl.glVertex3f(-0.38f, -0.38f, -1.0f);
		gl.glVertex3f( 0.38f, -0.38f, -1.0f);
		gl.glEnd();
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f( 0.38f,  0.38f, -1.76f);
		gl.glVertex3f(-0.38f,  0.38f, -1.76f);
		gl.glVertex3f(-0.38f, -0.38f, -1.76f);
		gl.glVertex3f( 0.38f, -0.38f, -1.76f);
		gl.glEnd();
		//Draw the sides
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(-0.38f,  0.38f, -1.00f);
		gl.glVertex3f(-0.38f,  0.38f, -1.76f);
		gl.glVertex3f(-0.38f, -0.38f, -1.76f);
		gl.glVertex3f(-0.38f, -0.38f, -1.00f);
		gl.glEnd();	
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f(0.38f,  0.38f, -1.00f);
		gl.glVertex3f(0.38f,  0.38f, -1.76f);
		gl.glVertex3f(0.38f, -0.38f, -1.76f);
		gl.glVertex3f(0.38f, -0.38f, -1.00f);
		gl.glEnd();
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f( 0.38f, -0.38f, -1.00f);
		gl.glVertex3f( 0.38f, -0.38f, -1.76f);
		gl.glVertex3f(-0.38f, -0.38f, -1.76f);
		gl.glVertex3f(-0.38f, -0.38f, -1.00f);
		gl.glEnd();
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex3f( 0.38f, 0.38f, -1.00f);
		gl.glVertex3f( 0.38f, 0.38f, -1.76f);
		gl.glVertex3f(-0.38f, 0.38f, -1.76f);
		gl.glVertex3f(-0.38f, 0.38f, -1.00f);
		gl.glEnd();
	}
	
	/**Draws the player's floor as a map*/
	public void drawFloor(GL2 gl) {
		Player player = frame.getClient().player();
		Float3 rot = player.rotation; //get the player's rotation
		Float3 pos = new Float3();
		pos.x = frame.getClient().player().pos().x;
		pos.y = frame.getClient().player().pos().y;
		pos.z = frame.getClient().player().pos().z;
		float cubeSize = (float) (0.76f/level.size.x);
		
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		if (player.orientation() == 2 || player.orientation() == 3) { //draw wall type 2
			float x = -0.38f+(0.76f/(level.size.x/pos.x));
			
			for (float z = 0.0f; z < level.size.z; ++z) {
				float dz = -1.76f+(0.76f*(z/level.size.z));
				for (float y = 0.0f; y < level.size.y; ++y) {
					float dy = -0.38f+(0.76f*(y/level.size.y));
					
					//set the colour based on the type of cube that is in the tile
					Cube c = level.getCube(new Int3((int) pos.x, (int) y, (int) z));
					
					if (c instanceof Wall) gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
					else if (c instanceof Glass) gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
					else gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
					
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3f(x, dy+cubeSize, dz+cubeSize);
					gl.glVertex3f(x, dy+cubeSize, dz);
					gl.glVertex3f(x, dy,          dz);
					gl.glVertex3f(x, dy, 	      dz+cubeSize);
					gl.glEnd();
					
					//draw players
					Player p = c.player();
					if (p != null) {
						float half = cubeSize/2.0f;
						
						
						if (p.id == 0) gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
						else if (p.id == 1) gl.glColor4f(0.6f, 0.0f, 0.6f, 1.0f);
						else if (p.id == 2) gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
						else if (p.id == 3) gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
						
						//draw player square
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3f(x+0.015f, dy+cubeSize, dz+cubeSize);
						gl.glVertex3f(x+0.015f, dy+cubeSize, dz+half);
						gl.glVertex3f(x+0.015f, dy+half, 	dz+half);
						gl.glVertex3f(x+0.015f, dy+half,     dz+cubeSize);
						gl.glEnd();
						
						//draw shadow
						gl.glColor4f(0.0f, 0.0f, 0.0f, 0.75f);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3f(x+0.01f, dy+cubeSize, dz+cubeSize);
						gl.glVertex3f(x+0.01f, dy+cubeSize, dz+half);
						gl.glVertex3f(x+0.01f, dy+half, 	dz+half);
						gl.glVertex3f(x+0.01f, dy+half,     dz+cubeSize);
						gl.glEnd();
					}
				}
			}
		}
		else if (player.orientation() == 0 || player.orientation() == 1) { //draw a floor layer
			float y = -0.38f+(0.76f/(level.size.y/pos.y));
			
			for (float z = 0.0f; z < level.size.z; ++z) {
				float dz = -1.76f+(0.76f*(z/level.size.z));
				for (float x = 0.0f; x < level.size.x; ++x) {
					float dx = -0.38f+(0.76f*(x/level.size.x));
					
					//set the colour based on the type of cube that is in the tile
					Cube c = level.getCube(new Int3((int) x, (int) pos.y, (int) z));
					
					if (c instanceof Wall) gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
					else if (c instanceof Glass) gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
					else gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
					
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3f(dx+cubeSize, y, dz+cubeSize);
					gl.glVertex3f(dx+cubeSize, y, dz);
					gl.glVertex3f(dx,          y, dz);
					gl.glVertex3f(dx,          y, dz+cubeSize);
					gl.glEnd();
					
					//draw players
					Player p = c.player();
					if (p != null) {
						float half = cubeSize/2.0f;
						
						
						if (p.id == 0) gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
						else if (p.id == 1) gl.glColor4f(0.6f, 0.0f, 0.6f, 1.0f);
						else if (p.id == 2) gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
						else if (p.id == 3) gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
						
						//draw player square
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3f(dx+cubeSize, y+0.015f, dz+cubeSize);
						gl.glVertex3f(dx+cubeSize, y+0.015f, dz+half);
						gl.glVertex3f(dx+half,     y+0.015f, dz+half);
						gl.glVertex3f(dx+half,     y+0.015f, dz+cubeSize);
						gl.glEnd();
						
						//draw shadow
						gl.glColor4f(0.0f, 0.0f, 0.0f, 0.75f);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3f(dx+cubeSize, y+0.01f, dz+cubeSize);
						gl.glVertex3f(dx+cubeSize, y+0.01f, dz+half);
						gl.glVertex3f(dx+half,     y+0.01f, dz+half);
						gl.glVertex3f(dx+half,     y+0.01f, dz+cubeSize);
						gl.glEnd();
					}
				}
			}
		}
		else if (player.orientation() == 4 || player.orientation() == 5) { //draw wall type one layer
			float z = -1.76f+(0.76f/(level.size.z/pos.z));
			
			for (float y = 0.0f; y < level.size.y; ++y) {
				float dy = -0.38f+(0.76f*(y/level.size.y));
				for (float x = 0.0f; x < level.size.x; ++x) {
					float dx = -0.38f+(0.76f*(x/level.size.x));
					
					//set the colour based on the type of cube that is in the tile
					Cube c = level.getCube(new Int3((int) x, (int) y, (int) pos.z));
					
					if (c instanceof Wall) gl.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
					else if (c instanceof Glass) gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
					else gl.glColor4f(0.0f, 1.0f, 1.0f, 1.0f);
					
					gl.glBegin(GL2.GL_QUADS);
					gl.glVertex3f(dx+cubeSize, dy+cubeSize, z);
					gl.glVertex3f(dx+cubeSize, dy,          z);
					gl.glVertex3f(dx,          dy,          z);
					gl.glVertex3f(dx,          dy+cubeSize, z);
					gl.glEnd();
					gl.glColor4d(0.0f, 1.0f, 0.0f, 1.0f);
					
					//draw players
					Player p = c.player();
					if (p != null) {
						float half = cubeSize/2.0f;
						
						
						if (p.id == 0) gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
						else if (p.id == 1) gl.glColor4f(0.6f, 0.0f, 0.6f, 1.0f);
						else if (p.id == 2) gl.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
						else if (p.id == 3) gl.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
						
						//draw player square
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3f(dx+cubeSize, dy+cubeSize, z+0.015f);
						gl.glVertex3f(dx+cubeSize, dy+half,     z+0.015f);
						gl.glVertex3f(dx+half,     dy+half,     z+0.015f);
						gl.glVertex3f(dx+half,     dy+cubeSize, z+0.015f);
						gl.glEnd();
						
						//draw shadow
						gl.glColor4f(0.0f, 0.0f, 0.0f, 0.75f);
						gl.glBegin(GL2.GL_QUADS);
						gl.glVertex3f(dx+cubeSize, dy+cubeSize, z+0.001f);
						gl.glVertex3f(dx+cubeSize, dy+half,     z+0.001f);
						gl.glVertex3f(dx+half,     dy+half,     z+0.001f);
						gl.glVertex3f(dx+half,     dy+cubeSize, z+0.001f);
						gl.glEnd();
						
					}
				}
			}
		}
	}
	
	/**Draw the minimap's background
	 * @param gl*/
	public void drawBackground(GL2 gl) {
		gl.glDisable(GL.GL_DEPTH_TEST); //disable depth testing
		gl.glLoadIdentity(); //load the identity matrix
		
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f); gl.glVertex3f( 0.42f,  0.42f, -1.0f);
		gl.glColor4f(0.8f, 0.8f, 0.8f, 1.0f); gl.glVertex3f( 0.42f, -0.42f, -1.0f);
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f); gl.glVertex3f(-0.42f, -0.42f, -1.0f);
		gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f); gl.glVertex3f(-0.42f,  0.42f, -1.0f);
		gl.glEnd();
		
		gl.glEnable(GL.GL_DEPTH_TEST);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {}
	@Override
	public void reshape(GLAutoDrawable drawable, int x1, int y1, int x2, int y2) {}
	
}