package data;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import world.items.Key;
import world.items.LightSource;
import world.objects.Door;
import world.objects.GameObject;
import world.objects.Lock;
import world.objects.Player;

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
     * @throws FileNotFoundException
     */
    public static RiemannCube readXML(File file) throws FileNotFoundException {
        // Ignore this, it just sets up the DOM parser. Truth is, I don't even
        // understand it. /Shrug
        // ==========================================================
        if (!file.exists())
            throw new FileNotFoundException();

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
        int width = Integer.parseInt(dimensions.getAttribute("width"));
        int height = Integer.parseInt(dimensions.getAttribute("height"));
        int depth = Integer.parseInt(dimensions.getAttribute("depth"));

        RiemannCube riemannCube = new RiemannCube(width, height, depth);

        int w = 0;
        int h = 0;
        int d = 0;
        int spawnCount = 0; // Keeps track of how many spawn cubes have been added, so it can update the map in RiemannCube correctly.

        // Get all the slice nodes, so we can iterate through them getting all the floor nodes, then all the cube nodes.
        // Note, Slices are the vertical slices through the cube, starting at the front going to the back. 
        // Floors are the birds eye view of each horizontal slice of the cube.
        NodeList slices = root.getChildNodes();

        for (int z = 1; z < slices.getLength(); z += 2) {        // Iterate through the slice nodes
            NodeList floors = slices.item(z).getChildNodes();    // Get the floor nodes from each slice
            h = 0; 
            
            for (int y = 1; y < floors.getLength(); y += 2) {    // Iterate through the floor nodes
                NodeList cubes = floors.item(y).getChildNodes(); // Get the cubes on each Slice of the floor
                w = 0;

                for (int x = 1; x < cubes.getLength(); x += 2) { // Iterate through all the cubes on each slice/floor
                    Element c = (Element) cubes.item(x);
                    String type = c.getAttribute("type");
                    String spawn = c.getAttribute("spawn");
                    
                    Cube cube = null;

                    if (type.equals("Space")) {
                        cube = new Space();
                    } else if (type.equals("Floor")) {
                        cube = new Floor();
                    } else if (type.equals("Wall")) {
                        cube = new Wall();
                    } else if(type.equals("Glass")){
                        cube = new Glass();
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
                                riemannCube, new Int3(x, y, z)));
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
    private static GameObject createInternalObject(Node n,
            RiemannCube riemannCube, Int3 cubePos) {
        GameObject ret = null;

        //TODO Let parser also add the items the player is holding to the player.
        if (n.getNodeName().equals("player")) {
            //Get the ID for the player
            Element e = (Element) n;
            int id = Integer.parseInt(e.getAttribute("id"));
            
            ret = new Player(cubePos, id);

        } else if (n.getNodeName().equals("key")) {
            // Get the color for the Key
            Element e = (Element) n;
            String col = e.getAttribute("color");
            Color newCol = Color.decode(col);

            ret = new Key(newCol);

        } else if (n.getNodeName().equals("lock")) {
            // Get the color for the lock
            Element e = (Element) n;
            String col = e.getAttribute("color");
            Color newCol = Color.decode(col);

            // Get the ID for the Lock
            int id = Integer.parseInt(e.getAttribute("id"));

            ret = new Lock(id, newCol);

        } else if (n.getNodeName().equals("door")) {
            // Get the color for the door
            Element e = (Element) n;
            String col = e.getAttribute("color");
            Color newCol = Color.decode(col);

            // Get the set of IDs for the triggers of this door
            String ids = e.getAttribute("triggerIDs");
            Scanner scan = new Scanner(ids);

            Set<Integer> triggerIDs = new HashSet<Integer>();

            // Adding the trigger IDs from the attribute of the door
            while (scan.hasNext())
                triggerIDs.add(Integer.parseInt(scan.next()));

            ret = new Door(triggerIDs, riemannCube.triggers, newCol);

        } else if (n.getNodeName().equals("lightsource")) {
            ret = new LightSource();
        }

        return ret;
    }
}
