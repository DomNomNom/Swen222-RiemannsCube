package tests;

import static org.junit.Assert.*;

import java.awt.Color;

import org.junit.Test;

import world.cubes.Floor;
import world.cubes.Wall;
import world.items.Key;

public class CubeTests {

    @Test
    public void test() {
        Floor f1 = new Floor();
        Floor f2 = new Floor();
        Wall w1 = new Wall();
        

        assertTrue(f1.equals(f1));
        assertTrue(f1.equals(f2));
        assertFalse(f1.equals(w1));
        assertFalse(w1.equals(f1));
        
        
    }
    
    @Test
    public void testElementEquality() {
        Floor f1 = new Floor();
        Floor f2 = new Floor();
        f1.addObject(new Key(new Color(0)));
        assertFalse(f1.equals(f2));
        assertFalse(f2.equals(f1));
        f1.addObject(new Key(new Color(0)));
        assertTrue(f1.equals(f2));
        assertTrue(f2.equals(f1));
    }

}
