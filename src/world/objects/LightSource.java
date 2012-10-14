package world.objects;

import java.awt.Color;

import world.cubes.Cube;
import world.objects.items.GameItem;

public class LightSource extends GameObject {

	public LightSource(Cube pos) {
        super(pos);
    }

    public Color color() {return new Color(0,0,0);}

    @Override
    public String getClassName() {
        return "LightSource";
    }

}
