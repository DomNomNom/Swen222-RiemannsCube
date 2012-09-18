package data;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import world.Player;
import world.RiemannCube;
import world.cubes.Cube;
import world.items.GameItem;
import world.objects.GameObject;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Saves a 3D map state to XML. Form of:
 *<map> 
 * <slice>
 * 	<floor>
 *   <cube>
 *    <object></>
 *    <player>
 *     <item></>
 *     <lightsource></>
 *    </>
 *   </>
 *  </>
 * </>
 *</>
 * If player is null, set number value to -1
 * Null items have type "null".
 *     
 * @author mudgejayd 300221669
 *
 */
public class LevelPipeline {
	
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
	 
			// root element
			Element rootElement = doc.createElement("map");
            rootElement.setAttribute("width", String.valueOf(level.width()));
            rootElement.setAttribute("height", String.valueOf(level.height()));
            rootElement.setAttribute("depth", String.valueOf(level.depth()));
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
						
						GameObject obj = curCube.object();
						//Adding object
						Element gameObj = doc.createElement("object");
						if(obj!=null){
							gameObj.setAttribute("type", obj.getClass().getName());
							gameObj.setAttribute("id", String.valueOf(obj.color().getRGB()));
							cube.appendChild(gameObj);
						}else{
							gameObj.setAttribute("type", "null");
							gameObj.setAttribute("id", "0");
						}
						
						//Adding player
						Player curPlayer = curCube.player();
						Element player = doc.createElement("player");
						if(curPlayer!=null){
							player.setAttribute("num", String.valueOf(curPlayer.num()));
							
							GameItem curItem = curPlayer.item();
							//Adding item
							Element item = doc.createElement("item");
							if(curItem!=null){
								item.setAttribute("type", item.getClass().getName());
							}else item.setAttribute("type", "null");
							
							player.appendChild(item);
							
							GameItem curTorch = curPlayer.torch();
                            //Adding item
                            Element torch = doc.createElement("lightsource");
                            if(curTorch!=null){
                                torch.setAttribute("type", item.getClass().getName());
                            }else item.setAttribute("type", "null");
                            
                            player.appendChild(torch);
						}else{
							player.setAttribute("num", "-1");
						}
						
						cube.appendChild(player);
						
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
	
	public void load(){
		JFileChooser fc = new JFileChooser("src");
		File f = null;
		
		int value = fc.showDialog(null, "Select File");
		if(value == JFileChooser.APPROVE_OPTION){
			f = fc.getSelectedFile();
		}
		
		if(f != null){
			XMLParser parser = new XMLParser(f);
		}
	}
}

