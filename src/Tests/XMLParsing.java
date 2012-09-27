package Tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import data.LevelPipeline;
import data.XMLParser;

import utils.Int3;
import world.RiemannCube;
import world.objects.Player;

public class XMLParsing {

    RiemannCube riemann;
    @Test
    public void test() {
        riemann = new RiemannCube(2, 2, 2);
        Player p = new Player(1, new Int3(0,0,0));
        riemann.getCube(0,0,0).addObject(p);
        LevelPipeline pipe = new LevelPipeline();
        pipe.save(riemann, "testCase");
        try {
           RiemannCube load = XMLParser.readXML(new File("testCase.xml"));
           assert(load.cubes[0][0][0].player().id()==1);
        } catch (FileNotFoundException e) {e.printStackTrace();}
    }

}
