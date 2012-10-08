package data;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.cubes.Floor;
import world.cubes.Glass;
import world.cubes.Space;
import world.cubes.Wall;
import world.objects.items.Key;
import world.objects.items.LightSource;
import world.objects.GameObject;
import world.objects.Lock;
import world.objects.Player;
import world.objects.doors.Door;
import world.objects.doors.EntranceDoor;
import world.objects.doors.ExitDoor;
import world.objects.doors.LevelDoor;

/**
 * Class to parse an XMLFile into a 3D Array which can be loaded by the game.
 * 
 * @author sandilalex
 * 
 */
public class XMLParser {
    /**
     * Parses an XML file and returns the RiemannCube represented by it. Returns
     * null if the file doesn't exist or if the file is null.
     * 
     * @param file
     * @return
     */
    public static RiemannCube readXML(InputStream file) {
        // Ignore this, it just sets up the DOM parser. Truth is, I don't even
        // understand it. /Shrug
        // ==========================================================

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = null;

        try {
            doc = dBuilder.parse(file);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();
        // ===========================================================

        //Riemann Tag
        Node root = doc.getDocumentElement();

        //Get the dimension of the Cube, which is described in the XML file 
        // --> Attributes in the root node.
        Element dimensions = (Element) root;
        int width  = Integer.parseInt(dimensions.getAttribute("width"));
        int height = Integer.parseInt(dimensions.getAttribute("height"));
        int depth  = Integer.parseInt(dimensions.getAttribute("depth"));

        RiemannCube riemannCube = new RiemannCube(new Int3(width, height, depth));

        int w = 0;
        int h = 0;
        int d = 0;
        int spawnCount = 0; // Keeps track of how many spawn cubes have been added, so it can update the map in RiemannCube correctly.

        // Get all the slice nodes, so we can iterate through them getting all the floor nodes, then all the cube nodes.
        // Note, Slices are the vertical slices through the cube, starting at the front going to the back. 
        // Floors are the birds eye view of each horizontal slice of the cube.
        NodeList slices = root.getChildNodes();

        for (int z = 0; z < depth; ++z) {        // Iterate through the slice nodes
            NodeList floors = slices.item(z*2+1).getChildNodes();    // Get the floor nodes from each slice
            h = 0; 
            
            for (int y = 0; y < height; ++y) {    // Iterate through the floor nodes
                NodeList cubes = floors.item(y*2+1).getChildNodes(); // Get the cubes on each Slice of the floor
                w = 0;

                for (int x = 0; x < width; ++x) { // Iterate through all the cubes on each slice/floor
                    Element c = (Element) cubes.item(x*2+1);
                    String type = c.getAttribute("type");
                    String spawn = c.getAttribute("spawn");
                    
                    Cube cube = null;

                    if (type.equals("Space")) {
                        cube = new Space(new Int3(x, y, z));
                    } else if (type.equals("Floor")) {
                        cube = new Floor(new Int3(x, y, z));
                    } else if (type.equals("Wall")) {
                        cube = new Wall(new Int3(x, y, z));
                    } else if(type.equals("Glass")){
                        cube = new Glass(new Int3(x, y, z));
                    }
                    
                    if(spawn.equals("true")){
                        cube.setSpawnPoint(true);
                        riemannCube.spawnCubes.put(spawnCount, cube);
                        spawnCount++;
                    } else if(spawn.equals("false")){
                        cube.setSpawnPoint(false);
                    } else {
                        throw new Error("Something wrong happened in XMLParser, to do with spawn cubes");
                    }

                    NodeList obs = c.getChildNodes();
                    for (int o = 1; o < obs.getLength(); o += 2) {
//                        System.out.println("OBJECT: ---------> " + obs.item(o).getNodeName());
                        cube.addObject(createInternalObject(obs.item(o),
                                riemannCube, cube));
                    }

                    riemannCube.setCube(w, h, d, cube);
                    w++;
                }

                h++;
            }
            d++;
        }

        return riemannCube;
    }

    /**
     * Takes a node and creates and returns a GameObject represented by it.
     * There is potentially a lot of
     * 
     * @param n
     * @return GameObject
     */
    private static GameObject createInternalObject(Node n, RiemannCube riemannCube, Cube cube) {
        GameObject ret = null;

        //TODO Let parser also add the items the player is holding to the player.
        if (n.getNodeName().equals("player")) {
            //Get the ID for the player
            Element e = (Element) n;
            int id = Integer.parseInt(e.getAttribute("id"));
            
            ret = new Player(cube, id);

        } else if (n.getNodeName().equals("key")) {
            // Get the color for the Key
            Element e = (Element) n;
            String col = e.getAttribute("color");

            Color newCol = null;
            boolean exit = true;
            if(col.length() != 0){
                newCol = Color.decode(col);
                exit = false;
            }
            
            ret = new Key(cube, newCol);
            ((Key)ret).setExit(exit);

        } else if (n.getNodeName().equals("lock")) {
            // Get the color for the lock
            Element e = (Element) n;
            String col = e.getAttribute("color");
            
            Color newCol = null;
            boolean exit = true;
            if(col.length() != 0){
                newCol = Color.decode(col);
                exit = false;
            }

            // Get the ID for the Lock
            int id = Integer.parseInt(e.getAttribute("id"));

            ret = new Lock(cube, id, newCol);
            ((Lock)ret).setExit(exit);

        } else if (n.getNodeName().equals("door")) {
            // Get the color for the door
            Element e = (Element) n;
            String type = e.getAttribute("type");
            
            Color newCol = null;
            if (!type.equals("exit")) {
                String col = e.getAttribute("color");
                newCol = Color.decode(col);
            }

            // Get the set of IDs for the triggers of this door
            String ids = e.getAttribute("triggerIDs");
            Scanner scan = new Scanner(ids);

            Set<Integer> triggerIDs = new HashSet<Integer>();

            // Adding the trigger IDs from the attribute of the door
            while (scan.hasNext())
                triggerIDs.add(Integer.parseInt(scan.next()));

            if(type.equals("level")){
                ret = new LevelDoor(cube, triggerIDs, riemannCube.triggers, newCol);
            } else if(type.equals("entrance")){
                String levelName = e.getAttribute("levelname");
                ret = new EntranceDoor(cube, triggerIDs, riemannCube.triggers, newCol, levelName);
                
            } else if(type.equals("exit")){
                ret = new ExitDoor(cube, triggerIDs, riemannCube.triggers);
            }

        } else if (n.getNodeName().equals("lightsource")) {
            ret = new LightSource(cube);
        }

        return ret;
    }
}
