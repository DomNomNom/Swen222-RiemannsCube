package world.cubes;

import java.util.ArrayList;
import java.util.List;

import world.objects.GameObject;

public abstract class Cube {

	private int type;
	private List<GameObject> objects = new ArrayList<GameObject>();
	
	public Cube(int type){
		this.type = type;
	}
	
	public int type(){
		return type;
	}
	
	public void setObject(GameObject o){
		objects.add(o);
	}
	
	public List<GameObject> objects(){
		return objects;
	}
}
