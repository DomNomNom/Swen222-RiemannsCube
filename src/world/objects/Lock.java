package world.objects;

import java.awt.Color;

import world.items.Key;

public class Lock implements Trigger {

	Key key;
	Color color;
	
	public Lock(Color color){
		this.color = color;
	}
	
	public Key key(){
		return key;
	}
	
	public void setKey(Key key){
		this.key = key;
	}
	
	public Color color(){
		return color;
	}
}
