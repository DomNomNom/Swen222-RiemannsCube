package world.cubes;

import utils.Int3;
import world.objects.GameObject;

public class Wall extends Cube {
    
    public Wall(Int3 pos) { 
        super(pos);
    }

    @Override
    public CubeType type() {
        return CubeType.WALL;
    }

    @Override
    public boolean addObject(GameObject o) {
        return false; // we won't allow any objects in here
    }
}
