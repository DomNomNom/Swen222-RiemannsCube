package world.objects;

import world.cubes.Cube;
import world.cubes.CubeType;

public class Glass extends Cube {

    public Glass(){ }
    
    @Override
    public int type() {
        return CubeType.GLASS.ordinal();
    }

}
