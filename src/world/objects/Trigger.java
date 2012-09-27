package world.objects;

import java.awt.Color;

import world.cubes.Cube;

public abstract class Trigger extends GameObject {

    public Trigger(Cube c) {
        super(c);
    }

    public abstract Color color();
    
    public abstract int getID();
}
