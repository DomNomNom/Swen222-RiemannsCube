package world.cubes;

/**
 * 
 * @author mudgejayd
 *
 */
public enum CubeType {
	
	SPACE, FLOOR, WALL, GLASS;
	
	public String toString(){
		return this.name().charAt(0)+this.name().substring(1, this.name().length()).toLowerCase();
	}
}
