package world.cubes;

import utils.Int3;
import world.objects.Player;

public class Space extends Cube{
    
    public Space(Int3 pos) { 
        super(pos);
    }


    @Override
    public CubeType type() {
        return CubeType.SPACE;
    }

    @Override
    public boolean blocks(Player p) {
        return true;
    }
}
