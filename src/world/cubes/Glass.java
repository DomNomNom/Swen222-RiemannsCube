package world.cubes;

import world.objects.GameObject;


public class Glass extends Cube {

    public Glass(){ }
    
    @Override
    public CubeType type() {
        return CubeType.GLASS;
    }
    
    @Override
    public boolean addObject(GameObject o) {
        return false; // we won't allow any objects in here
    }
}
