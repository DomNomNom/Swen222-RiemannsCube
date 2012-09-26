package world.cubes;


public class Glass extends Cube {

    public Glass(){ }
    
    @Override
    public int type() {
        return CubeType.GLASS.ordinal();
    }
    
}
