package MAIN;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.swing.*;

import utils.Int3;
import world.RiemannCube;
import world.objects.items.Key;
import world.objects.Lock;
import world.objects.Trigger;

import editor.EditorCanvas;
import editor.HelpFrame;
import data.LevelPipeline;

/**
 * Creates a new frame, for running the Level Editor in.
 * @author mudgejayd, sandilalex
 * 
 */
public class EditorFrame extends JFrame {

    private JPanel contentPane = new JPanel(new BorderLayout());
    
    private EditorCanvas canvas = new EditorCanvas();   //  Where the level is displayed
    private LevelPipeline pipe;     // Allows saving and loading the design the XML file.
    private String levelName = "New Level"; // Name of the file you are saving/loading from.

    public EditorFrame() {
        super("Level Editor");
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.addKeyListener(canvas);
        pack();
        setVisible(true);
        setFocusable(true);

        
        // Sets the frame to be full screen
        // ----------------
        Toolkit tk = Toolkit.getDefaultToolkit();  
        int xSize = ((int) tk.getScreenSize().getWidth());  
        int ySize = ((int) tk.getScreenSize().getHeight());  
        setSize(xSize,ySize);  
        // ----------------

        pipe = new LevelPipeline();

        addMenuBar();
        addSideBar();

        setContentPane(contentPane);
    }

    /**
     * Creates a menu bar on the North, for menu commands. Has: New, Save, Load.
     */
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // The main menu button --> File
        JMenu file = new JMenu("File");
        
        // "New" option in the menu bar, to create empty map.
        JMenuItem item = new JMenuItem("New");
        item.addActionListener(new ActionListener() {
            
            // Allows the user to make a new level, asks for the dimension.
            public void actionPerformed(ActionEvent e) {
                int width, height, depth;   // The dimensions of the level.
                
                try{
                    // Choose the dimesion of the level
                    width = Integer.parseInt(JOptionPane.showInputDialog(null, "Dimension?", "3"));
                    height = width;
                    depth = width;
                }catch(NumberFormatException nfe){
                    return;
                }
                
                canvas.setLevel(new RiemannCube(new Int3(width, height, depth)));   // Create the new Level (ie New RiemannCube)
                contentPane.add(canvas, BorderLayout.CENTER);
                setContentPane(contentPane);
            }
        });

        // Add "New" to the File button
        file.add(item);
        
        // "Save" option in the menu bar, to save to XML
        item = new JMenuItem("Save");
        item.addActionListener(new ActionListener() {
           
            // Saves the level to an XML file
            public void actionPerformed(ActionEvent e) {
                pipe = new LevelPipeline();
                
                // Lets the user choose the name for their level.
                String fname = JOptionPane.showInputDialog(null, "What name?", levelName);

                if(fname == null){
                    JOptionPane.showMessageDialog(null, "Enter a valid name for the level (no extension).");
                    return;
                }
                
                // Levels are saved with a file writer, initialize it here and pass it into LevelPipeline
                FileWriter filewriter;
                try {
                    filewriter = new FileWriter(new File("Levels/"+fname + ".xml"));
                    pipe.save(canvas.level(), filewriter);
                    filewriter.close();
                } catch (IOException e1) {
                    throw new Error(e1);
                }
            }
        });
        
        // Add "Save" to the File Button
        file.add(item);

        // Load option in the menu bar, to load an XML file into the editor
        item = new JMenuItem("Load");
        item.addActionListener(new ActionListener() {
            
            // Prompts the user to choose a file to load into the editor.s
            public void actionPerformed(ActionEvent e) {
                
                // The load method in LevelPipeline uses a JFileChooser to load files
                RiemannCube cube = pipe.load();
                levelName = pipe.getLastFileName();

                if (cube == null) {
                    JOptionPane.showMessageDialog(null, "File was not vaild.");
                } else {
                    canvas.setLevel(cube);
                    contentPane.add(canvas, BorderLayout.CENTER);
                    setContentPane(contentPane);
                }
            }
        });
        
        // Add "Load" to the File button
        file.add(item);

        // Creates the buttons that lets the user open a help screen.
        JMenu help = new JMenu("Help");
        item = new JMenuItem("Help Screen");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                // Creates a new JFrame that displays an image with instructions on how to use the editor
                new HelpFrame();
            }
        });

        // Add "Help" to the menu bar
        help.add(item);

        menuBar.add(file);
        menuBar.add(help);
        contentPane.add(menuBar, BorderLayout.NORTH);
    }

    /**
     * Creates a bar on the West side, complete with basic buttons for
     * performing important actions.
     */
    private void addSideBar() {
        // Changes the view perspective to be horizontal
        JPanel sideBar = new JPanel(new GridLayout(20, 1));
        JButton button = new JButton("View Horizontal");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.flip("horizontal");
                requestFocusInWindow();
            }
        });

        sideBar.add(button);

        // Changes the view perspective to be vertical
        button = new JButton("View Vertical");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.flip("vertical");
                requestFocusInWindow();
            }
        });
        
        sideBar.add(button);
        
        
        // Changes the view perspective to be orthogonal
        button = new JButton("View Orthogonal");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.flip("orthogonal");
                requestFocusInWindow();
            }
        });
        
        sideBar.add(button);
        
        // Moves the current slice up in the cube
        button = new JButton("Go up");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.changeFloor(1);
                requestFocusInWindow();
            }
        });
        
        sideBar.add(button);

        // Moves the current slice down in the cube
        button = new JButton("Go down");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.changeFloor(-1);
                requestFocusInWindow();
            }
        });
        
        sideBar.add(button);
        
        // Switch to Isomorphic view
        button = new JButton("View Isometric");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.changeIsometric();
                requestFocusInWindow();
            }
        });
        
        sideBar.add(button);
        
        sideBar.addKeyListener(canvas);
        contentPane.add(sideBar, BorderLayout.WEST);
    }

    public static void main(String[] args) {
        new EditorFrame();
    }
}
