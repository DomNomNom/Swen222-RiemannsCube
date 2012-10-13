package world.objects;

import java.awt.Color;
import java.util.Map;

import world.cubes.Cube;


/**
 * A trigger is something that will affect anther thing (such as a door) if its state changes
 *
 * @author schmiddomi
 */
public abstract class Trigger extends GameObject {

    /** whether the trigger is active (ie. the lock is open)    */
    protected boolean currentState = false;
    public boolean state() { return currentState; }
    
    
    private final int ID;
    public int getID() { return ID; }

    
    /**  
     * @param gobalIDs doing the adding to the trigger map here ensures consistency
     */
    public Trigger(Cube c, int ID, Map<Integer, Trigger> globalIDs) {
        super(c);
        this.ID = ID;
        globalIDs.put(ID, this);
    }
    
    public abstract Color color();
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        
        Trigger t = (Trigger) obj; // safe as it is checked above
        if (ID != t.ID) return false;
        
        return true;
    }
}
