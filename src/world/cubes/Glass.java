package world.cubes;

import utils.Int3;
import world.objects.GameObject;


public class Glass extends Cube {

    public Glass(Int3 pos) { 
        super(pos);
    }
    
    @Override
    public CubeType type() {
        return CubeType.GLASS;
    }
    
    @Override
    public boolean addObject(GameObject o) {
        return false; // we won't allow any objects in here
    }
}
