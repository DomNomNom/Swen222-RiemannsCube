package world.objects;

import java.awt.Color;

import world.cubes.Cube;


/**
 * A trigger is something that will affect anther thing (such as a door) if its state changes
 *
 * @author schmiddomi
 */
public abstract class Trigger extends GameObject {

    protected boolean currentState = false;
    
    public Trigger(Cube c) {
        super(c);
    }

    /**
     * @return whether the trigger is active (ie. the lock is open) 
     */
    public boolean state() { return currentState; }
    
    public abstract Color color();
    
    public abstract int getID();
}
