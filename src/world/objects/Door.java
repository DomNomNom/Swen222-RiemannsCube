package world.objects;

import java.awt.Color;

public class Door implements GameObject {

	Lock[] locks;
	int index = 0;
	Color color;
	
	public Door(int numLocks){
		locks = new Lock[numLocks];
	}
	
	public void addLock(Lock lock){
		if (allLocksPlaced()) System.out.println("All locks placed!");
		else locks[index++] = lock;
	}
	
	public Lock[] locks(){
		return locks;
	}
	
	public boolean allLocksPlaced(){
		return index == locks.length;
	}
}