package world.cubes;

public class Floor extends Cube {
    public Floor() { }
    
    public int type(){
        return CubeType.FLOOR.ordinal(); // TODO: proper enums
    }
    
}