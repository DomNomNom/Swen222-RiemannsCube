package world.objects;

import java.awt.Color;

public class Button extends Trigger {

    Color color;
    public Color color() { return color; }

    public final int id;
    public int getID() { return id; }
    

    public Button(int id, Color color){
        this.color = color;
        this.id = id;
    }
    
    
    

    @Override
    public String getClassName() {
        return "Trigger";
    }
}
