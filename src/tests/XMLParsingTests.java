package tests;

import static org.junit.Assert.*;

import java.awt.Color;
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
        
        Player p = new Player(riemann.getCube(0,0,0), 1);
        riemann.getCube(0,0,0).addObject(p);
        

        try {
            filewriter = new FileWriter(file);
            pipe.save(riemann, filewriter);
            load = XMLParser.readXML(new FileInputStream(file));
            
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
    

}
