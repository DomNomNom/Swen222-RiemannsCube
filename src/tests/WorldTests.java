package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.cubes.Floor;
import world.events.PlayerMove;
import world.objects.Player;


public class WorldTests {

    /** returns a new World with a player(id=0) */
    public RiemannCube generateWorld() {
        RiemannCube world = new RiemannCube(new Int3(6, 6, 6));
        Player p = new Player(world.getCube(new Int3()), 0);
        world.players.put(p.id, p);
        world.getCube(new Int3()).addObject(p);
        return world;
    }
    
    @Test
    public void test() {
        RiemannCube world  = generateWorld();
        RiemannCube world2 = generateWorld();
        
        assertEquals(world, world);
        assertEquals(world, world2);
    }

    @Test
    public void testNoMoveAction() {        
        RiemannCube world  = generateWorld();
        RiemannCube world2 = generateWorld();
        assertTrue(world2.applyAction(new PlayerMove(0, new Int3())));
        assertTrue(world.equals(world2));
    }
    
    @Test
    public void testMoveAction() {        
        RiemannCube world  = generateWorld();
        RiemannCube world2 = generateWorld();
        assertTrue(world2.applyAction(new PlayerMove(0, new Int3(1, 0, 0))));
        //assertFalse(world.equals(world2)); FIXME
    }
}
