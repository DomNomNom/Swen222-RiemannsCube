package world.objects;

import java.awt.Color;
import java.util.Map;

import world.cubes.Cube;

public class Button extends Trigger {

    Color color;
    public Color color() { return color; }


    public Button(Cube pos, int ID, Map<Integer, Trigger> gobalIDs, Color color){
        super(pos, ID, gobalIDs);
        this.color = color;
    }
    
    
    

    @Override
    public String getClassName() {
        return "Trigger";
    }
}
