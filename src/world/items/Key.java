package world.items;

import java.awt.Color;

public class Key extends GameItem {

    // The colour identifies what locks it belongs to
    Color colour;
    public Color colour() {return colour;  }
    
    public Key(Color colour){
        this.colour = colour;
    }
    

    @Override
    public String getClassName() {
        return "Key";
    }
}
