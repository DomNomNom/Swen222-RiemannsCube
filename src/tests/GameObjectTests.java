package tests;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.HashSet;

import org.junit.Test;

import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.events.Action;
import world.events.ItemUseStop;
import world.events.ItemPickup;
import world.events.ItemUseStart;
import world.events.PlayerMove;
import world.objects.Container;
import world.objects.Lock;
import world.objects.Trigger;
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

        assertFalse(cube.canUseItemStart(null)); // we can't get anything from the container yet
        assertFalse(c.canUseStart(null));

        GameItem newItem  = new Key(cube, Color.RED );
        GameItem newItem2 = new Key(cube, Color.BLUE);
        
        // we can put stuff in
        assertTrue(cube.canUseItemStart(newItem ));
        assertTrue(cube.canUseItemStart(newItem2));
        assertTrue(c.canUseStart(newItem ));
        assertTrue(c.canUseStart(newItem2));
        
        //cube.useItem(newItem); // put in
        c.useStart(newItem);
        
        // now we shouldn't be able to add
        assertFalse(cube.canUseItemStart(newItem ));
        assertFalse(cube.canUseItemStart(newItem2));
        assertFalse(c.canUseStart(newItem ));
        assertFalse(c.canUseStart(newItem2));
        
        // now we should be able to remove
        assertTrue(c.canUseStart(null));
        assertTrue(cube.canUseItemStart(null));
        
        GameItem out = cube.useItemStart(null); // get item out
        
        assertTrue(out == newItem); // out == in
        
        // now we should be where we started
        assertFalse(cube.canUseItemStart(null));
        assertFalse(c.canUseStart(null));
        assertTrue(cube.canUseItemStart(newItem ));
        assertTrue(cube.canUseItemStart(newItem2));
        assertTrue(c.canUseStart(newItem ));
        assertTrue(c.canUseStart(newItem2));
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
        assertFalse(red.canUseStart(null));
        
        // Test that a player holding an item can add things to the container
        assertTrue(red.canUseStart(key));

        // Add the key, then check that it is now impossible to add something to the container
        red.useStart(key);
        Key key2 = new Key(cube.getCube(0, 0, 2), Color.RED);
        assertFalse(red.canUseStart(key2));

        // Tests that a player holding nothing (null) can pop an item out of the container.
        assertTrue(red.canUseStart(null));   

        
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
        assertEquals(null, red.useStart(key));
        
        // Tests that a player using a container, not carrying an item, will pick up the item in the container.
        assertEquals(key, red.useStart(null));
        
        // Test that the container is now empty
        assertEquals(null, cube.containers.get(key.color()).getItem());
        
        // Test that adding an item after it has been removed still works
        assertEquals(null, red.useStart(key));
        assertEquals(key, cube.containers.get(key.color()).getItem());
    }

    
    @Test
    public void testDoors() {
        RiemannCube world = WorldTests.generateWorld(); // player is at (0,0,0)
        Cube spwnCube = world.getCube(0, 0, 0);
        Cube doorCube = world.getCube(0, 0, 1);
        Cube lockCube = world.getCube(0, 1, 0);
        
        int lockID = 1;
        Trigger lock = new Lock(lockCube, lockID, world.triggers, Color.RED);
        world.triggers.put(1, lock);
        lockCube.addObject(lock);
        
        Door door = new LevelDoor(doorCube, world.triggers, Color.RED);
        doorCube.addObject(door);
        door.addTrigger(lock.getID());
        
        Key key = new Key(spwnCube, Color.RED);
        spwnCube.addObject(key);
        
        Action moveToDoorCube = new PlayerMove(0, doorCube.pos());

        assertFalse(world.isValidAction(moveToDoorCube)); // we shouldn't be able into the cube with a locked door
        assertTrue(door.isClosed());
        
        assertTrue(world.applyAction(new ItemPickup  (0                 )));
        assertTrue(world.applyAction(new PlayerMove  (0, lockCube.pos() )));
        assertTrue(world.applyAction(new ItemUseStart(0                 ))); // put the key in the lock (should unlock door)
        assertTrue(world.applyAction(new ItemUseStop  (0                 ))); // lock is locked again but door still remains open
        assertFalse(door.isClosed()); // this is a vital step for the door to keep track that it has been opened!
        assertTrue(world.applyAction(new PlayerMove(0, spwnCube.pos() ))); // move back
        assertTrue(world.applyAction(new PlayerMove(0, doorCube.pos() ))); // and now we're in the door cube :D
        
    }
}
