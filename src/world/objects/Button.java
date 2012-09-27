package world.objects;

import java.awt.Color;

import world.cubes.Cube;

public class Button extends Trigger {

    Color color;
    public Color color() { return color; }

    public final int id;
    public int getID() { return id; }
    

    public Button(Cube pos, int id, Color color){
        super(pos);
        this.color = color;
        this.id = id;
    }
    
    
    

    @Override
    public String getClassName() {
        return "Trigger";
    }
}
