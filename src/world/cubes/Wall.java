package world.cubes;

import world.objects.GameObject;

public class Wall extends Cube {
    
//    public int type(){
//        return CubeType.WALL.ordinal(); // TODO: proper enums
//    }

    @Override
    public CubeType type() {
        return CubeType.WALL;
    }

    @Override
    public boolean addObject(GameObject o) {
        return false; // we won't allow any objects in here
    }
}
