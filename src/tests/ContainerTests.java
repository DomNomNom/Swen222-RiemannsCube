package tests;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import utils.Int3;
import world.RiemannCube;
import world.objects.Container;
import world.objects.GlobalHolder;
import world.objects.items.Key;

public class ContainerTests {
	
	private RiemannCube cube;
	
	@Test
	public void testCanUse(){
		cube = new RiemannCube(new Int3(4, 4, 4));
		
		// Create GlobalHolder for Red containers.
		cube.containers.put(Color.RED, new GlobalHolder());
		
		// Create a red container
		Container red = new Container(cube.getCube(0, 0, 0), Color.RED, cube);
		
		// Create a key
		Key key = new Key(cube.getCube(0, 0, 1), Color.RED);

		// Test that a player holding nothing can't pop something from an empty container
		assertFalse(red.canUse(null));
		
		// Test that a player holding an item can add things to the container
		assertTrue(red.canUse(key));

		// Add the key, then check that it is now impossible to add something to the container
		red.use(key);
		Key key2 = new Key(cube.getCube(0, 0, 2), Color.RED);
		assertFalse(red.canUse(key2));

		// Tests that a player holding nothing (null) can pop an item out of the container.
		assertTrue(red.canUse(null));	

		
	}
	
	@Test
	public void testUse(){
		cube = new RiemannCube(new Int3(4, 4, 4));
		
		// Create GlobalHolder for Red containers.
		cube.containers.put(Color.RED, new GlobalHolder());
		
		// Create a red container
		Container red = new Container(cube.getCube(0, 0, 0), Color.RED, cube);
		
		// Create a new key and add it to the container
		Key key = new Key(cube.getCube(0, 0, 1), Color.RED);
		
		// When adding an item, a null should be returned
		assertEquals(null, red.use(key));
		
		// Tests that a player using a container, not carrying an item, will pick up the item in the container.
		assertEquals(key, red.use(null));
		
		// Test that the container is now empty
		assertEquals(null, cube.containers.get(key.colour()).getItem());
		
		// Test that adding an item after it has been removed still works
		assertEquals(null, red.use(key));
		assertEquals(key, cube.containers.get(key.colour()).getItem());
	}
}
