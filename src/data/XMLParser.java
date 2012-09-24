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

        Node root = doc.getDocumentElement();
//        System.out.println(root.getNodeName());

        Element dimensions = (Element) root;
        int width = Integer.parseInt(dimensions.getAttribute("width"));
        int height = Integer.parseInt(dimensions.getAttribute("height"));
        int depth = Integer.parseInt(dimensions.getAttribute("depth"));

        RiemannCube riemannCube = new RiemannCube(width, height, depth);

        int w = 0;
        int h = 0;
        int d = 0;

        NodeList slices = root.getChildNodes();

        for (int z = 1; z < slices.getLength(); z += 2) {
            NodeList floors = slices.item(z).getChildNodes();
            h = 0;
            
            for (int y = 1; y < floors.getLength(); y += 2) {
                NodeList cubes = floors.item(y).getChildNodes();
                w = 0;

                for (int x = 1; x < cubes.getLength(); x += 2) {
                    Element c = (Element) cubes.item(x);
                    int type = Integer.parseInt(c.getAttribute("type"));

                    Cube cube = null;

                    if (type == 0) {
                        cube = new Space();
                    } else if (type == 1) {
                        cube = new Floor();
                    } else if (type == 1) {
                        cube = new Wall();
                    }

                    NodeList obs = c.getChildNodes();
                    for (int o = 1; o < obs.getLength(); o += 2) {
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

        return null;
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
