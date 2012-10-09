package world.objects.doors;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import world.cubes.Cube;
import world.objects.Trigger;

/**
 * Class that creates the most simple type of door, to be used in levels
 */
public class LevelDoor extends Door{

    public LevelDoor(Cube cube, Map<Integer, Trigger> triggers, Color col) {
        super(cube, triggers, col);
    }
    
}
