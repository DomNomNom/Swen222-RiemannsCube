package tests;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.events.Action;
import world.events.ItemDrop;
import world.events.ItemPickup;
import world.events.ItemUse;
import world.events.PlayerMove;
import world.events.PlayerSpawning;
import world.objects.Container;
import world.objects.GameObject;
import world.objects.items.GameItem;
import world.objects.items.Key;

public class ActionTests {

    @Test
    public void testMoveAction() {        
        RiemannCube world  = WorldTests.generateWorld();
        RiemannCube world2 = WorldTests.generateWorld();
        assertTrue(world2.applyAction(new PlayerMove(0, new Int3(0, 0, 0)))); // move to current square
        assertTrue(world.equals(world2));
        assertTrue(world2.applyAction(new PlayerMove(0, new Int3(1, 0, 0)))); // move to adjacent square
        assertFalse(world.equals(world2));
        assertTrue(world2.applyAction(new PlayerMove(0, new Int3(0, 0, 0)))); // move back
        assertTrue(world.equals(world2));
    }
    
    @Test
    public void testSpawnAction() {
        RiemannCube world  = WorldTests.generateWorld();
        RiemannCube world2 = WorldTests.generateWorld();
        
        assertTrue(world.applyAction(new PlayerSpawning(1, new Int3(1, 0, 0), "placeHolder")));
        assertFalse(world.equals(world2));
        
        assertFalse(world.applyAction(new PlayerSpawning(1, new Int3(1, 0, 0), "placeHolder"))); // duplicate IDs
        assertFalse(world.applyAction(new PlayerSpawning(1, new Int3(2, 0, 0), "placeHolder")));

        assertTrue(world2.applyAction(new PlayerSpawning(1, new Int3(1, 0, 0), "placeHolder")));
        assertTrue(world.equals(world2));
    }

    
    @Test
    public void testPickupDropUse() {
        RiemannCube world  = WorldTests.generateWorld();
        Cube pos = world.getCube(0,0,0);
        
        GameObject containter = new Container(pos, Color.GREEN, world.containers);
        GameItem key  = new Key(pos, Color.BLUE);
        GameItem key2 = new Key(pos, Color.RED);
        assertTrue(pos.addObject(key       ));
        assertTrue(pos.addObject(key2      ));
        assertTrue(pos.addObject(containter));
        
        // new we should have a player, 2 keys and a container in the same cube.
        
        // these are the actions we are going to test
        Action pickup = new ItemPickup(0);
        Action use    = new ItemUse   (0); // this is both pickup and put in
        Action drop   = new ItemDrop  (0);

        assertTrue (world.isValidAction(pickup));
        assertFalse(world.isValidAction(use   ));
        assertFalse(world.isValidAction(drop  ));
        
        assertTrue(world.applyAction(pickup)); // pick up a key

        assertFalse(world.isValidAction(pickup));
        assertTrue (world.isValidAction(use   ));
        assertTrue (world.isValidAction(drop  ));
        
        assertTrue(world.applyAction(use)); // put the key in the container
        
        assertTrue (world.isValidAction(pickup));
        assertTrue (world.isValidAction(use   ));
        assertFalse(world.isValidAction(drop  ));
        
        assertTrue(world.applyAction(pickup)); // pick up second key
        
        assertFalse(world.isValidAction(pickup));
        assertFalse(world.isValidAction(use   )); // we can't put it in the container as the container is full
        assertTrue (world.isValidAction(drop  ));
        
        assertTrue(world.applyAction(drop)); // drop it again

        assertTrue (world.isValidAction(pickup));
        assertTrue (world.isValidAction(use   ));
        assertFalse(world.isValidAction(drop  ));
        
        assertTrue(world.applyAction(use)); // get the key out of the container (does also pick it up)
        
        assertFalse(world.isValidAction(pickup));
        assertTrue (world.isValidAction(use   ));
        assertTrue (world.isValidAction(drop  ));
    }
}
