package tests;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import data.LevelPipeline;
import data.XMLParser;

import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.objects.items.Key;
import world.objects.Lock;
import world.objects.Player;
import world.objects.Trigger;
import world.objects.doors.Door;
import world.objects.doors.LevelDoor;

public class XMLParsingTests {


    RiemannCube riemann = new RiemannCube(new Int3(3, 3, 3));

    LevelPipeline pipe = new LevelPipeline();
    File file = new File("Levels/testCase.xml");
    FileWriter filewriter;
    
    RiemannCube load;
    
    @Before
    public void initMap(){

        Color col = Color.PINK;
        Door door = new LevelDoor(riemann.getCube(0,1,2), riemann.triggers, col);
        
        Trigger trig = new Lock(riemann.getCube(0,1,1), 1, riemann.triggers, col);
        door.addTrigger(1);
        Key key = new Key(riemann.getCube(0,1,0), col);

        riemann.getCube(0,1,2).addObject(door);
        riemann.getCube(0,1,1).addObject(trig);
        riemann.getCube(0,1,0).addObject(key);
        
        trig = new Lock(riemann.getCube(0,2,1), 2, riemann.triggers, col);
        
        riemann.getCube(0,2,1).addObject(trig);
        riemann.getCube(0,2,0).addObject(key);
        
        Player p = new Player(riemann.getCube(0,0,0), 1, "placeHolder");
        riemann.getCube(0,0,0).addObject(p);
        

        try {
            filewriter = new FileWriter(file);
            pipe.save(riemann, filewriter);
            load = XMLParser.readXML(new FileInputStream(file));
            
            //assertTrue(load.equals(riemann));
         } catch (Exception e) {
             e.printStackTrace();
             fail();
         }
    }
    
    
    @Test
    public void testPlayers() {
       assert(load.cubes[0][0][0].player().id()==1);
    }
    
    @Test
    public void testObjectsLoading() {
        assertTrue(load.cubes[0][1][2].object() instanceof Door);
        assertTrue(load.cubes[0][1][1].object() instanceof Lock);
    }
    
    @Test
    public void testObjectsAssociating() {
        Door door = (Door) load.cubes[0][1][2].object();
        Lock lock = (Lock) load.cubes[0][1][1].object();
        
        if(door.triggersIDs()!=null)
            assertTrue(door.triggersIDs().contains(lock.getID()));
        else fail("Need to initialise door.triggers()");
    }
    
    @After
    public void deleteFile(){
        file.delete();
    }
    

    
    // === Doms tests ===
    
    @Test
    /** very descriptive name */
    public void testStuff() {
        RiemannCube world = WorldTests.generateWorld();
//        assertTrue(world.equals(reParse(world))); // the base case
//        assertTrue(world != reParse(world)); // a small test of re-parse
        
        // now lets add some more suff
        Cube keyCube  = world.getCube(0, 0, 0);
        Cube doorCube = world.getCube(0, 0, 1);
        Cube lockCube = world.getCube(0, 1, 0);
        
        int lockID = 1;
        Trigger lock = new Lock(lockCube, lockID, world.triggers, Color.RED);
        world.triggers.put(1, lock);
        lockCube.addObject(lock);
//        assertTrue(world.equals(reParse(world)));
        
        Door door = new LevelDoor(doorCube, world.triggers, Color.RED);
        doorCube.addObject(door);
        door.addTrigger(lock.getID());
        
        assertEquals(world.toString(), reParse(world).toString());
        
        assertTrue(world.equals(reParse(world)));
        
        Key key = new Key(keyCube, Color.RED);
        keyCube.addObject(key);

        
        assertTrue(world.equals(reParse(world)));
    }
    
    /** Serialises and then unserialises the RC and returns the result */
    public RiemannCube reParse(RiemannCube world) {
        return XMLParser.readXML(new ByteArrayInputStream(world.toString().getBytes()));
    }
}
