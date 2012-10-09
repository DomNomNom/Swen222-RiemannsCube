package world.objects.doors;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import world.cubes.Cube;
import world.objects.GameObject;
import world.objects.Trigger;

/**
 * A Door conditionally impedes movement to a tile
 * The colour on this is just decorative.
 * It uses poll rather than push architecture.  (push would be more efficient)
 *
 * @author schmiddomi
 */
public abstract class Door extends GameObject {

    private final Set<Integer> triggerIDs = new HashSet<Integer>();
    private final Map<Integer, Trigger> triggersMap;

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

}