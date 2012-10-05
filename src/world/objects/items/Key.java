package world.objects.items;

import java.awt.Color;

import world.cubes.Cube;

/**
 * A Key will unlock a lock trigger if it has the same colour
 *
 * @author schmiddomi
 */
public class Key extends GameItem {

    // The colour identifies what locks it belongs to
    Color colour;
    public Color colour() {return colour;  }
    
    public Key(Cube pos, Color colour){
        super(pos);
        this.colour = colour;
    }
    

    @Override
    public String getClassName() {
        return "Key";
    }
    
}
