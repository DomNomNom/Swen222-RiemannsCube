package world.objects;

import java.awt.Color;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Door extends GameObject {

    private Set<Integer> triggerIDs;
    private Map<Integer, Trigger> triggersMap;

    private int index = 0, numLocks;
    private Color color;

    public Door(Set<Integer> triggerIDs, Map<Integer, Trigger> triggers,
            Color col) {
        this.triggerIDs = triggerIDs;
        this.triggersMap = triggers;
        this.color = col;
    }

    public Door(int numLocks, Color color) {
        triggerIDs = new HashSet<Integer>(numLocks);
        this.numLocks = numLocks;
        this.color = color;
    }

    public void addTrigger(int id) {
        if (allTriggersPlaced())
            System.out.println("All triggers placed!");
        else {
            triggerIDs.add(id);
            index++;
        }
    }

    public Set<Integer> triggers() {
        return triggerIDs;
    }

    public boolean allTriggersPlaced() {
        return numLocks == triggerIDs.size();
    }

    public Color color() {
        return this.color;
    }

    @Override
    public String getClassName() {
        return "Door";
    }
    
    public int id(){
        return 0; //FIXME
    }
}