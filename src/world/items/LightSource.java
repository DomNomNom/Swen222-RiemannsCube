package world.items;

import java.awt.Color;

public class LightSource extends GameItem {

	public Color color() {return new Color(0,0,0);}

    @Override
    public String getClassName() {
        return "LightSource";
    }

}
