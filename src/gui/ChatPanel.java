package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import world.events.ChatMessage;

import com.jogamp.opengl.util.Animator;

/**
 * This is the chat panel that the player can use to communicate with the other
 * players
 * 
 * @author David Saxon 300199370, sandilalex
 */
public class ChatPanel extends GLJPanel implements GLEventListener {

    // FIELDS
    private static final long serialVersionUID = 1L;

    private GameFrame frame; // the JFrame containing this panel
    private int width; // the current width of the window
    private int height; // the current height of the window
    private double panelScale = 0.22; // the scale of the panel to the whole
                                      // level

    private JTextArea chatArea; // Area which the text gets printed to
    private JTextField inputField; // Field where the user can enter text
    private Minimap minimapPanel; //the panel where the minimap is displayed

    private BufferedImage background;
    private ChatMessage message;

    //TODO If there is time allow for different colors for different players
    private Map<Integer, Color> playerColors = new HashMap<Integer, Color>();
    
    public ChatMessage getChat() {
        return message;
    }

    public void addMessage(ChatMessage message) {
        int numLines = (height-300)/10;
        
        if(chatArea.getLineCount() > numLines){
            chatArea.setText("");
        } 
        
        String name = frame.getClient().getWorld().players.get(message.speakerID).name();
        if(message.message.equalsIgnoreCase("/clear")){
        	chatArea.setText("");
        	return;
        }
        
        chatArea.append("[" + name + "]: " + message.message + "\n");
    }

    public static Animator animator; // the animator makes sure the chat is
                                     // always being updated

    // CONSTRUCTOR
    /**
     * Constructs a new chat panel
     * 
     * @param width
     *            the width of the overall JFrame
     * @param height
     *            the height of the overall JFrame
     */
    public ChatPanel(GameFrame frame) {
        addGLEventListener(this);
        this.frame = frame;
        height = frame.getSize().height;
        setPreferredSize(new java.awt.Dimension(250, height));
        setLayout(new BorderLayout());

        this.setBackground(Color.gray.darker());

        createCenterPanel();
        
        //add the minimap panel
        minimapPanel = new Minimap(frame, 250, 250);
        minimapPanel.setPreferredSize(new Dimension(250, 250));
        add(minimapPanel, BorderLayout.SOUTH);
        minimapPanel.addGLEventListener(this);
    }

    //METHODS
    /**
     * Creates the Panel that goes on the chat panel, has a text area, text
     * field and a label.
     */
    private void createCenterPanel() {
        try{
            background = ImageIO.read(new File("resources/gfx/chatPanel.png"));
        }catch(IOException e){
            throw new Error("Image file was not found.");
        }
        
        JPanel center = new JPanel(new BorderLayout()){
            public void paintComponent(Graphics g){
                //TODO Check dimensions are correct
                g.drawImage(background, 0, 0, 250, height-250, null);
            }
        };

        // Create and add the label
        JLabel chatLabel = new JLabel("Chat");
        chatLabel.setFont(new Font("xirod", Font.BOLD, 20));
        chatLabel.setForeground(Color.WHITE);
        chatLabel.setHorizontalAlignment(JLabel.CENTER);

        center.add(chatLabel, BorderLayout.NORTH);

        // Create and add the Text area.
        chatArea = new JTextArea();
        chatArea.setBackground(new Color(0, 0, 0, 0));
        chatArea.setForeground(Color.GREEN.brighter()); //TODO Change if there is time. This by default will set text always green
        chatArea.setLineWrap(true);
        chatArea.setFont(new Font("xirod", Font.PLAIN, 9));
        chatArea.setEditable(false);

        // Initialise Colors in map
        playerColors.put(0, Color.RED.brighter());
        playerColors.put(1, new Color(148, 0, 211));
        playerColors.put(2, Color.GREEN.brighter());
        playerColors.put(3, Color.CYAN.brighter());

        center.add(chatArea, BorderLayout.CENTER);

        // Create, set up the action listener and add the Text field
        inputField = new JTextField();
        inputField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                message = new ChatMessage(inputField.getText(), frame.getClient().player().id());
                frame.getClient().push(message);

                inputField.setText("");
                
                frame.getViewPort().requestFocus();
            }
        });

        center.add(inputField, BorderLayout.SOUTH);

        // Add the panel to the main GLPanel
        add(center, BorderLayout.CENTER);
    }

    /**
     * Gets the TextField so that the viewport can request focus on it.
     * 
     * @return
     */
    public JTextField getInputField() {
        return inputField;
    }
    
    /**Gets the panel that the minimap is stored in
     * @return*/
    public GLJPanel getMinimapPanel() {
    	return minimapPanel;
    }
    
    /** Initialises the chat panel */
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.90f, 0.90f, 0.90f, 1.0f); // set the clear colour

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear the screen for the first time
        minimapPanel.init(drawable); //initialise the minimap panel
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if (frame.getSize().height != height) { //if the width has changed then scale the chat panel
            setPreferredSize(new java.awt.Dimension(250, frame.getSize().height));
        }
        
        minimapPanel.display(drawable); //display the minimap panel
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        // DO NOTHING
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x1, int y1, int x2, int y2) {
        // DO NOTHING
    }

}
