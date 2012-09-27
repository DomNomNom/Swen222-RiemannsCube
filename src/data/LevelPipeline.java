package data;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.util.Collections;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import world.objects.Door;
import world.objects.Player;
import world.objects.Trigger;
import world.RiemannCube;
import world.cubes.Cube;
import world.items.GameItem;
import world.items.Key;
import world.objects.GameObject;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * 
 * Saves a 3D map state to XML. Form of: <map> - depth, height, width <slice> -
 * z <floor> - y <cube> - x, type <player> - id <item> -type, id</>
 * <lightsource> - type</> <object> - type, id</> </> </> </> </> </> If player
 * is null, set number value to -1 Null items have type "null".
 * 
 * @author mudgejayd 300221669
 * 
 */
public class LevelPipeline {

    public void save(RiemannCube level, Writer writer) {
        try {
            // Document we're writing XML to.
            int width = level.size.x, height = level.size.y, depth = level.size.z;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Transforms into proper XML format.
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);

            // root elements
            Element rootElement = doc.createElement("riemann");
            rootElement.setAttribute("depth", String.valueOf(depth));
            rootElement.setAttribute("height", String.valueOf(height));
            rootElement.setAttribute("width", String.valueOf(width));
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

                        // Adds spawn location
                        if (curCube.isSpawnPoint()) {
                            cube.setAttribute("spawn", "true");
                        } else {
                            cube.setAttribute("spawn", "false");
                        }

                        // Save object on square, if exists.
                        GameObject obj = curCube.object();
                        if (obj != null) {
                            Element gameObj = getObjectElement(obj, doc);
                            cube.appendChild(gameObj);
                        }

                        // Create the player
                        Player curPlayer = curCube.player();
                        Element player = doc.createElement("player");
                        if (curPlayer != null) {
                            player.setAttribute("id",
                                    String.valueOf(curPlayer.id()));

                            // Add non-lightsource items
                            GameItem curItem = curPlayer.item();
                            Element item;
                            if (curItem != null) {
                                item = doc.createElement(curItem
                                        .getClassName());
                            }
                            
//                            if (curItem != null) {
//                                item.setAttribute(curItem.getClassName(), item
//                                        .getClass().getName());
//
//                                player.appendChild(item);
//                            }

                            // Players dont have light sources anymore
                            // // add lightsource
                            // GameItem curLight = curPlayer.torch();
                            // Element light = doc.createElement("lightsource");
                            // if (curLight != null) {
                            // light.setAttribute("type", item.getClass()
                            // .getName());
                            //
                            // player.appendChild(light);
                            // }

                            cube.appendChild(player);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RiemannCube load() {
        JFileChooser fc = new JFileChooser(".");
        File f = null;

        int value = fc.showDialog(null, "Select File");
        if (value == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
        }

        RiemannCube cube = null;
        if (f.exists()) {
            try {
                cube = XMLParser.readXML(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return cube;
    }

    private Element getObjectElement(GameObject obj, Document doc) {
        Element element;
        if (obj instanceof Door) {
            Door door = (Door) obj;
            element = doc.createElement("door");
            String triggerIDs = "";
            for (Integer i : door.triggers())
                triggerIDs += i.toString() + " ";
            element.setAttribute("triggerIDs", triggerIDs);
            element.setAttribute("color", hexCode(door.color()));
        } else if (obj instanceof Trigger) {
            element = doc.createElement(obj.getClassName().toLowerCase());
            element.setAttribute("id", String.valueOf(((Trigger) obj).getID()));
            element.setAttribute("color", hexCode(((Trigger) obj).color()));
        } else if (obj instanceof Key) {
            element = doc.createElement(obj.getClassName().toLowerCase());
            element.setAttribute("color", hexCode(((Key) obj).colour()));
        } else {
            element = doc.createElement(obj.getClassName());
        }

        return element;
    }

    private String hexCode(Color c) {
        String s = Integer.toHexString(c.getRGB() & 0xffffff);
        if (s.length() < 6)
            s = "000000".substring(0, 6 - s.length()) + s;
        return '#' + s;
    }
}
