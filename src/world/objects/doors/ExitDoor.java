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
public class ExitDoor extends Door {

    public final String hubLevel = "";  //TODO Create HubLevel
    // No color for the exit door, maybe a specific texture
    
    public ExitDoor(Cube cube, Set<Integer> triggerIDs, Map<Integer, Trigger> triggers) {
        super(cube, triggerIDs, triggers, null);
    }

    public ExitDoor(Cube cube, int numLocks) {
        super(cube, numLocks, null);
    }
}
