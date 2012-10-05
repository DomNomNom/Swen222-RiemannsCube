package world.objects.doors;

import java.awt.Color;
import java.util.Map;
import java.util.Set;

import world.cubes.Cube;
import world.objects.Trigger;

/**
 * Class that creates a door which enables the players to "travel" through to 
 * a new level. Ie, this door contains the name of an xml file of the level it
 * leads to.
 */
public class EntranceDoor extends Door {

    private final String levelName;
    public String levelName(){ return levelName;}
    
    public EntranceDoor(Cube cube, Set<Integer> triggerIDs, Map<Integer, Trigger> triggers, Color col, String levelName) {
        super(cube, triggerIDs, triggers, col);
        this.levelName = levelName;
    }

    public EntranceDoor(Cube cube, int numLocks, Color color, String levelName) {
        super(cube, numLocks, color);
        this.levelName = levelName;
    }
    
}
