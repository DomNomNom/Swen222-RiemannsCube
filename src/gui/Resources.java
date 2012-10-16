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

/**This class loads all the images that will be used as textures
 * and provides methods for accessing the images
 * 
 * @author David Saxon 300199370
 */
public class Resources {
	
	//FIELDS
	private int[] texID = new int[27]; //where the texture ids are stored
	//The 3D objects
	private Object3D keyObj;
	private Object3D buttonObj;
	private Object3D buttonBaseObj;
	private Object3D lightBaseObj;
	private Object3D lightObj;
	
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
	private ByteBuffer portalTex;
	private ByteBuffer planet1Tex;
	private ByteBuffer planet2Tex;
	private ByteBuffer planet3Tex;
	private ByteBuffer planet4Tex;
	private ByteBuffer starTex;
	private ByteBuffer container1Tex;
	private ByteBuffer container2Tex;
	private ByteBuffer lightBaseTex;
	private ByteBuffer lightTex;
	private ByteBuffer keyTex;
	private ByteBuffer buttonBaseTex;
	private ByteBuffer buttonTex;
	private ByteBuffer tokenTex;
	private ByteBuffer lightFlareTex;
	
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
		buttonObj = new Object3D(new File("resources/obj/button.obj"));
		buttonBaseObj = new Object3D(new File("resources/obj/buttonBase.obj"));
		lightBaseObj = new Object3D(new File("resources/obj/lightBase.obj"));
		lightObj = new Object3D(new File("resources/obj/light.obj"));
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
		
		BufferedImage portalImg = null;
		try {
			portalImg = ImageIO.read(new File("resources/gfx/portal.png")); //open the image
			portalTex = convertImageData(portalImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage planet1Img = null;
		try {
			planet1Img = ImageIO.read(new File("resources/gfx/planet1.png")); //open the image
			planet1Tex = convertImageData(planet1Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage planet2Img = null;
		try {
			planet2Img = ImageIO.read(new File("resources/gfx/planet2.png")); //open the image
			planet2Tex = convertImageData(planet2Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage planet3Img = null;
		try {
			planet3Img = ImageIO.read(new File("resources/gfx/planet3.png")); //open the image
			planet3Tex = convertImageData(planet3Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage planet4Img = null;
		try {
			planet4Img = ImageIO.read(new File("resources/gfx/planet4.png")); //open the image
			planet4Tex = convertImageData(planet4Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage starImg = null;
		try {
			starImg = ImageIO.read(new File("resources/gfx/star.png")); //open the image
			starTex = convertImageData(starImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage container1Img = null;
		try {
			container1Img = ImageIO.read(new File("resources/gfx/container1.png")); //open the image
			container1Tex = convertImageData(container1Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage container2Img = null;
		try {
			container2Img = ImageIO.read(new File("resources/gfx/container2.png")); //open the image
			container2Tex = convertImageData(container2Img); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage lightBaseImg = null;
		try {
			lightBaseImg = ImageIO.read(new File("resources/gfx/lightBase.png")); //open the image
			lightBaseTex = convertImageData(lightBaseImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage lightImg = null;
		try {
			lightImg = ImageIO.read(new File("resources/gfx/light.png")); //open the image
			lightTex = convertImageData(lightImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage keyImg = null;
		try {
			keyImg = ImageIO.read(new File("resources/gfx/key.png")); //open the image
			keyTex = convertImageData(keyImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage buttonBaseImg = null;
		try {
			buttonBaseImg = ImageIO.read(new File("resources/gfx/buttonBase.png")); //open the image
			buttonBaseTex = convertImageData(buttonBaseImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage buttonImg = null;
		try {
			buttonImg = ImageIO.read(new File("resources/gfx/button.png")); //open the image
			buttonTex = convertImageData(buttonImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage tokenImg = null;
		try {
			tokenImg = ImageIO.read(new File("resources/gfx/token.png")); //open the image
			tokenTex = convertImageData(tokenImg); //converts the image
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage lightFlareImg = null;
		try {
			lightFlareImg = ImageIO.read(new File("resources/gfx/lightFlare.png")); //open the image
			lightFlareTex = convertImageData(lightFlareImg); //converts the image
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
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1668,
            200, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pausedTitleTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[9]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1670,
            134, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pausedResumeTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[10]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1670,
            139, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, pausedExitTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[11]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, doorTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[12]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1000,
            1000, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, portalTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[13]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1600,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, planet1Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[14]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1600,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, planet2Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[15]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1600,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, planet3Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[16]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1600,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, planet4Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[17]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 800,
            800, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, starTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[18]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 500,
            500, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, container1Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[19]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 550,
            550, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, container2Tex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[20]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 100,
            100, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, lightBaseTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[21]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 100,
            20, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, lightTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[22]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 10,
            10, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, keyTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[23]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 50,
            50, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buttonBaseTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[24]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 400,
            400, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buttonTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[25]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 100,
            100, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, tokenTex);
        
        gl.glBindTexture(GL.GL_TEXTURE_2D, texID[26]);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, 1000,
            100, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, lightFlareTex);
	}
	
	/**Return an object that matches the given string
	 * @param name the name of the required object
	 * @return the object*/
	public Object3D getObj(String name) {
		if (name.equals("key")) return keyObj;
		else if (name.equals("button")) return buttonObj;
		else if (name.equals("buttonBase")) return buttonBaseObj;
		else if (name.equals("lightBase")) return lightBaseObj;
		else if (name.equals("light")) return lightObj;
		return null;
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
