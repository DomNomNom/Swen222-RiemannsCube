package world.objects;

import utils.Configurations;
import utils.Int3;
import utils.Int3;
import world.cubes.Cube;

/**
 * A GameObject is anything that a Cube could contain.  
 *
 * @author schmiddomi
 */
public abstract class GameObject {
    
    protected Cube cube;
    public Cube cube() { return cube; }
    public Int3 pos() { return cube.pos(); }
    
    public GameObject(Cube c) {
        if (c==null) throw new IllegalArgumentException();
        cube = c;
    }
    
    /**
     * This will remove this object from the old cube and add to the new   
     * 
     * @param to
     * @return Whether it was possible to do so
     */
    public boolean move(Cube to) {
        if (Configurations.debugPrint)  System.out.println(myName() +" moving " + cube.pos() + " ==> " + to.pos());
        if (!cube.removeObject(this)) throw new IllegalStateException("I can't remove myself from this cube! D:");
        if (!to.addObject(this))      throw new IllegalStateException("I add myself to the other cube! D:");
        cube = to;
        return true;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)      return true;
        if (obj == null)      return false;
        if (!(obj instanceof GameObject))  return false;  
        GameObject other = (GameObject) obj;
        if (!getClassName().equals(other.getClassName())) return false;
        if (!pos().equals(other.pos())) return false;
        
        return true;
    }

    public abstract String getClassName();
    public String myName() { return "[" + getClassName() + "]"; }
    
    /** 
     * returns whether a player is allowed to move to the same cube as this object
     * returns false by default. 
     */
    public boolean blocks(Player p) {
        return false;
    }
}
