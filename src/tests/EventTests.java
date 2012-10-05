package tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import org.junit.Test;

import data.LevelPipeline;
import data.XMLParser;

import world.RiemannCube;
import world.events.FullStateUpdate;

public class EventTests {

    @Test
    public void testFullStateUpdate() {
        RiemannCube world1 = WorldTests.generateWorld();
        LevelPipeline saveLoader = new LevelPipeline();
        
        StringWriter out = new StringWriter();
        saveLoader.save(world1, out);
        
        FullStateUpdate event = new FullStateUpdate(out.toString());
        
        // ==== Here the event would get sent over the network and received at the other client(s) ====
        
        RiemannCube world2 = XMLParser.readXML(new ByteArrayInputStream(event.level.getBytes()));
        assertTrue(world1.equals(world2));
    }

}
