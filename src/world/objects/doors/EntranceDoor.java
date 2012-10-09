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
    
    public EntranceDoor(Cube cube, Map<Integer, Trigger> triggers, Color col, String levelName) {
        super(cube, triggers, col);
        this.levelName = levelName;
    }

}
