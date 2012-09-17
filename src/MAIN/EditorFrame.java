package MAIN;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import world.RiemannCube;

import editor.EditorCanvas;
import data.LevelPipeline;

public class EditorFrame extends JFrame {
	
	JPanel contentPane = new JPanel(new BorderLayout());
	EditorCanvas canvas = new EditorCanvas();
	
	public EditorFrame(){
		super("Level Editor");
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addKeyListener(canvas);
		pack();
		setVisible(true);
		setFocusable(true);
		
		addMenuBar();
		addSideBar();

		setContentPane(contentPane);
	}
	
	private void addMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("New");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int width = Integer.parseInt(JOptionPane.showInputDialog(null, "Width?", "3"));
				int height = Integer.parseInt(JOptionPane.showInputDialog(null, "Height?", "3"));
				int depth = Integer.parseInt(JOptionPane.showInputDialog(null, "Depth?", "3"));
				canvas.setLevel(new RiemannCube(width, height, depth));
				contentPane.add(canvas, BorderLayout.CENTER);
				setContentPane(contentPane);
			}});
		menu.add(item);
		item = new JMenuItem("Save");
			item.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					LevelPipeline pipe = new LevelPipeline();
					String fname = JOptionPane.showInputDialog(null, "What name?", "New level");
					pipe.save(canvas.level(), fname);
				}
			});
			menu.add(item);
		menuBar.add(menu);
		contentPane.add(menuBar, BorderLayout.NORTH);
	}
	
	private void addSideBar(){
		JPanel sideBar = new JPanel(new GridLayout(20,1));
		JButton button = new JButton("View Horizontal");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				canvas.flip("horizontal");requestFocusInWindow();}
		});
		sideBar.add(button);
		button = new JButton("View Vertical");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				canvas.flip("vertical");requestFocusInWindow();}
		});
		sideBar.add(button);
		button = new JButton("View Orthogonal");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				canvas.flip("orthogonal");requestFocusInWindow();}
		});
		sideBar.add(button);
		button = new JButton("Go up");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				canvas.changeFloor(1);requestFocusInWindow();}
		});
		sideBar.add(button);
		button = new JButton("Go down");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				canvas.changeFloor(-1);requestFocusInWindow();}
		});
		sideBar.add(button);
		button = new JButton("View Isometric");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				canvas.changeIsometric();requestFocusInWindow();}
		});
		sideBar.add(button);
		sideBar.addKeyListener(canvas);
		contentPane.add(sideBar, BorderLayout.WEST);
	}
	
	public static void main(String[] args){
		JFrame edFrame = new EditorFrame();
	}
}