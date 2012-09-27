package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class HelpFrame extends JFrame {

    private JPanel main;
    private BufferedImage help;

    public HelpFrame() {
        setTitle("Level Editor Help Screen");
        setLayout(new BorderLayout());
        setSize(new Dimension(1000, 628));
        setMinimumSize(new Dimension(1000, 628));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
        
        setResizable(false);
        makeMainPanel();
        setVisible(true);
    }

    private void makeMainPanel() {
        try{
            help = ImageIO.read(new File("resources/HelpScreen.png"));
        }catch(IOException e){
            throw new Error("Help file was not found");
        }
        main = new JPanel(){
            public void paint(Graphics g){
                g.drawImage(help, 0, 0, 1000, 628, null);
            }
        };
        main.setBackground(Color.BLACK);
        add(main, BorderLayout.CENTER);
    }
}