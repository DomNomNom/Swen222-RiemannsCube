package world.objects;

import java.awt.Color;

import world.cubes.Cube;
import world.objects.items.Key;

public class Lock extends Trigger {

    private Color color;
    private int ID;
    
    // Tells whether this lock opens an exit door
    // Necessary as exit doors don't have colors.
    private boolean isExit = false;
    public boolean isExit(){ return isExit;}
    public void setExit(boolean b){ isExit = b;}
    
    public Lock(Cube pos, int ID, Color color){
        super(pos);
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
