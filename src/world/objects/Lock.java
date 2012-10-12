package world.objects;

import java.awt.Color;
import java.util.Map;

import world.cubes.Cube;
import world.objects.items.GameItem;
import world.objects.items.Key;

public class Lock extends Trigger {

    private Color color;
    public Color color(){ return color; }
    
    private Key insertedKey = null;
    
    // Tells whether this lock opens an exit door
    // Necessary as exit doors don't have colours.
    private boolean isExit = false;
    public boolean isExit(){ return isExit;}
    public void setExit(boolean b){ isExit = b;}
    
    
    public Lock(Cube pos, int ID, Map<Integer, Trigger> globalIDs, Color color){
        super(pos, ID, globalIDs);
        this.color = color;
    }
    

    @Override
    public boolean canUseStart(GameItem item) {
        if (!(item instanceof Key)) return false;
        Key k = (Key) item;
        if (!k.color().equals(color)) return false; // must have the same 
        if (currentState) return false; // we can't interact once we have used the key
        return true;
    }
    @Override
    public boolean canUseStop(GameItem item) {
        if (!currentState) return false; // we can't pull anything out if we don't have anything
        if (item != null) return false; // they shouldn't try to insert anything
        if (this.insertedKey == null) throw new Error("OMG!"); // sanity check
        return true;
    }
    @Override
    public GameItem useStart(GameItem item) {
        insertedKey = (Key)item;
        currentState = true;
        return null; // we don't give the key back yet
    }
    @Override
    public GameItem useStop(GameItem item) {
        insertedKey = (Key)item;
        currentState = true;
        Key ret = insertedKey;
        insertedKey = null;
        return ret; // we give the key back as we are nice locks :)
    }
    
    @Override
    public String getClassName() {
        return "Lock";
    }
}