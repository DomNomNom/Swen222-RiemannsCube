package world.objects;

import java.awt.Color;

public class Door extends GameObject {

    Trigger[] triggers;
    int index = 0;
    Color color;
    
    public Door(int numLocks){
        triggers = new Trigger[numLocks];
        this.color = Color.BLUE;
    }
    
    public Door(int numLocks, Color color){
        triggers = new Trigger[numLocks];
        this.color = color;
    }
    
    public void addTrigger(Trigger trigger){
        if (allTriggersPlaced()) System.out.println("All triggers placed!");
        else triggers[index++] = trigger;
    }
    
    public Trigger[] triggers(){
        return triggers;
    }
    
    public boolean allTriggersPlaced(){
        return index == triggers.length;
    }
    
    public Color color(){
        return this.color;
    }
}