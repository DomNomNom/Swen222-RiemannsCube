package world.cubes;

import java.util.ArrayList;
import java.util.List;

import world.Player;
import world.objects.GameObject;

public abstract class Cube {

	private int type;
	private GameObject object;
	private Player player;
	
	public Cube(int type){
		this.type = type;
	}
	
	public int type(){
		return type;
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
