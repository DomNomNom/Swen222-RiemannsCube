package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import data.LevelPipeline;
import data.XMLParser;

import utils.Int3;
import world.RiemannCube;
import world.objects.Player;

public class XMLParsingTests {

    RiemannCube riemann;
    @Test
    public void test() {
        riemann = new RiemannCube(new Int3(2, 2, 2));
        Player p = new Player(riemann.getCube(0,0,0), 1);
        riemann.getCube(0,0,0).addObject(p);
        LevelPipeline pipe = new LevelPipeline();
        File file = new File("testCase.xml");
        FileWriter filewriter;
        try {
           filewriter = new FileWriter(file);
           pipe.save(riemann, filewriter);
           RiemannCube load = XMLParser.readXML(file);
           assert(load.cubes[0][0][0].player().id()==1);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
            }
    }

}
