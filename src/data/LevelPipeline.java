package data;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import world.RiemannCube;
import world.cubes.Cube;
import world.objects.GameObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class LevelPipeline {

	public LevelPipeline(){
		
	}
	
	public void save(RiemannCube level, String fname){
		try {
			//Document we're writing XML to.
			int width = level.width(), height = level.height(), depth = level.depth();
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			//Transforms into proper XML format.
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(fname+".xml"));
	 
			// root elements
			Element rootElement = doc.createElement("map");
			doc.appendChild(rootElement);
			
			
			
			
			
			//Iterate over each orthogonal 'slice' of the cube
			for(int k = 0; k < depth; k++){
				Element slice = doc.createElement("slice");
				slice.setAttribute("z", String.valueOf(k));
				//doc.appendChild(slice);
				//iterate over each horizontal 'floor' of the slice
				for(int j = 0; j < height; j++){
					Element row = doc.createElement("floor");
					row.setAttribute("y", String.valueOf(j));
					//doc.appendChild(row);
					//iterate over the vertical, getting each 'cube' of the row
					for(int i = 0; i < width; i++){
						Cube curCube = level.getCube(i, j, k);
						Element cube = doc.createElement("cube");
						cube.setAttribute("x", String.valueOf(i));
						//Record the numerical value of the cube to the document.
						cube.setAttribute("type", String.valueOf(curCube.type()));
						
						for(GameObject obj : curCube.objects()){
							Element gameObj = doc.createElement("object");
							gameObj.setAttribute("type", obj.getClass().getName());
							gameObj.setAttribute("id", ""); //FIXME
						}
						
						row.appendChild(cube);
						slice.appendChild(row);
						rootElement.appendChild(slice);
					}
				}
			}
			
			source = new DOMSource(doc);
			transformer.transform(source, result);
			 
			System.out.println("File saved!");
			
		} catch (Exception e) {e.printStackTrace();}
	}
}

