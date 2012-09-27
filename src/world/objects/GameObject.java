package world.objects;

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
    public Cube getCube() { return cube; }
    public Int3 getPos() { return cube.pos; }
    
    /**
     * This will remove this object from the old cube and add to the new   
     * 
     * @param to
     * @return Whether it was possible to do so
     */
    public boolean move(Cube to) {
        if (!cube.removeObject(this)) 
            throw new IllegalStateException("I can't remove this object from this cube! D:");
        
        if (!cube.addObject(this))
            return false;
        
        cube = to;
        return true;
    }
    
    
    public abstract String getClassName();

}
