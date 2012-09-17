package world.objects;

import world.items.Key;

public class Lock implements Trigger {

	Key key;
	
	public Lock(){
	}
	
	public Key key(){
		return key;
	}
	
	public void setKey(Key key){
		this.key = key;
	}
}
