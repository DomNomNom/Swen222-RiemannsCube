package data;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import world.objects.Container;
import world.objects.Player;
import world.objects.Trigger;
import world.RiemannCube;
import world.cubes.Cube;
import world.objects.items.GameItem;
import world.objects.items.Key;
import world.objects.items.Token;
import world.objects.GameObject;
import world.objects.doors.Door;
import world.objects.doors.EntranceDoor;
import world.objects.doors.ExitDoor;
import world.objects.doors.LevelDoor;

import javax.swing.JFileChooser;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
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
 * @author mudgejayd
 * 
 */
public class LevelPipeline {
    
    private String lastFileName;
    public String getLastFileName() {return lastFileName;}
    
    public void save(RiemannCube level, Writer writer) {
        try {
            // Document we're writing XML to.
            int width = level.size.x, height = level.size.y, depth = level.size.z;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            doc.normalize();

            // Transforms into proper XML format.
            TransformerFactory transformerFactory = TransformerFactory
                    .newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
              "{http://xml.apache.org/xslt}indent-amount", "4");
            
            DOMSource source = new DOMSource(doc);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
                            
                            player.setAttribute("name", curPlayer.name());

                            // Add item player is holding
                            GameItem curItem = curPlayer.item();
                            Element item;
                            if (curItem != null) {
                                item = getObjectElement(curItem, doc);
                                player.appendChild(item);
                            }
                            
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String format(String unformattedXml) {
        try {
            final Document document = parseXmlFile(unformattedXml);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RiemannCube load() {
        JFileChooser fc = new JFileChooser("Levels");
        File f = null;

        int value = fc.showDialog(null, "Select File");
        if (value == JFileChooser.APPROVE_OPTION) {
            f = fc.getSelectedFile();
        }
        
        if(f == null)
            return null;

        RiemannCube cube = null;
        if (f.exists()) {
            try {
                cube = XMLParser.readXML(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        lastFileName = f.getName();
        return cube;
    }

    private Element getObjectElement(GameObject obj, Document doc) {
        Element element = null;
        if (obj instanceof Door) {
            Door door = null;
            element = doc.createElement("door");
            if (obj instanceof LevelDoor) {
                door = (LevelDoor) obj;
                element.setAttribute("type", "level");
                element.setAttribute("color", hexCode(door.color()));
            } else if(obj instanceof EntranceDoor){
                door = (EntranceDoor) obj;
                element.setAttribute("type", "entrance");
                element.setAttribute("levelname", ((EntranceDoor)door).levelName());
                element.setAttribute("color", hexCode(door.color()));
            } else if(obj instanceof ExitDoor){
                door = (ExitDoor) obj;
                element.setAttribute("type", "exit");
            }
            
            String triggerIDs = "";
            for (Integer i : door.triggersIDs())
                triggerIDs += i.toString() + " ";
            element.setAttribute("triggerIDs", triggerIDs);

        } else if (obj instanceof Trigger) {
            element = doc.createElement(obj.getClassName().toLowerCase());
            element.setAttribute("id", String.valueOf(((Trigger) obj).getID()));
            element.setAttribute("color", hexCode(((Trigger) obj).color()));
        } else if (obj instanceof Key) {
            element = doc.createElement(obj.getClassName().toLowerCase());
            element.setAttribute("color", hexCode(((Key) obj).color()));
        } else if (obj instanceof Token) {
            element = doc.createElement(obj.getClassName().toLowerCase());
        } else if(obj instanceof Container){
            element = doc.createElement(obj.getClassName().toLowerCase());
            element.setAttribute("color", hexCode(((Container) obj).color()));
        } else {
            element = doc.createElement(obj.getClassName());
        }

        return element;
    }

    private String hexCode(Color c) {
        if(c == null)
            return null;
        
        String s = Integer.toHexString(c.getRGB() & 0xffffff);
        if (s.length() < 6)
            s = "000000".substring(0, 6 - s.length()) + s;
        return '#' + s;
    }

}
