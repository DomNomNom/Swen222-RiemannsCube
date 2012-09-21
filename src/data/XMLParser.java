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
	public static RiemannCube readXML(File file) throws FileNotFoundException{
		if(!file.exists())
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
		
		Node cube = doc.getDocumentElement();
		System.out.println(cube.getNodeName());
		
		Element dimensions = (Element) cube;
		int width = Integer.parseInt(dimensions.getAttribute("width"));
		int height = Integer.parseInt(dimensions.getAttribute("height"));
		int depth = Integer.parseInt(dimensions.getAttribute("depth"));

		RiemannCube riemannCube = new RiemannCube(width, height, depth);
		
		NodeList slices = cube.getChildNodes();
		
		for(int z = 1; z < slices.getLength(); z += 2){
		    System.out.println(slices.item(z).getNodeName());
		    
		    NodeList floors = slices.item(z).getChildNodes();
		    
		    for(int y = 1; y < floors.getLength(); y += 2){
		        System.out.println(floors.item(y).getNodeName());
		        
		        NodeList cubes = floors.item(y).getChildNodes();
		        
		        for(int x = 1; x < cubes.getLength(); x += 2){
		            System.out.println(cubes.item(x).getNodeName());
		            
		            
		        }
		        
		    }
		}
		
		return null;
	}
}
