package world.cubes;

import utils.Int3;

public class Floor extends Cube {
    
    public Floor(Int3 pos) { 
        super(pos);
    }

    @Override
    public CubeType type() {
        return CubeType.FLOOR;
    }

    
}