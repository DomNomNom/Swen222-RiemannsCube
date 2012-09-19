package MAIN;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import world.RiemannCube;

import editor.EditorCanvas;
import data.LevelPipeline;

/**
 * 
 * @author mudgejayd 300221669
 * Creates a new frame, for running the Level Editor in.
 *
 */
public class EditorFrame extends JFrame {
//<<<<<<< HEAD
//    
//    JPanel contentPane = new JPanel(new BorderLayout());
//    EditorCanvas canvas = new EditorCanvas();
//    
//    public EditorFrame(){
//        super("Level Editor");
//        setLayout(new BorderLayout());
//        add(canvas, BorderLayout.CENTER);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.addKeyListener(canvas);
//        pack();
//        setVisible(true);
//        setFocusable(true);
//        
//        addMenuBar();
//        addSideBar();
//
//        setContentPane(contentPane);
//    }
//    
//    /**
//     * Creates a menu bar on the North, for menu commands.
//     */
//    private void addMenuBar(){
//        JMenuBar menuBar = new JMenuBar();
//        JMenu menu = new JMenu("File");
//        //"New" option in the menu bar, to create empty map.
//        JMenuItem item = new JMenuItem("New");
//        item.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                int width = Integer.parseInt(JOptionPane.showInputDialog(null, "Width?", "3"));
//                int height = Integer.parseInt(JOptionPane.showInputDialog(null, "Height?", "3"));
//                int depth = Integer.parseInt(JOptionPane.showInputDialog(null, "Depth?", "3"));
//                canvas.setLevel(new RiemannCube(width, height, depth));
//                contentPane.add(canvas, BorderLayout.CENTER);
//                setContentPane(contentPane);
//            }});
//        menu.add(item);
//        //"Save" option in the menu bar, to save to XML
//        item = new JMenuItem("Save");
//            item.addActionListener(new ActionListener(){
//                public void actionPerformed(ActionEvent e) {
//                    LevelPipeline pipe = new LevelPipeline();
//                    String fname = JOptionPane.showInputDialog(null, "What name?", "New level");
//                    pipe.save(canvas.level(), fname);
//                }
//            });
//            menu.add(item);
//        menuBar.add(menu);
//        contentPane.add(menuBar, BorderLayout.NORTH);
//    }
//    
//    /**
//     * Creates a bar on the West side, complete with basic buttons
//     * for performing important actions.
//     */
//    private void addSideBar(){
//        JPanel sideBar = new JPanel(new GridLayout(20,1));
//        JButton button = new JButton("View Horizontal");
//        button.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                canvas.flip("horizontal");requestFocusInWindow();}
//        });
//        sideBar.add(button);
//        button = new JButton("View Vertical");
//        button.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                canvas.flip("vertical");requestFocusInWindow();}
//        });
//        sideBar.add(button);
//        button = new JButton("View Orthogonal");
//        button.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                canvas.flip("orthogonal");requestFocusInWindow();}
//        });
//        sideBar.add(button);
//        button = new JButton("Go up");
//        button.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                canvas.changeFloor(1);requestFocusInWindow();}
//        });
//        sideBar.add(button);
//        button = new JButton("Go down");
//        button.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                canvas.changeFloor(-1);requestFocusInWindow();}
//        });
//        sideBar.add(button);
//        button = new JButton("View Isometric");
//        button.addActionListener(new ActionListener(){
//            public void actionPerformed(ActionEvent e) {
//                canvas.changeIsometric();requestFocusInWindow();}
//        });
//        sideBar.add(button);
//        sideBar.addKeyListener(canvas);
//        contentPane.add(sideBar, BorderLayout.WEST);
//    }
//    
//    public static void main(String[] args){
//        JFrame edFrame = new EditorFrame();
//    }
//=======

	JPanel contentPane = new JPanel(new BorderLayout());
	EditorCanvas canvas = new EditorCanvas();
	LevelPipeline pipe;

	public EditorFrame() {
		super("Level Editor");
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(canvas);
		pack();
		setVisible(true);
		setFocusable(true);

		pipe  = new LevelPipeline();
		
		addMenuBar();
		addSideBar();

		setContentPane(contentPane);
	}
	
	/**
	 * Creates a menu bar on the North, for menu commands.
	 */
	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		//"New" option in the menu bar, to create empty map.
		JMenuItem item = new JMenuItem("New");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int width = Integer.parseInt(JOptionPane.showInputDialog(null,
						"Width?", "3"));
				int height = Integer.parseInt(JOptionPane.showInputDialog(null,
						"Height?", "3"));
				int depth = Integer.parseInt(JOptionPane.showInputDialog(null,
						"Depth?", "3"));
				canvas.setLevel(new RiemannCube(width, height, depth));
				contentPane.add(canvas, BorderLayout.CENTER);
				setContentPane(contentPane);
			}
		});
		
		menu.add(item);
		//"Save" option in the menu bar, to save to XML
		item = new JMenuItem("Save");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pipe = new LevelPipeline();
				String fname = JOptionPane.showInputDialog(null, "What name?",
						"New level");
				pipe.save(canvas.level(), fname);
			}
		});
		menu.add(item);
		
		item = new JMenuItem("Load");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pipe.load();
			}
		});
		
		menu.add(item);
		menuBar.add(menu);
		contentPane.add(menuBar, BorderLayout.NORTH);
	}
	
	/**
	 * Creates a bar on the West side, complete with basic buttons
	 * for performing important actions.
	 */
	private void addSideBar(){
		JPanel sideBar = new JPanel(new GridLayout(20,1));
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
		JFrame edFrame = new EditorFrame();
	}
//>>>>>>> f6ff72f749313e62046c4d7edb16c4519649d04b
}
