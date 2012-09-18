package world.items;

import java.awt.Color;

public class Key extends GameItem {

    Color color;
    
    public Key(Color color){
        this.color = color;
    }
    
    public Color color(){
        return color;
    }
}
