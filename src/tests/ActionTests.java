package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.Int3;
import world.RiemannCube;
import world.events.PlayerMove;

public class ActionTests {

    @Test
    public void testMoveAction() {        
        RiemannCube world  = WorldTests.generateWorld();
        RiemannCube world2 = WorldTests.generateWorld();
        assertTrue(world2.applyAction(new PlayerMove(0, new Int3(1, 0, 0))));
        assertFalse(world.equals(world2));
    }

}
