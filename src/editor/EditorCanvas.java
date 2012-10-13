package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import utils.Int3;
import world.RiemannCube;
import world.cubes.Cube;
import world.cubes.CubeType;
import world.cubes.Floor;
import world.cubes.Glass;
import world.cubes.Space;
import world.cubes.Wall;
import world.objects.Container;
import world.objects.GameObject;
import world.objects.Lock;
import world.objects.Button;
import world.objects.Trigger;
import world.objects.doors.Door;
import world.objects.doors.EntranceDoor;
import world.objects.doors.ExitDoor;
import world.objects.doors.LevelDoor;
import world.objects.items.Key;
import world.objects.items.Token;

/**
 * 
 * @author mudgejayd Allows user to design/edit their own levels.
 * 
 */
public class EditorCanvas extends JComponent implements MouseListener,  KeyListener {

    private RiemannCube level;
    private Cube[][] slice = new Cube[0][0];
    // x, y and z represent selected cube in 3D. i, j are selected square
    // and depend on your viewing angle.
    private int x, y, z, width, height, squareLength = 50, i = 0, j = 0, left = 350,
            top = 350;
    private String orientation = "horizontal";
    private boolean isometric = false;
    private Door curDoor = null;
    private Lock curLock = null;
    private Button curButton;
    private int triggerChoice;
    private int triggersLeftToPlace;
    private int triggerID = 0;
    private Set<Color> usedColors = new HashSet<Color>();
    
    private int numContainersPlaced;
    private Color curContainerColor;
    
    
    public EditorCanvas() {
        super();
        setPreferredSize(new Dimension(1000, 1000));
        addMouseListener(this);
    }

    /**
     * checks that we have placed all current triggers
     */
    private boolean allTriggersPlaced() {
        return triggersLeftToPlace == 0;
    }
    
    /**
     * Draws canvas according to the current view mode.
     */
    public void paintComponent(Graphics g) {
        if (level == null)
            return;

        if (isometric) {
            drawIsometric(g);
            return;
        }

        width = slice.length;
        height = slice[0].length;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                // Choose colour based on cube being drawn.
                if (slice[i][j].type() == CubeType.FLOOR) {
                    if (slice[i][j].isSpawnPoint()) {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                } else if (slice[i][j].type() == CubeType.WALL) {
                    g.setColor(Color.GRAY);
                } else if (slice[i][j].type() == CubeType.GLASS) {
                    g.setColor(new Color(202, 225, 255));
                }

                g.fillRect(left + i * squareLength, top + j * squareLength,
                        squareLength, squareLength);
                g.setColor(Color.BLACK);
                g.drawRect(left + i * squareLength, top + j * squareLength,
                        squareLength, squareLength);
                // draw top object.
                if (slice[i][j].object() != null)
                    drawObject(g, slice[i][j].object(),
                            left + i * squareLength, top + j * squareLength);
                if (slice[i][j].player() != null) {
                    g.drawString(slice[i][j].player().toString(), left + i
                            * squareLength, top + j * squareLength);
                }
            }

        if (orientation.equals("horizontal")) {
            i = x;
            j = z;
        } else if (orientation.equals("vertical")) {
            i = y;
            j = z;
        } else if (orientation.equals("orthogonal")) {
            i = x;
            j = y;
        }
        // Draw highlighted square.
        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect(left + i * squareLength, top + j * squareLength,
                squareLength, squareLength);
        g.setColor(Color.GREEN);
        g.drawRect(left + i * squareLength, top + j * squareLength,
                squareLength, squareLength);
    }

    /**
     * Draw an object at the (x,y) co-ordinate.
     * 
     * @param g
     *            Graphics shown on canvas
     * @param obj
     *            Object to be drawn on
     * @param x
     *            Co-ordinate
     * @param y
     *            Co-ordinate
     */
    private void drawObject(Graphics g, GameObject obj, int x, int y) {
        if (obj instanceof LevelDoor) {
            g.setColor(((Door) obj).color());
            g.fillRect(x, y, squareLength, squareLength);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, squareLength, squareLength);
            return;
            
        }else if(obj instanceof ExitDoor){ //Exit Door
            g.setColor(Color.WHITE);
            g.fillRect(x + 1, y + 1, squareLength - 2, squareLength - 2);
            g.setColor(Color.RED);
            
            for(int i = 0; i < 4; i++)
                g.drawRect(x + i, y + i, squareLength - (i*2), squareLength - (i*2));
            
            g.setFont(new Font("Serif", Font.BOLD, 30));
            
            g.setColor(Color.BLACK);
            g.drawString("X", x + squareLength/4 + 5, y + 3*squareLength/4);
            g.drawRect(x, y, squareLength, squareLength);
            
        } else if (obj instanceof EntranceDoor) { //Entrance Door
            g.setColor(Color.WHITE);
            g.fillRect(x + 1, y + 1, squareLength - 2, squareLength - 2);
            g.setColor(((EntranceDoor) obj).color());
            
            for(int i = 0; i < 4; i++)
                g.drawRect(x + i, y + i, squareLength - (i*2), squareLength - (i*2));

            g.setFont(new Font("Serif", Font.BOLD, 30));

            g.setColor(Color.BLACK);
            g.drawString("E", x + squareLength/4 + 5, y + 3*squareLength/4);
            g.drawRect(x, y, squareLength, squareLength);
        } else if (obj instanceof Key) {           //Key
            if(((Key)obj).isExit()){
                g.setColor(Color.YELLOW);
                g.fillOval(x, y, squareLength, squareLength);

                g.setColor(Color.RED);
                for(int i = 0; i < 4; i++)
                    g.drawOval(x + i, y + i, squareLength - (i*2), squareLength - (i*2));

                g.setColor(Color.BLACK);
                g.setFont(new Font("Serif", Font.BOLD, 30));
                g.drawString("X", x + squareLength/4 + 5, y + 3*squareLength/4);
                g.drawOval(x, y, squareLength, squareLength);
            } else {
                g.setColor(Color.YELLOW);
                g.fillOval(x, y, squareLength, squareLength);
                g.setColor(((Key) obj).color());
                g.fillOval(x + squareLength / 4, y + squareLength / 4,
                        squareLength / 2, squareLength / 2);
                g.setColor(Color.BLACK);
                g.drawOval(x + squareLength / 4, y + squareLength / 4,
                        squareLength / 2, squareLength / 2);
                g.drawOval(x, y, squareLength, squareLength);
            }
            
        }else if(obj instanceof Token){         //Token
            g.setColor(Color.YELLOW);
            g.fillRect(x + squareLength/4, y + squareLength/4, squareLength/2, squareLength/2);
            g.setColor(Color.BLACK);
            g.drawRect(x + squareLength/4, y + squareLength/4, squareLength/2, squareLength/2);
            
        } else if(obj instanceof Container){	// Containers
        	Color col = ((Container) obj).color();
        	int red = col.getRed();
        	int blue = col.getBlue();
        	int green = col.getGreen();

        	// Fills an oval of the color of the container, getting darker towards the middle
			for (int i = 0; i < squareLength/2; i++) {
				if(red - (i + i/4) < 0){
					red = 0;
				} else {
					red -= (i + i/4);
				}
				if(green - (i + i/4) < 0) {
					green = 0;
				} else {
					green -= (i + i/4);
				}
				if(blue - (i + i/4) < 0){
					blue = 0;
				} else {
					blue -= (i + i/4);
				}
				g.setColor(new Color(red, green, blue));
				g.fillOval(x + i, y + i, squareLength - (i * 2), squareLength - (i * 2));
			}
                
        } else if (obj instanceof Trigger) {    //Trigger
            if(obj instanceof Lock){        //Lock
                if(((Lock)obj).isExit()){
                    g.setColor(Color.RED);
                    
                    for(int i = 0; i < 4; i++)
                        g.drawOval(x + i, y + i, squareLength - (i*2), squareLength - (i*2));
                    
                    g.setColor(Color.BLACK);
                    g.drawOval(x, y, squareLength, squareLength);

                        g.setFont(new Font("Serif", Font.BOLD, 30));
                        g.drawString("X", x + squareLength/4 + 5, y + 3*squareLength/4);
                } else {
                    g.setColor(((Lock) obj).color());
                    g.fillOval(x, y, squareLength, squareLength);
                    g.setColor(Color.BLACK);
                    g.drawOval(x, y, squareLength, squareLength);

                }
            } else if(obj instanceof Button){   //Button
                if(((Button)obj).isExit()){
                    g.setColor(Color.RED);
                    
                    for(int i = 0; i < 8; i++)
                        g.drawOval(x + i, y + i, squareLength - (i*2), squareLength - (i*2));
                    
                    g.setColor(Color.BLACK);
                    for(int i = 0; i < 4; i++)
                        g.drawOval(x + i, y + i, squareLength - (i*2), squareLength - (i*2));

                    g.setFont(new Font("Serif", Font.BOLD, 30));
                    g.drawString("X", x + squareLength/4 + 5, y + 3*squareLength/4);
                } else {
                    g.setColor(((Button) obj).color());
                    g.fillOval(x, y, squareLength, squareLength);
                    
                    g.setColor(Color.BLACK);
                    for (int i = 0; i < 4; i++)
                        g.drawOval(x + i, y + i, squareLength - (i * 2), squareLength - (i * 2));

                }
            }
            
        }
    }

    /**
     * Draws level isometrically, rather than with a square grid.
     * 
     * @param g
     *            Graphics shown on canvas
     */
    public void drawIsometric(Graphics g) {
        int top, height = level.size.y, width = level.size.x;
        int floorHeight = (width + height) * squareLength / 2; // Height of
                                                               // entire floor.
        float dWidth = (float) (squareLength * 3 / Math.sqrt(2)); // width of
                                                                  // diamond
        float left;
        Cube[][] slice;
        for (int floor = 0; floor < height; floor++) {
            slice = level.horizontalSlice(floor);
            for (int i = 0; i < width; i++) {
                // Draws top of column, then iterates down, and to the left.
                top = this.top + i * squareLength / 2 - floor * floorHeight
                        + floorHeight / 2 + height * floorHeight / 2;
                left = this.left + i * dWidth / 2 + height; // lefthand side of
                                                            // starting column.
                for (int j = 0; j < level.size.z; j++) {
                    // Chooses colour depending on cube type.
                    if (slice[i][j].type() == CubeType.FLOOR) {
                        if (slice[i][j].isSpawnPoint()) {
                            g.setColor(Color.RED);
                        } else {
                            g.setColor(Color.WHITE);
                        }
                    } else if (slice[i][j].type() == CubeType.WALL) {
                        g.setColor(Color.GRAY);
                    } else if (slice[i][j].type() == CubeType.GLASS) {
                        g.setColor(new Color(202, 225, 255));
                    }

                    //Highlight selected diamond
                    if (this.x == i && this.z == j && y == floor) {
                        int red = (int)(g.getColor().getRed()*0.5);
                        int green = (int)(g.getColor().getGreen()*0.9);
                        int blue = (int)(g.getColor().getBlue()*0.5);
                        g.setColor(new Color(red, green, blue));
                    }
                    drawDiamond(g, top, (int) left, squareLength);
                    if (slice[i][j].object() != null)
                        drawObject(g, slice[i][j].object(), (int) left
                                + squareLength / 2, top);
                    top += squareLength / 2;
                    left -= dWidth / 2;
                }
            }
        }
    }

    /**
     * Draw a single diamond section of the isometric grid.
     * 
     * @param g
     *            Graphics shown on canvas
     * @param top
     *            Top corner of the diamond
     * @param left
     *            Left corder of the diamond
     * @param height
     *            How wall the diamond is
     */
    public void drawDiamond(Graphics g, int top, int left, int height) {
        float width = (float) (3 / Math.sqrt(2));
        float middle = left + width * height / 2;
        float edge = middle;
        for (int y = top; y < top + height / 2; y++) {
            g.drawLine((int) edge, y, (int) (edge + 2 * (middle - edge)), y);
            edge -= width;
        }
        for (int y = top + height / 2; y < top + height; y++) {
            g.drawLine((int) edge, y, (int) (edge + 2 * (middle - edge)), y);
            edge += width;
        }
        g.setColor(Color.BLACK);
        g.drawLine((int) middle, top, left, top + height / 2);
        g.drawLine((int) middle, top, (int) (middle * 2 - left), top + height
                / 2);
        g.drawLine((int) middle, top + height, left, top + height / 2);
        g.drawLine((int) middle, top + height, (int) (middle * 2 - left), top
                + height / 2);
    }

    /**
     * Changes to and from isometric view.
     */
    public void changeIsometric() {
        this.isometric = !isometric;
        this.top = 350;
        this.left = 350;
        repaint();
    }

    /**
     * Chooses the level to edit.
     * 
     * @param level
     *            Level for editing.
     */
    public void setLevel(RiemannCube level) {
        if (level == null) {
            JOptionPane.showMessageDialog(null, "Level Was Null");
        }
        
        System.out.println(level.toString());
        
        this.level = level;
        slice = level.verticalSlice(0);
        x = 0;
        y = 0;
        z = 0;
        repaint();
    }

    /**
     * Current level being edited
     * 
     * @return Level being edited
     */
    public RiemannCube level() {
        return this.level;
    }

    public void zoom(float magnify) {
        squareLength = (int) (magnify * squareLength);
    }

    /**
     * Flip around currently selected point to chosen orientation.
     * 
     * @param orientation
     *            horizontal, vertical or orthogonal
     */
    public void flip(String orientation) {
        isometric = false;
        this.orientation = orientation;
        if (orientation.equals("horizontal")) {
            slice = level.horizontalSlice(y);
        } else if (orientation.equals("vertical")) {
            slice = level.verticalSlice(x);
        } else if (orientation.equals("orthogonal")) {
            slice = level.orthogonalSlice(z);
        }
        repaint();
    }

    /**
     * Changes which floor, within the same view.
     * 
     * @param step
     *            How many steps you take up (negative for down).
     */
    public void changeFloor(int step) {
        isometric = false;
        if (orientation.equals("horizontal")) {
            y += step;
            if (y < 0 || y >= height)
                y -= step;
            slice = level.horizontalSlice(y);
        } else if (orientation.equals("vertical")) {
            x += step;
            if (x < 0 || x >= width)
                x -= step;
            slice = level.verticalSlice(x);
        } else if (orientation.equals("orthogonal")) {
            z += step;
            if (z < 0 || z >= level.size.z)
                z -= step;
            slice = level.orthogonalSlice(z);
        }
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
        int i = e.getX() - left, j = e.getY() - top;
        if (!isometric) {
            if (orientation.equals("horizontal")) {
                x = i / squareLength;
                z = j / squareLength;
            } else if (orientation.equals("vertical")) {
                y = i / squareLength;
                z = j / squareLength;
            } else if (orientation.equals("orthogonal")) {
                x = i / squareLength;
                y = j / squareLength;
            }
        } else {
            return;
            // linear transform from isometric to grid-based.
            /*i = (int) (i - 3 * squareLength / (2 * Math.sqrt(2)));
            x = (int) ((i) * Math.sqrt(2) / 3 + j) / squareLength;
            z = (int) ((-i * Math.sqrt(2) / 3 + j) / squareLength);
            int floorHeight = (level.size.y + level.size.x + 1) * squareLength / 2;
            y = (e.getY() - top) / floorHeight;
            x -= y * level.size.x;
            z -= y * level.size.z;
            y = level.size.y - y - 3;
            this.i = x;
            this.j = z;
            System.out.println(x + " " + y + " " + z + " floorheight: "+ floorHeight);*/
        }
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP)
            top -= squareLength;
        else if (code == KeyEvent.VK_DOWN)
            top += squareLength;
        else if (code == KeyEvent.VK_LEFT)
            left -= squareLength;
        else if (code == KeyEvent.VK_RIGHT)
            left += squareLength;
        repaint();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
        char typed = e.getKeyChar();
        Int3 currentPos = new Int3(x, y, z);
        
        Cube currentCube = null;
        try{
            currentCube = level.getCube(currentPos);
        } catch(ArrayIndexOutOfBoundsException exc){
            JOptionPane.showMessageDialog(null, "You can only place things on the cube.");
            return;
        }
        
        if (typed == 'f') { //Floor
            level.setCube(x, y, z, new Floor(currentPos));
            
        } else if (typed == 'w') { //Wall
            level.setCube(x, y, z, new Wall(currentPos));
            
        } else if (typed == 's') { //Spawn point
            Cube spawnPoint = new Floor(new Int3(x, y, z));
            spawnPoint.setSpawnPoint(true);
            level.setCube(x, y, z, spawnPoint);
            
        } else if (typed == 'g') { //Glass
            level.setCube(x, y, z, new Glass(new Int3(x, y, z)));
            
        } else if (typed == ' ') { //Empty space
            level.setCube(x, y, z, new Space(new Int3(x, y, z)));
            
        } else if (level.getCube(x, y, z).object() == null) {
                //Need empty square to add object
                
            if (typed == 't'){ //Token
                level.getCube(x,y,z).addObject((new Token(level.getCube(x, y, z))));
			} else if (typed == 'c') { // Container
			    if(numContainersPlaced > 0){
			        level.getCube(x, y, z).addObject(new Container(level.getCube(x, y, z), curContainerColor, null));
			        numContainersPlaced--;
			        return;
			    }
			        
			    
			    numContainersPlaced = 0;
                try {
                    numContainersPlaced = Integer.parseInt(JOptionPane.showInputDialog(
                            null, "How many containers?", "2"));
                } catch (NumberFormatException numE) {
                    JOptionPane.showMessageDialog(null,
                            "Make sure you enter a number.");
                    return;
                }
			    
				curContainerColor = JColorChooser.showDialog(null, "Chooser a color", Color.WHITE);
				
				if (curContainerColor == null) {
				    numContainersPlaced = 0;
					return;
				}
				
				if(usedColors.contains(curContainerColor)){
				    JOptionPane.showMessageDialog(null,
                            "That Color has already been used.");
				    numContainersPlaced = 0;
                    return;
				}
				
				Color temp = curContainerColor;
				usedColors.add(temp);
				level.getCube(x, y, z).addObject(new Container(level.getCube(x, y, z), curContainerColor, null));
				numContainersPlaced--;
            } else if (typed == 'd') { //Door
                if (curDoor == null || allTriggersPlaced()) {
                    if (level.cubes[x][y][z].type() != CubeType.FLOOR) {
                        JOptionPane.showMessageDialog(null,
                                "You can only add objects to a floor cube.");
                        return;
                    }
                    
                    String[] choices = {"Level Door", "Entrance Door", "Exit Door"};
                    int choice = JOptionPane.showOptionDialog(null, "What type of door do you want to place?", "Door Type", JOptionPane.YES_NO_OPTION  
                            , JOptionPane.PLAIN_MESSAGE, null, choices, null);
                    
                    
                    Color col = null;
                    if (choice != 2) {
                        col = JColorChooser.showDialog(null,
                                "Choose a Color for the Door", Color.WHITE);
                        if (col == null) {
                            return;
                        }
                        if(usedColors.contains(col)){
                            JOptionPane.showMessageDialog(null,
                                    "That Color has already been used.");
                            return;
                        }
                            
                        usedColors.add(col);
                    }

                    String[] triggerType = {"Locks", "Buttons"};
                    triggerChoice = JOptionPane.showOptionDialog(null, "What type of trigger do you want?", "Door Type", JOptionPane.YES_NO_OPTION  
                            , JOptionPane.PLAIN_MESSAGE, null, triggerType, null);
                    
                    triggersLeftToPlace = 0;
                    try {
                        triggersLeftToPlace = Integer.parseInt(JOptionPane.showInputDialog(
                                null, "How many?", "2"));
                    } catch (NumberFormatException numE) {
                        JOptionPane.showMessageDialog(null,
                                "Make sure you enter a number.");
                        return;
                    }
                    
                    switch (choice) {
                    case 0:
                        curDoor = new LevelDoor(currentCube, level.triggers, col);
                        break;
                    case 1:
                        JFileChooser fc = new JFileChooser("Levels");
                        File f = null;

                        int value = fc.showDialog(null, "Select File");
                        if (value == JFileChooser.APPROVE_OPTION) {
                            f = fc.getSelectedFile();
                        }
                        
                        if(f == null)
                            return;
                        
                        curDoor = new EntranceDoor(currentCube, level.triggers, col, f.getName());
                        break;
                    case 2: 
                        curDoor = new ExitDoor(currentCube, level.triggers);
                        break;
                    }
                    level.getCube(x, y, z).addObject(curDoor);
                } else {
                    System.out
                            .println("Finish placing the locks for the last door!");
                }
            } else if(typed == 'b'){
                if (curDoor != null && !allTriggersPlaced()) {
                    if (level.cubes[x][y][z].type() != CubeType.FLOOR) {
                        JOptionPane.showMessageDialog(null,
                                "You can only add objects to a floor cube.");
                        return;
                    }

                    curButton = new Button(currentCube, triggerID++, level.triggers, curDoor.color());

                    if(curDoor instanceof ExitDoor){
                        curButton.setExit(true);
                    }

                    level.getCube(x, y, z).addObject(curButton);
                    curDoor.addTrigger(curButton.getID());
                    --triggersLeftToPlace;
                }
            }
            else if (typed == 'l') { //Lock
                if(triggerChoice == 1){
                    return;
                }
                
                if (curDoor != null && !allTriggersPlaced()) {
                    if (level.cubes[x][y][z].type() != CubeType.FLOOR) {
                        JOptionPane.showMessageDialog(null,
                                "You can only add objects to a floor cube.");
                        return;
                    }

                    curLock = new Lock(currentCube, triggerID++, level.triggers, curDoor.color());

                    if(curDoor instanceof ExitDoor){
                        curLock.setExit(true);
                    }

                    level.getCube(x, y, z).addObject(curLock);
                    curDoor.addTrigger(curLock.getID());
                    --triggersLeftToPlace;
                }
            } else if (typed == 'k') { //Key
                if (curLock != null) {
                    if (level.cubes[x][y][z].type() != CubeType.FLOOR) {
                        JOptionPane.showMessageDialog(null,
                                "You can only add objects to a floor cube.");
                        return;
                    }
                    
                    Key newKey = new Key(currentCube, curLock.color());
                    if(curLock.isExit()){
                        newKey.setExit(true);
                    }

                    level.getCube(x, y, z).addObject(newKey);
                    curLock = null;
                }
            } else if(typed == 'c') {   //Container
                
            }
        }
        if (!isometric)
            flip(orientation);
    }
}
