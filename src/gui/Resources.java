package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
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
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

/**This class loads all the images that will be used as textures
 * and provides methods for accessing the images
 * 
 * @author David Saxon 300199370
 */
public class Resources {
	
	//FIELDS
	private int[] texID = new int[15]; //where the texture ids are stored
	//The 3D objects
	public Object3D keyObj; //TODO: make getters
	
	//The textures
	private ByteBuffer floorTex;
	private ByteBuffer wallTex;
	private ByteBuffer glassTex;
	private ByteBuffer spaceTex;
	private ByteBuffer player1Tex;
	private ByteBuffer player2Tex;
	private ByteBuffer player3Tex;
	private ByteBuffer player4Tex;
	private ByteBuffer pausedTitleTex;
	private ByteBuffer pausedResumeTex;
	private ByteBuffer pausedExitTex;
	private ByteBuffer doorTex;
	private ByteBuffer doorLightsTex;
	//TODO: remove these only for testing
	private ByteBuffer buttonTex;
	private ByteBuffer lockTex;
	
	
	//CONSTRUCTOR
	/**Creates a new resources object
	 * @param drawable*/
	public Resources(GL2 gl) {
		loadObj();
		loadTextures(gl);
	}
	
	//METHODS
	/**Loads the object files*/
	private void loadObj() {
		keyObj = new Object3D(new File("resources/obj/key.obj"));
	}
	
	/**Loads all the textures that are needed
	 * @param drawable*/
	private void loadTextures(GL2 gl) {
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        
		//load the images into the textures
		BufferedImage floorImg = null;
	    try {
			floorImg = ImageIO.read(new File("resources/gfx/floor1.png")); //open the image
			floorTex = convertImageData(floorImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage wallImg = null;
		try {
			wallImg = ImageIO.read(new File("resources/gfx/wall1.png")); //open the image
			wallTex = convertImageData(wallImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage glassImg = null;
		try {
			glassImg = ImageIO.read(new File("resources/gfx/glass.png")); //open the image
			glassTex = convertImageData(glassImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage spaceImg = null;
		try {
			spaceImg = ImageIO.read(new File("resources/gfx/skyPlane.png")); //open the image
			spaceTex = convertImageData(spaceImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage player1Img = null;
		try {
			player1Img = ImageIO.read(new File("resources/gfx/player1.png")); //open the image
			player1Tex = convertImageData(player1Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage player2Img = null;
		try {
			player2Img = ImageIO.read(new File("resources/gfx/player2.png")); //open the image
			player2Tex = convertImageData(player2Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage player3Img = null;
		try {
			player3Img = ImageIO.read(new File("resources/gfx/player3.png")); //open the image
			player3Tex = convertImageData(player3Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage player4Img = null;
		try {
			player4Img = ImageIO.read(new File("resources/gfx/player4.png")); //open the image
			player4Tex = convertImageData(player4Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage pausedTitleImg = null;
		try {
			pausedTitleImg = ImageIO.read(new File("resources/gfx/pausedTitle.png")); //open the image
			pausedTitleTex = convertImageData(pausedTitleImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage pausedResumeImg = null;
		try {
			pausedResumeImg = ImageIO.read(new File("resources/gfx/resume.png")); //open the image
			pausedResumeTex = convertImageData(pausedResumeImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage pausedExitImg = null;
		try {
			pausedExitImg = ImageIO.read(new File("resources/gfx/exit.png")); //open the image
			pausedExitTex = convertImageData(pausedExitImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage doorImg = null;
		try {
			doorImg = ImageIO.read(new File("resources/gfx/door.png")); //open the image
			doorTex = convertImageData(doorImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage doorLightsImg = null;
		try {
			doorLightsImg = ImageIO.read(new File("resources/gfx/doorLights.png")); //open the image
			doorLightsTex = convertImageData(doorLightsImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		
		//TODO: remove these
		BufferedImage buttonImg = null;
		try {
			buttonImg = ImageIO.read(new File("resources/gfx/button.png")); //open the image
			buttonTex = convertImageData(buttonImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage lockImg = null;
		try {
			lockImg = ImageIO.read(new File("resources/gfx/lock.png")); //open the image
			lockTex = convertImageData(lockImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		
		
		
		//create the texture IDs
		gl.glGenTextures(texID.length, texID, 0);
		
		//Create the textures
		gl.glBindTexture(GL.GL_TEXTURE_2D, texID[0]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, floorTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[1]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, wallTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[2]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, glassTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[3]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 300,
            300, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, spaceTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[4]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 512,
            512, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, player1Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[5]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 512,
            512, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, player2Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[6]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 512,
            512, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, player3Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[7]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 512,
            512, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, player4Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[8]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1022,
            171, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pausedTitleTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[9]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1096,
            114, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pausedResumeTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[10]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1004,
            112, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pausedExitTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[11]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, doorTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[12]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, doorLightsTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[13]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buttonTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[14]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, lockTex);
	}
	
	/**Get the texture ids
	 * @return the texture ids*/
	public int[] getIDs() {
		return texID;
	}
	
	/**Converts a buffered image to an array of byte buffer
	 * @param bufferedImage the buffered image to convert
	 * @return the new byte buffer
	*/
	private ByteBuffer convertImageData(BufferedImage bufferedImage) {
		//create needed variables
	    ByteBuffer imageBuffer;
	    WritableRaster raster;
	    BufferedImage texImage;
	    
	    //build the colour model
	    ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace .getInstance(ColorSpace.CS_sRGB),
	    		new int[] { 8, 8, 8, 8 }, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
	    
	    //set the raster
	    raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
	            bufferedImage.getWidth(), bufferedImage.getHeight(), 4, null);
	    //create the image
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
	
}
