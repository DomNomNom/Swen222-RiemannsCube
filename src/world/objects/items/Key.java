package world.objects.items;

import java.awt.Color;
import java.util.Random;

import world.cubes.Cube;

/**
 * A Key will unlock a lock trigger if it has the same colour
 *
 * @author schmiddomi
 */
public class Key extends GameItem {

    // The colour identifies what locks it belongs to
    Color color;
    private float rotation = 0; //the current rotation of the object
    private float rotationSpeed; //the rotation speed of the key
    
    public Color color() {return color;  }
    
    // Tells whether this key opens an exit door
    // Necessary as exit doors don't have colors.
    private boolean isExit = false;
    public boolean isExit(){ return isExit;}
    public void setExit(boolean b){ isExit = b;}
    
    public Key(Cube pos, Color color){
        super(pos);
        this.color = color;
        Random rand = new Random();
        rotation = rand.nextInt(360);
        rotationSpeed = (rand.nextFloat()*10)-5;
    }
    
    /**rotate the key*/
    public void rotate() {
    	rotation += rotationSpeed;
    	if (rotation >= 360) rotation -= 360;
    	else if (rotation < 0) rotation += 360;
    }
    
    public float getRotate() {
    	return rotation;
    }

    @Override
    public String getClassName() {
        return "Key";
    }
    
}
