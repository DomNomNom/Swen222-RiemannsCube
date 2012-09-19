package world.objects;

import java.awt.Color;

public interface GameObject {
	
	//important for ID-ing Doors to Locks to Keys. Other objects can return (0,0,0).
	public Color color();
}
