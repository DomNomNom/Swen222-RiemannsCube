package world.cubes;

public class Wall extends Cube {
    
    public Wall() {
    }

    
    public int type(){
        return CubeType.WALL.ordinal(); // TODO: proper enums
    }

}
