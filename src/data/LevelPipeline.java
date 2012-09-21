package data;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import world.objects.Door;
import world.objects.Player;
import world.objects.Trigger;
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
 *<map> - width, height, depth 
 * <slice> - z
 * 	<floor> - y
 *   <cube> - x, type
 *    <object>type, id</>
 *    <player> - number
 *     <item>type, id</>
 *     <lightsource>type</>
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

    public LevelPipeline() {

    }

    public void save(RiemannCube level, String fname) {
        try {
            // Document we're writing XML to.
            int width = level.width, height = level.height, depth = level.depth;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Transforms into proper XML format.
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(fname + ".xml"));

            // root elements
            Element rootElement = doc.createElement("riemann cube");
            rootElement.setAttribute("width", String.valueOf(width));
            rootElement.setAttribute("height", String.valueOf(height));
            rootElement.setAttribute("depth", String.valueOf(depth));
            doc.appendChild(rootElement);

            // Iterate over each orthogonal 'slice' of the cube
            for (int k = 0; k < depth; k++) {
                Element slice = doc.createElement("slice");
                slice.setAttribute("z", String.valueOf(k));
                // doc.appendChild(slice);
                // iterate over each horizontal 'floor' of the slice
                for (int j = 0; j < height; j++) {
                    Element row = doc.createElement("floor");
                    row.setAttribute("y", String.valueOf(j));
                    // doc.appendChild(row);
                    // iterate over the vertical, getting each 'cube' of the row
                    for (int i = 0; i < width; i++) {
                        Cube curCube = level.getCube(i, j, k);
                        Element cube = doc.createElement("cube");
                        cube.setAttribute("x", String.valueOf(i));
                        // Record the numerical value of the cube to the
                        // document.
                        cube.setAttribute("type",
                                String.valueOf(curCube.type()));

                        GameObject obj = curCube.object();
                        if (obj != null) {
                            Element gameObj = getObjectElement(obj, doc);
                        }

                        Player curPlayer = curCube.player();
                        Element player = doc.createElement("player");
                        if (curPlayer != null) {
                            player.setAttribute("num",
                                    String.valueOf(curPlayer.id()));

                            GameItem curItem = curPlayer.item();
                            Element item = doc.createElement("item");
                            if (curItem != null) {
                                item.setAttribute("type", item.getClass()
                                        .getName());
                            } else
                                item.setAttribute("type", "null");

                            player.appendChild(item);
                        } else {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        JFileChooser fc = new JFileChooser("src");
        File f = null;

        int value = fc.showDialog(null, "Select File");
        if (value == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
        }

        if (f != null) {

        }
    }
    
    private Element getObjectElement(GameObject obj, Document doc){
        Element element;
        if(obj instanceof Door){
            Door door = (Door) obj;
            element = doc.createElement("door");
            element.setAttribute("id", String.valueOf(door.id()));
        }else if(obj instanceof Trigger){
            element = doc.createElement(obj.getClassName().toLowerCase());
            element.setAttribute("id", String.valueOf(((Trigger)obj).id()));
        }else{
            element = doc.createElement(obj.getClassName());
        }
        return element;
    }
}
