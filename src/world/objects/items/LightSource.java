package world.objects.items;

import java.awt.Color;

import world.cubes.Cube;

public class LightSource extends GameItem {

	public LightSource(Cube pos) {
        super(pos);
    }


    public Color color() {return new Color(0,0,0);}
	
	

    @Override
    public String getClassName() {
        return "LightSource";
    }

}
