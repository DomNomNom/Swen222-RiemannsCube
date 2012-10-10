package world.objects;

import java.awt.Color;

import world.cubes.Cube;
import world.objects.items.GameItem;
import world.objects.items.Key;

public class Lock extends Trigger {

    private Color color;
    public Color color(){ return color; }
    
    private int ID;
    public int getID(){ return ID; }
    
    // Tells whether this lock opens an exit door
    // Necessary as exit doors don't have colours.
    private boolean isExit = false;
    public boolean isExit(){ return isExit;}
    public void setExit(boolean b){ isExit = b;}
    
    
    public Lock(Cube pos, int ID, Color color){
        super(pos);
        this.ID = ID;
        this.color = color;
    }
    

    @Override
    public boolean canUse(GameItem i) {
        if (currentState) return false; // we can't interact once we have used the key
        if (i instanceof Key) return true;
        return false;
    }
    @Override
    public GameItem use(GameItem i) {
        currentState = true;
        return null; // we consume the key
    }
    
    @Override
    public String getClassName() {
        return "Lock";
    }
}