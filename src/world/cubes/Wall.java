package world.cubes;

public class Wall extends Cube {
    
//    public int type(){
//        return CubeType.WALL.ordinal(); // TODO: proper enums
//    }

    @Override
    public CubeType type() {
        return CubeType.WALL;
    }

}
