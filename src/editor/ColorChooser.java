package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorChooser extends JFrame{

	private List<Color> cols = new ArrayList<Color>();
	private static Color choice;
	
	public ColorChooser(List<Color> cols){
		super("Choose a Color");
		
		if (cols != null) {
			this.cols = cols;
		}
		
		this.setLayout(new BorderLayout());
		this.setSize(new Dimension(500, 500));
		
		// For Centering frame in the middle of the screen.
        // ----------------------------------------------------------
        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Determine the new location of the window
        int wid = this.getSize().width;
        int height = this.getSize().height;

        int x = (dim.width / 2 - wid / 2);
        int y = (dim.height / 2 - height / 2);

        // Move the window
        setLocation(x, y);
        // ----------------------------------------------------------
		
		createTop();
		createMiddle();
		createSouth();
		
		setVisible(true);
	}
	
	public static Color chooseColor(List<Color> cols){
		new ColorChooser(cols);
		
		return choice;
	}
	
	private void createTop(){
		JPanel top = new JPanel();
		top.setSize(new Dimension(500, 60));
		
		// Create and add the label
        JLabel colsLabel = new JLabel("Recent Colors:");
        colsLabel.setFont(new Font("Serif", Font.BOLD, 40));
        colsLabel.setHorizontalAlignment(JLabel.CENTER);
        
        top.add(colsLabel);
        add(top, BorderLayout.NORTH);
	}
	
	private void createMiddle(){
		JPanel middle = new JPanel(new GridLayout(5, 5));
		middle.setSize(500, 400);
		
		for(Color col : cols){
			if(col == null)
				continue;
			
			JButton button = new JButton();
			button.setPreferredSize(new Dimension(100, 30));
			button.setBackground(col);
			button.addActionListener(new buttonListener(col));
			middle.add(button);
		}
		
		add(middle, BorderLayout.CENTER);
	}
	
	private void createSouth(){
		JPanel south = new JPanel(new BorderLayout());
		south.setSize(new Dimension(500, 40));
		
		JButton button = new JButton("Choose Specific Color");
		button.setPreferredSize(new Dimension(200, 35));
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choice = JColorChooser.showDialog(null, "Choose Color", Color.WHITE);
				dispose();
			}
		});
		
		south.add(button, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);
	}
	
	public class buttonListener implements ActionListener{

		private Color col;
		
		public buttonListener(Color col){
			this.col = col;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			choice = col;
			dispose();
		}
		
	}
}
