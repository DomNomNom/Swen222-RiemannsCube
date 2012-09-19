package world.cubes;

import world.Player;
import world.objects.GameObject;

public abstract class Cube {

	private GameObject object;
	private Player player;
	public CubeType cubeType;
	
	public Cube(int type){
		this.cubeType = CubeType.values()[type];
	}
	
	public int type(){
		return cubeType.ordinal();
	}
	
	public void setObject(GameObject o){
		object= o;
	}
	
	public GameObject object(){
		return object;
	}

	public Player player() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
