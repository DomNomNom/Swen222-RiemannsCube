package editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import utils.Int3;
import world.*;
import world.cubes.*;
import world.objects.*;
import world.items.*;

/**
 * 
 * @author mudgejayd 300221669 Allows user to design/edit their own levels.
 * 
 */
public class EditorCanvas extends JComponent implements MouseListener,
        KeyListener {

    private RiemannCube level;
    Cube[][] slice = new Cube[0][0];
    // x, y and z represent selected cube in 3D. i, j are selected square
    // and depend on your viewing angle.
    int x, y, z, width, height, squareLength = 50, i = 0, j = 0, left = 350,
            top = 350;
    String orientation = "horizontal";
    boolean isometric = false;
    Door curDoor = null;
    Lock curLock = null;
    int lockID = 0;
    Key curKey = new Key(Color.BLUE);

    public EditorCanvas() {
        super();
        setPreferredSize(new Dimension(1000, 1000));
        addMouseListener(this);
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
                if (slice[i][j].type() == 1)
                    g.setColor(Color.RED);
                if (slice[i][j].type() == 2)
                    g.setColor(Color.GRAY);
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
        if (obj instanceof Door) {
            g.setColor(((Door) obj).color());
            g.fillRect(x, y, squareLength, squareLength);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, squareLength, squareLength);
            return;
        }
        if (obj instanceof Key) {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, squareLength, squareLength);
            g.setColor(((Key) obj).colour());
            g.fillOval(x + squareLength/4, y + squareLength/4, squareLength/2, squareLength/2);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, squareLength, squareLength);
        } else if (obj instanceof Trigger) {
            g.setColor(((Lock) obj).color());
            g.fillOval(x, y, squareLength, squareLength);
            g.setColor(Color.BLACK);
            g.drawOval(x, y, squareLength, squareLength);
        }
    }

    /**
     * Draws level isometrically, rather than with a square grid.
     * 
     * @param g
     *            Graphics shown on canvas
     */
    public void drawIsometric(Graphics g) {
        int top, height = level.height, width = level.width;
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
                for (int j = 0; j < level.depth; j++) {
                    // Chooses colour depending on cube type.
                    if (slice[i][j].type() == 1)
                        g.setColor(Color.RED);
                    if (slice[i][j].type() == 2)
                        g.setColor(Color.GRAY);
                    if (this.x == i && this.z == j && y == floor) {
                        int red = g.getColor().getRed();
                        int green = g.getColor().getGreen();
                        int blue = g.getColor().getBlue();
                        g.setColor(new Color(red, blue, green, 100));
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
            if (z < 0 || z >= level.depth)
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
            // linear transform from isometric to grid-based.
            i = (int) (i - 3 * squareLength / (2 * Math.sqrt(2)));
            x = (int) ((i) * Math.sqrt(2) / 3 + j) / squareLength;
            z = (int) ((-i * Math.sqrt(2) / 3 + j) / squareLength);
            int floorHeight = (level.height + level.width + 1) * squareLength
                    / 2;
            y = (e.getY() - top) / floorHeight;
            x -= y * level.width;
            z -= y * level.width;
            y = level.height - y - 1;
            this.i = x;
            this.j = z;
            System.out.println(x + " " + y + " " + z);
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
        char key = e.getKeyChar();
        if (key == 'f') {
            level.setCube(x, y, z, new Floor());
        } else if (key == 'w') {
            level.setCube(x, y, z, new Wall());
        } else if (key == ' ') {
            level.setCube(x, y, z, new Space());
        } else if (key == '1') {
            level.cubes[x][y][z].addObject(new Player(new Int3(x, y, z), 1));
        } else if (level.getCube(x, y, z).object() == null) {
            if (key == 'd') {
                if (curDoor == null || curDoor.allTriggersPlaced()) {

                    Color col = JColorChooser.showDialog(null,
                            "Choose a Color for the Door", Color.WHITE);

                    int num = Integer.parseInt(JOptionPane.showInputDialog(
                            null, "How many locks?", "4"));
                    curDoor = new Door(num, col);
                    level.getCube(x, y, z).addObject(curDoor);
                } else {
                    System.out
                            .println("Finish placing the locks for the last door!");
                }
            } else if (key == 'l') {
                if (curDoor != null && !curDoor.allTriggersPlaced()
                        && curKey != null) {
                    curLock = new Lock(lockID++, curDoor.color());
                    level.getCube(x, y, z).addObject(curLock);
                    curDoor.addTrigger(curLock.getID());
                    curKey = null;
                }
            } else if (key == 'k') {
                if (curLock != null) {
                    curKey = new Key(curLock.color());
                    level.getCube(x, y, z).addObject(curKey);
                    curLock = null;
                }
            }
        }
        if (!isometric)
            flip(orientation);
    }
}
