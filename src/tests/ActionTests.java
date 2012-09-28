package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.Int3;
import world.RiemannCube;
import world.events.PlayerMove;
import world.events.PlayerSpawning;

public class ActionTests {

    @Test
    public void testMoveAction() {        
        RiemannCube world  = WorldTests.generateWorld();
        RiemannCube world2 = WorldTests.generateWorld();
        assertTrue(world2.applyAction(new PlayerMove(0, new Int3(1, 0, 0))));
        assertFalse(world.equals(world2));
    }
    
    @Test
    public void testSpawnAction() {        
        RiemannCube world  = WorldTests.generateWorld();
        RiemannCube world2 = WorldTests.generateWorld();
        
        assertTrue(world.applyAction(new PlayerSpawning(1, new Int3(1, 0, 0))));
        assertFalse(world.equals(world2));
        
        assertFalse(world.applyAction(new PlayerSpawning(1, new Int3(1, 0, 0)))); // duplicate IDs
        assertFalse(world.applyAction(new PlayerSpawning(1, new Int3(2, 0, 0))));

        assertTrue(world2.applyAction(new PlayerSpawning(1, new Int3(1, 0, 0))));
        assertTrue(world.equals(world2));
    }

}
