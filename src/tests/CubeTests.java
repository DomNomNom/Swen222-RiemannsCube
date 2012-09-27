package tests;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import utils.Int3;
import world.cubes.Floor;
import world.cubes.Wall;
import world.items.Key;

public class CubeTests {

    @Test
    public void test() {
        Floor f1 = new Floor(new Int3());
        Floor f2 = new Floor(new Int3());
        Wall w1 = new Wall(new Int3());
        

        assertTrue(f1.equals(f1));
        assertTrue(f1.equals(f2));
        assertFalse(f1.equals(w1));
        assertFalse(w1.equals(f1));
    }
    
    @Test
    public void testElementEquality() {
        Floor f1 = new Floor(new Int3());
        Floor f2 = new Floor(new Int3());
        f1.addObject(new Key(f1, new Color(0)));
        assertFalse(f1.equals(f2));
        assertFalse(f2.equals(f1));
        f1.addObject(new Key(f1, new Color(0))); // TODO: automatic adding
        assertTrue(f1.equals(f2));
        assertTrue(f2.equals(f1));
    }

}
