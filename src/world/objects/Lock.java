package world.objects;

import java.awt.Color;

import world.items.Key;

public class Lock extends Trigger {

    private Color color;
    private int ID;
    
    public Lock(int ID, Color color){
        this.ID = ID;
        this.color = color;
    }
    
    public Color color(){
        return color;
    }
    
    public int getID(){
        return ID;
    }

    @Override
    public String getClassName() {
        return "Lock";
    }
}
