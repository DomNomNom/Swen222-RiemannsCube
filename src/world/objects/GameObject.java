package world.objects;

import utils.Int3;


public abstract class GameObject {
    
    protected final Int3 pos = new Int3();
    public Int3 getPos() { return pos; }
    void setPos(Int3 to) { pos.set(to); } // only accessible from within world package
	
    public abstract String getClassName();

}
