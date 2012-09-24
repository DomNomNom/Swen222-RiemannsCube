package world.objects;

import utils.Int3;


public abstract class GameObject {
    
    protected final Int3 pos = new Int3();
    public Int3 getPos() { return pos; }
	
    public abstract String getClassName();

}
