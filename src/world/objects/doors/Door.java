package world.objects.doors;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import world.cubes.Cube;
import world.objects.GameObject;
import world.objects.Player;
import world.objects.Trigger;

/**
 * A Door conditionally impedes movement to a tile
 * The colour on this is just decorative.
 * It uses poll rather than push architecture.  (push would be preferred but more complex)
 * A door will stay open once isClosed() has been called while all the triggers where active (locks unlocked) 
 *
 * @author schmiddomi
 */
public abstract class Door extends GameObject {

    private final Set<Integer> triggerIDs = new HashSet<Integer>();
    private final Map<Integer, Trigger> triggersMap;

    private boolean open = false;
    private boolean playSound = false;
    private boolean soundPlayed = false;
    
    private Color color;

    
    public Door(Cube cube, Map<Integer, Trigger> triggers, Color col) {
        super(cube);
        this.triggersMap = triggers;
        this.color = col;
    }

    public void addTrigger(int id) {
        triggerIDs.add(id);
    }

    /** returns a view of the trigger IDs */
    public Set<Integer> triggersIDs() {
        return Collections.unmodifiableSet(triggerIDs);
    }

    public Color color() {
        return this.color;
    }

    @Override
    public String getClassName() {
        return "Door";
    }

    /** A door will stay open once isClosed() has been called while all the triggers where active (locks unlocked) */
    public boolean isClosed() {
    	if (!soundPlayed) {
    		playSound = soundPlayed = true;
    	}
        if (open) return false;
    	for (Integer i : triggerIDs) {
    		if (!triggersMap.containsKey(i)) throw new Error("Someone didn't add a trigger to the triggerMap!");
    		if (!triggersMap.get(i).state()) return true;
    	}
    	open = true;
    	return false;
    	
    }
    @Override
    /** will block the player (return true) iff any of our triggers are off
     * calls isClosed()
     */
    public boolean blocks(Player p) {
    	return isClosed();
    }
    
    public boolean playSound() {
    	return playSound;
    }
    
    public void soundPlayed() {
    	playSound = false;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        Door other = (Door) o;
        if (triggerIDs.equals(other.triggerIDs)) return false;
        return true;
    }
}