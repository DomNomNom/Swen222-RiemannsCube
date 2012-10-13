package tests;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import world.RiemannCube;
import world.events.FullStateUpdate;
import data.XMLParser;

public class EventTests {

    /**
     * This simulates the server switching a level without all the server/client/networking complications
     */
    @Test
    public void testFullStateUpdate() {
        RiemannCube world1 = WorldTests.generateWorld();
        
        FullStateUpdate event = new FullStateUpdate(world1.toString());
        
        // ==== Here, the event would get sent over the network and received at the other client(s) ====
        
        RiemannCube world2 = XMLParser.readXML(new ByteArrayInputStream(event.level.getBytes()));
        assertTrue(world1.equals(world2));
    }

}
