package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
		
		// More to do:
		// Needs dimension of the 3D array in the XML file
		Node cube = doc.getDocumentElement();
		System.out.println(cube.getNodeName());
		
		NodeList intern = cube.getChildNodes();
		
		System.out.println(intern.item(2).getNodeName());
		
		return null;
	}
}
