package tests;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;

import org.junit.Before;
import org.junit.Test;

import data.LevelPipeline;
import data.XMLParser;

import utils.Int3;
import world.RiemannCube;
import world.items.Key;
import world.objects.Door;
import world.objects.Lock;
import world.objects.Player;
import world.objects.Trigger;

public class XMLParsingTests {


    RiemannCube riemann = new RiemannCube(new Int3(3, 3, 3));

    LevelPipeline pipe = new LevelPipeline();
    File file = new File("testCase.xml");
    FileWriter filewriter;
    
    RiemannCube load;
    
    @Before
    public void initMap(){

        Color col = Color.PINK;
        Door door = new Door(riemann.getCube(0,1,2), 2, col);
        
        Trigger trig = new Lock(riemann.getCube(0,1,1), 1, col);
        door.addTrigger(1);
        Key key = new Key(riemann.getCube(0,1,0), col);

        riemann.getCube(0,1,2).addObject(door);
        riemann.getCube(0,1,1).addObject(trig);
        riemann.getCube(0,1,0).addObject(key);
        
        trig = new Lock(riemann.getCube(0,2,1), 2, col);
        
        riemann.getCube(0,2,1).addObject(trig);
        riemann.getCube(0,2,0).addObject(key);
        
        Player p = new Player(riemann.getCube(0,0,0), 1);
        riemann.getCube(0,0,0).addObject(p);
        

        try {
            filewriter = new FileWriter(file);
            pipe.save(riemann, filewriter);
            load = XMLParser.readXML(file);
            
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
        
        if(door.triggers()!=null)
            assertTrue(door.triggers().contains(lock.getID()));
        else fail("Need to initialise door.triggers()");
    }

}
