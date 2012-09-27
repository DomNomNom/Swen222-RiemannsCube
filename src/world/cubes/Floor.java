package world.cubes;

public class Floor extends Cube {
    
//    public int type(){
//        return CubeType.FLOOR.ordinal(); // TODO: proper enums
//    }

    @Override
    public CubeType type() {
        return CubeType.FLOOR;
    }

    
}