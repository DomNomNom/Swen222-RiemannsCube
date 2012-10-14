package gui;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import utils.Float2;
import utils.Float3;
import world.RiemannCube;
import world.objects.Player;
import world.objects.items.Key;

/** Graphics controls all rendering onto the view port
 * It consists of a set of static methods that can be
 * used for drawing different objects.
 * 
 * @author David Saxon 300199370*/
public class Graphics {
	
	//FIELDS
	private static GL2 gl = null; //openGL
	private static boolean high = false; //is true if on high graphics mode
	private static Resources resources = null; //a reference to the game resources
	private static RiemannCube level = null; //a reference to the level
	private static Player player = null; //a reference to the player
	private static int portalRot = 0; //the rotation of the portals
	private static float planetRot = 0.0f; //the rotation of the planets
	
	//METHODS
    /**Draws a openGL textured quad
     * @param v the position vector of the cube
     * @param n the normal vector of the quad*/
    public static void drawQuadTex(Float3 v, Float3 n, boolean inside) {
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
    
    /**Draws a openGL scaled textured quad
     * @param v the position vector of the cube
     * @param n the normal vector of the quad
     * @param scale the scale*/
    public static void drawQuadTexScaled(Float3 v, Float3 n, boolean inside, float scale) {
    	//find the difference between the point and the normal
    	Float3 dv = new Float3((n.x-v.x), (n.y-v.y), (n.z-v.z));
    	
    	//Find the rotation amounts
    	float xRot = Math.abs(dv.y)*(90.0f+dv.y*90.0f);
    	float yRot = Math.abs(dv.z)*(dv.z*90.0f);
    	float zRot = Math.abs(dv.x)*(dv.x*90.0f);

    	gl.glPushMatrix(); //push new matrix
    	
    	gl.glTranslatef(v.x, v.y, v.z); //translate world to position
    	
    	//apply scale
    	gl.glScalef(scale, scale, scale);
    	
    	//apply the world orientation rotation
    	gl.glRotatef(player.rotation.y, 0.0f, 1.0f, 0.0f);
    	gl.glRotatef(player.rotation.x, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(player.rotation.z, 0.0f, 0.0f, 1.0f);
    	
    	gl.glRotatef(scale*90.0f+270.0f, 0.0f, 1.0f, 0.0f);
    	gl.glRotatef(scale*90.0f+270.0f, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(scale*90.0f+270.0f, 0.0f, 0.0f, 1.0f);
    	
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
     * @param v the position vector of the cube
     * @param n the normal vector of the quad
     * @param col = the rgb colour of the quad
     * @param a the alpha value of the colour of the quad*/
    public static void drawQuadCol(Float3 v, Float3 n, boolean inside, Float3 col, float a) {
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
        
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
	/**Draws a player that is always facing the camera
	 * @param v the position vector of the player
	 * @param playerId the player to be drawn id*/
    public static void drawPlayer(Float3 v, Float3 camPos, int playerId) {
    	//TODO: fix for other rotations
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
    
    /**Draw a floor cube in high graphics
     * @param v the position vector of the cube*/
    public static void drawFloorHigh(Float3 v) {
	    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
	    	Graphics.drawQuadTex(v, new Float3(v.x, v.y-1, v.z), true);
	    	Graphics.drawQuadTex(v, new Float3(v.x, v.y+1, v.z), true);
    }
	
    /**Draw a wall cube in high graphics
     * @param v the position vector of the cube*/
    public static void drawWallHigh(Float3 v) {
    	//draw the 4 walls
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[1]); //bind the wall tile texture
		Graphics.drawQuadTex(v, new Float3(v.x-1, v.y, v.z  ), false);
		Graphics.drawQuadTex(v, new Float3(v.x+1, v.y, v.z  ), false);
		Graphics.drawQuadTex(v, new Float3(v.x,   v.y, v.z+1), false);
		Graphics.drawQuadTex(v, new Float3(v.x,   v.y, v.z-1), false);
		//draw the floor
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[0]); //bind the floor tile texture
		Graphics.drawQuadTex(v, new Float3(v.x, v.y-1, v.z), false);
		Graphics.drawQuadTex(v, new Float3(v.x, v.y+1, v.z), false);
    }
    
    /**Draw a glass cube in high graphics
     * @param v the position vector of the cube*/
    public static void drawGlassHigh(Float3 v) {
    	//draw the 4 walls
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[2]); //bind the glass texture
		Graphics.drawQuadTex(v, new Float3(v.x-1, v.y, v.z), true);
		Graphics.drawQuadTex(v, new Float3(v.x+1, v.y, v.z), true);
		Graphics.drawQuadTex(v, new Float3(v.x, v.y, v.z+1), true);
		Graphics.drawQuadTex(v, new Float3(v.x, v.y, v.z-1), true);
		Graphics.drawQuadTex(v, new Float3(v.x-1, v.y, v.z), false);
		Graphics.drawQuadTex(v, new Float3(v.x+1, v.y, v.z), false);
		Graphics.drawQuadTex(v, new Float3(v.x, v.y, v.z+1), false);
		Graphics.drawQuadTex(v, new Float3(v.x, v.y, v.z-1), false);
    }
    
    /**Draw a floor cube in low graphics
     * @param v the position vector of the cube*/
    public static void drawFloorLow(Float3 v) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
		Float3 colour = new Float3(0.0f, 1.0f, 1.0f); //set the colour of the floor
		Graphics.drawQuadCol(v, new Float3(v.x, v.y-1, v.z), true, colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y+1, v.z), true, colour, 1.0f);
    }
    
    /**Draw a wall cube in low graphics
     * @param v the position vector of the cube*/
    public static void drawWallLow(GL2 gl, Float3 v) {
    	//draw the 4 walls
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	Float3 colour = new Float3(1.0f, 0.0f, 1.0f); //set the colour of the wall
    	Graphics.drawQuadCol(v, new Float3(v.x-1, v.y, v.z  ), false, colour, 1.0f);
    	Graphics.drawQuadCol(v, new Float3(v.x+1, v.y, v.z  ), false, colour, 1.0f);
    	Graphics.drawQuadCol(v, new Float3(v.x,   v.y, v.z+1), false, colour, 1.0f);
    	Graphics.drawQuadCol(v, new Float3(v.x,   v.y, v.z-1), false, colour, 1.0f);
		//draw the floor
		colour = new Float3(0.0f, 1.0f, 1.0f); //set the colour of the floor
		Graphics.drawQuadCol(v, new Float3(v.x, v.y-1, v.z), false, colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y+1, v.z), false, colour, 1.0f);
    }
    
    /**Draw a wall cube in low graphics
     * @param v the position vector of the cube*/
    public static void drawWallLow(Float3 v) {
    	//draw the 4 walls
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	Float3 colour = new Float3(1.0f, 0.0f, 1.0f); //set the colour of the wall
    	Graphics.drawQuadCol(v, new Float3(v.x-1, v.y, v.z  ), false, colour, 1.0f);
    	Graphics.drawQuadCol(v, new Float3(v.x+1, v.y, v.z  ), false, colour, 1.0f);
    	Graphics.drawQuadCol(v, new Float3(v.x,   v.y, v.z+1), false, colour, 1.0f);
    	Graphics.drawQuadCol(v, new Float3(v.x,   v.y, v.z-1), false, colour, 1.0f);
		//draw the floor
		colour = new Float3(0.0f, 1.0f, 1.0f); //set the colour of the floor
		Graphics.drawQuadCol(v, new Float3(v.x, v.y-1, v.z), false, colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y+1, v.z), false, colour, 1.0f);
    }
    
    /**Draw a glass cube in low graphics
     * @param v the position vector of the cube*/
    public static void drawGlassLow(Float3 v) {
    	//draw the floor
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
		Float3 colour = new Float3(0.0f, 1.0f, 1.0f); //set the colour of the floor
		Graphics.drawQuadCol(v, new Float3(v.x, v.y-1, v.z), true,  colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y+1, v.z), true,  colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y-1, v.z), false, colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y+1, v.z), false, colour, 1.0f);
    	//draw the 4 walls
		colour = new Float3(1.0f, 1.0f, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x-1, v.y, v.z), true,  colour, 0.4f);
		Graphics.drawQuadCol(v, new Float3(v.x+1, v.y, v.z), true,  colour, 0.4f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y, v.z+1), true,  colour, 0.4f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y, v.z-1), true,  colour, 0.4f);
		Graphics.drawQuadCol(v, new Float3(v.x-1, v.y, v.z), false, colour, 0.4f);
		Graphics.drawQuadCol(v, new Float3(v.x+1, v.y, v.z), false, colour, 0.4f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y, v.z+1), false, colour, 0.4f);
		Graphics.drawQuadCol(v, new Float3(v.x, v.y, v.z-1), false, colour, 0.4f);
    }
    
    /**Draw a door object in high graphics
     * @param v the position vector of the door
     * @param col the colour of the door*/
    public static void drawDoorHigh(Float3 v, Color col, float scale) {
    	//draw the door block
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[11]); //bind the door texture
		gl.glColor4f(col.getRed()/255.0f, col.getGreen()/255.0f, col.getBlue()/255.0f, 1.0f);
		
		gl.glPushMatrix();
		
		Graphics.drawQuadTexScaled(v, new Float3(v.x-1, v.y, v.z  ), false, scale);
		Graphics.drawQuadTexScaled(v, new Float3(v.x+1, v.y, v.z  ), false, scale);
		Graphics.drawQuadTexScaled(v, new Float3(v.x,   v.y, v.z+1), false, scale);
		Graphics.drawQuadTexScaled(v, new Float3(v.x,   v.y, v.z-1), false, scale);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**Draw a door object in high graphics
     * @param v the position vector of the door
     * @param col the colour of the door*/ 
    public static void drawDoorLow(Float3 v, Color col) {
    	//draw the door block
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
		Float3 colour = new Float3(col.getRed()/255.0f, col.getGreen()/255.0f, col.getBlue()/255.0f);
		Graphics.drawQuadCol(v, new Float3(v.x-1, v.y, v.z  ), false, colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x+1, v.y, v.z  ), false, colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x,   v.y, v.z+1), false, colour, 1.0f);
		Graphics.drawQuadCol(v, new Float3(v.x,   v.y, v.z-1), false, colour, 1.0f);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }
    
    /**Draw a portal
    @param v the position vector of the portal*/
    public static void drawPortal(Float3 v) {
    	gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[12]); //bind the portal texture
    	
    	//rotate the texture
    	gl.glMatrixMode(GL.GL_TEXTURE);
    	gl.glPushMatrix();
    	gl.glLoadIdentity();
    	gl.glTranslatef(0.5f, 0.5f, 0.0f); //translate to origin
    	gl.glRotatef(portalRot, 0.0f, 0.0f, 1.0f); //rotate texture
    	gl.glTranslatef(-0.5f, -0.5f, 0.0f); //translate back
    	gl.glMatrixMode(GL2.GL_MODELVIEW); //switch back to model view
    	 
 		Graphics.drawQuadTex(v, new Float3(v.x-1, v.y, v.z  ), false);
 		Graphics.drawQuadTex(v, new Float3(v.x+1, v.y, v.z  ), false);
 		Graphics.drawQuadTex(v, new Float3(v.x,   v.y, v.z+1), false);
 		Graphics.drawQuadTex(v, new Float3(v.x,   v.y, v.z-1), false);
 		
 		gl.glMatrixMode(GL.GL_TEXTURE);
 		gl.glPopMatrix();
 		gl.glMatrixMode(GL2.GL_MODELVIEW); //switch back to model view
    }
    
    public static void drawKey(Float3 v, Key k) {
    	gl.glPushMatrix(); //push new matrix
    	
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
    	
    	gl.glTranslatef(v.x, v.y, v.z); //translate world to position
    	
    	//apply the world orientation rotation
    	gl.glRotatef(player.rotation.y, 0.0f, 1.0f, 0.0f);
    	gl.glRotatef(player.rotation.x, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(player.rotation.z, 0.0f, 0.0f, 1.0f);
    	
    	gl.glRotatef(k.getRotate(), 0, 1, 0);
    	gl.glTranslatef(0.0f, -0.5f, 0.0f);
    	
    	gl.glColor4f(k.color().getRed()/255.0f, k.color().getGreen()/255.0f, k.color().getBlue()/255.0f, 1.0f);
    	resources.getObj("key").render(gl);
    	gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    	
    	gl.glPopMatrix();
    }
    
    /**TODO: fix this*/
    public static void drawPlayerKey(Float3 v, Color col) {
    	gl.glPushMatrix(); //push new matrix
    	
       	gl.glLoadIdentity(); //load the identity matrix
       	
       	gl.glTranslatef(-0.07f, -0.15f, -0.5f);
       	gl.glRotatef(75, 0, 1, 0);
       	
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	gl.glColor4f(col.getRed()/255.0f, col.getGreen()/255.0f, col.getBlue()/255.0f, 1.0f);
    	resources.getObj("key").render(gl);
    	gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    	
    	gl.glPopMatrix();
    }
    
    /**TODO: fix this*/
    public static void drawButton(Float3 v, Color col) {
    	gl.glPushMatrix();
    	
    	gl.glTranslatef(v.x, v.y, v.z); //translate world to position
    	
    	//apply the world orientation rotation
    	gl.glRotatef(player.rotation.y, 0.0f, 1.0f, 0.0f);
    	gl.glRotatef(player.rotation.x, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(player.rotation.z, 0.0f, 0.0f, 1.0f);
    	
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	gl.glColor4f(0.9f, 0.9f, 0.9f, 1.0f);
    	
    	resources.getObj("buttonBase").render(gl); //draw the base
    	
    	gl.glColor4f(col.getRed()/255.0f, col.getGreen()/255.0f, col.getBlue()/255.0f, 1.0f);
    	
    	resources.getObj("button").render(gl);
        
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        
        gl.glPopMatrix();
    }
    
    /**TODO: fix this*/
    public static void drawLock(Float3 v, Color col) {
    	gl.glPushMatrix();
    	
    	gl.glTranslatef(v.x, v.y, v.z); //translate world to position
    	
    	//apply the world orientation rotation
    	gl.glRotatef(player.rotation.y, 0.0f, 1.0f, 0.0f);
    	gl.glRotatef(player.rotation.x, 1.0f, 0.0f, 0.0f);
    	gl.glRotatef(player.rotation.z, 0.0f, 0.0f, 1.0f);
    	
    	gl.glTranslatef(0, -1.0f, 0);
    	
    	gl.glBindTexture(GL.GL_TEXTURE_2D, 0); //unbind textures
    	gl.glColor4f(0.1f, 0.1f, 0.1f, 1.0f);
    	
    	resources.getObj("buttonBase").render(gl); //draw the base
    	
    	gl.glColor4f(col.getRed()/255.0f, col.getGreen()/255.0f, col.getBlue()/255.0f, 1.0f);
    	
    	resources.getObj("button").render(gl);
        
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        
        gl.glPopMatrix();
    }
    
    /**Draws an outer box of glass around the level in high graphics*/
    public static void drawOuterGlassHigh() {
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
    
	/**Draws the space box in high graphics*/
    public static void drawSpaceBoxHigh() {
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
    
    /**Draws the pause screen overlay*/
    public static void drawPause() {
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
    
    /**Draws a planet
     * @param v the position vector of the planet
     * @param rot the initial rotation of the planet
     * @param radius the radius of the planet
     * @param texID the planet's texture id
     */
	public static void drawPlanet(Float3 v, Float3 rot, int radius, int texID) {
		gl.glPushMatrix();
		gl.glTranslatef(v.x, v.y,v.z);
		gl.glRotatef(rot.x, 1, 0, 0);
		gl.glRotatef(rot.y, 0, 1, 0);
		gl.glRotatef(rot.z, 0, 0, 1);
		gl.glRotatef(planetRot, 0, 1, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, resources.getIDs()[texID]);
		Graphics.drawSphere(radius, 50, 50);
		gl.glPopMatrix();
	}
    
	/**Draws a sphere
	 * @param r the radius of the sphere
	 * @param lats the number of latitude polygons
	 * @param longs the number of longitude polygons
	 */
    public static void drawSphere(float r, int lats, int longs) {
		for(int i = 1; i <= lats; i++) {
			float lat0 = (float) (Math.PI*(-0.5f+(float)(i-1)/lats));
			float z0  = (float) Math.sin(lat0);
			float zr0 =  (float) Math.cos(lat0);
			
			float lat1 = (float) (Math.PI * (-0.5+(float)i/lats));
			float z1 = (float) Math.sin(lat1);
			float zr1 = (float) Math.cos(lat1);
			
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for(int j = 0; j <= longs; j++) {
				float lng = (float) (2*Math.PI*(float)(j-1)/longs);
				float x = (float) Math.cos(lng);
				float y = (float) Math.sin(lng);
				gl.glNormal3f(r*x*zr1, r*y*zr1, r*z1);
				gl.glTexCoord2f((x*zr1)/2.0f, (y*zr1)/2.0f+0.5f);
				gl.glVertex3f(r*x*zr1, r*y*zr1, r*z1);
				gl.glNormal3f(r*x*zr0, r*y*zr0, r*z0);
				gl.glTexCoord2f((x*zr0)/2.0f, (y*zr0)/2.0f+0.5f);
				gl.glVertex3f(r*x*zr0, r*y*zr0, r*z0);
			}
			gl.glEnd();  
		}
    }
    
    /**Prints the current fps to the screen*/
    /*TODO: fix this to actually print to screen*/
    public static void printFps(int frameTime) {
    	double currentFps = 60;
    	if (frameTime > 0) currentFps = 1000/frameTime;
    	if (currentFps >= 60) currentFps = 60;
    	System.out.println(currentFps);
    }
    
    /**Rotates the portal*/
    public static void rotatePortal() {
    	++portalRot;
    	if (portalRot >= 360) portalRot = 1;
    }
    
    /**Rotates the planets*/
    public static void rotatePlanets() {
    	planetRot += 0.004f;
    	if (planetRot >= 360) planetRot = 0.004f;
    }
    
	//SETTERS
	public static void setGL(GL2 gl) {
		Graphics.gl = gl;
	}
	
	public static void setResources(Resources resources) {
		Graphics.resources = resources;
	}
	
	public static void setHigh(boolean high) {
		Graphics.high = high;
	}
	
	public static void setLevel(RiemannCube level) {
		Graphics.level = level;
	}
	
	public static void setPlayer(Player player) {
		Graphics.player = player;
	}

}
