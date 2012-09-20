package world.objects;

import utils.Int3;


public abstract class GameObject {
	
    public abstract String getClassName();

    private Int3 pos;
    public Int3 getPos() { return pos; }

}
