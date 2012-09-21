package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import world.RiemannCube;
import world.cubes.Cube;
import world.cubes.Floor;
import world.cubes.Space;
import world.cubes.Wall;

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
        //Ignore this, it just sets up the DOM parser. Truth is, I don't even understand it. /Shrug
        //==========================================================
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
        //===========================================================

        Node root = doc.getDocumentElement();
        System.out.println(root.getNodeName());

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
//            System.out.println(slices.item(z).getNodeName());

            NodeList floors = slices.item(z).getChildNodes();

            h = 0;
            for (int y = 1; y < floors.getLength(); y += 2) {
//                System.out.println(floors.item(y).getNodeName());

                NodeList cubes = floors.item(y).getChildNodes();
                
                w = 0;
                for (int x = 1; x < cubes.getLength(); x += 2) {
//                    System.out.println(cubes.item(x).getNodeName());
                    
//                    System.out.println(w + " " + h + " " + d);
                    
                    Element c = (Element) cubes.item(x);
                    int type = Integer.parseInt(c.getAttribute("type"));
                    
                    if(type == 0){
                        Cube cube = new Space();
                        riemannCube.setCube(w, h, d, cube);
                    } else if(type == 1){
                        Cube cube = new Floor();
                        riemannCube.setCube(w, h, d, cube);
                    } else if(type == 1){
                        Cube cube = new Wall();
                        riemannCube.setCube(w, h, d, cube);
                    }
                    
                    w++;
                }

                h++;
            }
            d++;
        }

        return null;
    }
}
