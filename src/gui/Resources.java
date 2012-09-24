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
	private int[] texID = new int[4]; //where the texture ids are stored
	//The textures
	private ByteBuffer floorTex;
	private ByteBuffer wallTex;
	private ByteBuffer glassTex;
	private ByteBuffer spaceTex;
	
	//CONSTRUCTOR
	/**Creates a new resources object
	 * @param drawable*/
	public Resources(GLAutoDrawable drawable) {
		loadTextures(drawable);
	}
	
	//METHODS
	/**Get the texture ids
	 * @return the texture ids
	 */
	public int[] getIDs() {
		return texID;
	}
	
	/**Loads all the textures that are needed
	 * @param drawable*/
	private void loadTextures(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		
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
		
		//create the texture IDs
		gl.glGenTextures(texID.length, texID, 0);
		
		//Create the textures
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
            300, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, spaceTex);
		
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
