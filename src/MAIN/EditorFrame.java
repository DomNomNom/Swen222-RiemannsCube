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
 * 
 * @author mudgejayd 300221669 Creates a new frame, for running the Level Editor
 *         in.
 * 
 */
public class EditorFrame extends JFrame {

    JPanel contentPane = new JPanel(new BorderLayout());
    EditorCanvas canvas = new EditorCanvas();
    LevelPipeline pipe;
    String levelName = "New Level";

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
     * Creates a menu bar on the North, for menu commands.
     */
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        // "New" option in the menu bar, to create empty map.
        JMenuItem item = new JMenuItem("New");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int width, height, depth;
                try{
                width = Integer.parseInt(JOptionPane.showInputDialog(null,
                        "Width?", "3"));
                
                height = Integer.parseInt(JOptionPane.showInputDialog(null,
                        "Height?", "3"));
                
                depth = Integer.parseInt(JOptionPane.showInputDialog(null,
                        "Depth?", "3"));
                }catch(NumberFormatException nfe){
                    return;
                }
                
                canvas.setLevel(new RiemannCube(new Int3(width, height, depth)));
                contentPane.add(canvas, BorderLayout.CENTER);
                setContentPane(contentPane);
            }
        });

        file.add(item);
        // "Save" option in the menu bar, to save to XML
        item = new JMenuItem("Save");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pipe = new LevelPipeline();
                String fname = JOptionPane.showInputDialog(null, "What name?",
                        levelName);

                if(fname == null){
                    JOptionPane.showMessageDialog(null, "Enter a valid name for the level (no extension).");
                    return;
                }
                
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
        file.add(item);

        item = new JMenuItem("Load");
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
        file.add(item);

        // Creates the buttons that lets the user open a help screen.
        JMenu help = new JMenu("Help");
        item = new JMenuItem("Help Screen");
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new HelpFrame();
            }
        });

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
        JPanel sideBar = new JPanel(new GridLayout(20, 1));
        JButton button = new JButton("View Horizontal");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.flip("horizontal");
                requestFocusInWindow();
            }
        });
        sideBar.add(button);
        button = new JButton("View Vertical");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.flip("vertical");
                requestFocusInWindow();
            }
        });
        sideBar.add(button);
        button = new JButton("View Orthogonal");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.flip("orthogonal");
                requestFocusInWindow();
            }
        });
        sideBar.add(button);
        button = new JButton("Go up");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.changeFloor(1);
                requestFocusInWindow();
            }
        });
        sideBar.add(button);
        button = new JButton("Go down");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                canvas.changeFloor(-1);
                requestFocusInWindow();
            }
        });
        sideBar.add(button);
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
