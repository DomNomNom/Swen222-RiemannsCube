package tests;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.HashSet;

import org.junit.Test;

import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.objects.Container;
import world.objects.doors.Door;
import world.objects.doors.LevelDoor;
import world.objects.GlobalHolder;
import world.objects.items.GameItem;
import world.objects.items.Key;

/**
 * Tests that test one game object at a time
 *
 * @author schmiddomi
 */
public class GameObjectTests {

    @Test
    public void testSimpleAddRemove() {
        RiemannCube world = WorldTests.generateWorld();
        Cube cube = world.getCube(0, 0, 0);
        Container c  = new Container(cube, Color.BLACK, world.containers);
        cube.addObject(c);

        assertFalse(cube.canUseItem(null)); // we can't get anything from the container yet
        assertFalse(c.canUse(null));

        GameItem newItem  = new Key(cube, Color.RED );
        GameItem newItem2 = new Key(cube, Color.BLUE);
        
        // we can put stuff in
        assertTrue(cube.canUseItem(newItem ));
        assertTrue(cube.canUseItem(newItem2));
        assertTrue(c.canUse(newItem ));
        assertTrue(c.canUse(newItem2));
        
        //cube.useItem(newItem); // put in
        c.use(newItem);
        
        // now we shouldn't be able to add
        assertFalse(cube.canUseItem(newItem ));
        assertFalse(cube.canUseItem(newItem2));
        assertFalse(c.canUse(newItem ));
        assertFalse(c.canUse(newItem2));
        
        // now we should be able to remove
        assertTrue(c.canUse(null));
        assertTrue(cube.canUseItem(null));
        
        GameItem out = cube.useItem(null); // get item out
        
        assertTrue(out == newItem); // out == in
        
        // now we should be where we started
        assertFalse(cube.canUseItem(null));
        assertFalse(c.canUse(null));
        assertTrue(cube.canUseItem(newItem ));
        assertTrue(cube.canUseItem(newItem2));
        assertTrue(c.canUse(newItem ));
        assertTrue(c.canUse(newItem2));
    }
    
    @Test
    public void testContainerCanUse(){
        RiemannCube cube = new RiemannCube(new Int3(4, 4, 4));
        
        // Create GlobalHolder for Red containers.
        cube.containers.put(Color.RED, new GlobalHolder());
        
        // Create a red container
        Container red = new Container(cube.getCube(0, 0, 0), Color.RED, cube.containers);
        
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
    public void testContainerUse(){
        RiemannCube cube = new RiemannCube(new Int3(4, 4, 4));
        
        // Create GlobalHolder for Red containers.
        cube.containers.put(Color.RED, new GlobalHolder());
        
        // Create a red container
        Container red = new Container(cube.getCube(0, 0, 0), Color.RED, cube.containers);
        
        // Create a new key and add it to the container
        Key key = new Key(cube.getCube(0, 0, 1), Color.RED);
        
        // When adding an item, a null should be returned
        assertEquals(null, red.use(key));
        
        // Tests that a player using a container, not carrying an item, will pick up the item in the container.
        assertEquals(key, red.use(null));
        
        // Test that the container is now empty
        assertEquals(null, cube.containers.get(key.color()).getItem());
        
        // Test that adding an item after it has been removed still works
        assertEquals(null, red.use(key));
        assertEquals(key, cube.containers.get(key.color()).getItem());
    }

    
    @Test
    public void testDoors() {
        RiemannCube world = WorldTests.generateWorld();
        Cube cube = world.getCube(0, 0, 1);
        Door door = new LevelDoor(cube, world.triggers, Color.RED);
        
    }
}
