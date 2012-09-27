package world.cubes;

import utils.Int3;

public class Space extends Cube{
    
    public Space(Int3 pos) { 
        super(pos);
    }


    @Override
    public CubeType type() {
        return CubeType.SPACE;
    }
    
}
