package tests;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import world.RiemannCube;
import world.cubes.Cube;
import world.objects.Container;
import world.objects.items.GameItem;
import world.objects.items.Key;

public class GameObjectTests {

    @Test
    public void testSimpleAddRemove() {
        RiemannCube world = WorldTests.generateWorld();
        Cube cube = world.getCube(0, 0, 0);
        Container c  = new Container(cube, Color.BLACK, world);
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

}
