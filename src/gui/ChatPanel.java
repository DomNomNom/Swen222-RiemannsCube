package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
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
 * @author David Saxon 300199370
 */
public class ChatPanel extends GLJPanel implements GLEventListener {

    // FIELDS
    private static final long serialVersionUID = 1L;

    private GameFrame frame; // the JFrame containing this panel
    private int width; // the current width of the window
    private double panelScale = 0.22; // the scale of the panel to the whole
                                      // level

    private JTextArea chatArea; // Area which the text gets printed to
    private JTextField inputField; // Field where the user can enter text

    private ChatMessage message;

    public ChatMessage getChat() {
        return message;
    }

    public void addMessage(ChatMessage message) {
        chatArea.append(message.message);
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
        width = frame.getSize().width;
        setPreferredSize(new java.awt.Dimension((int) (width * panelScale),
                frame.getSize().height));
        setLayout(new BorderLayout());

        this.setBackground(Color.gray.darker());

        createCenterPanel();
    }

    /**
     * Creates the Panel that goes on the chat panel, has a text area, text
     * field and a label.
     */
    private void createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.GRAY.darker());

        // Create and add the label
        JLabel chatLabel = new JLabel("Chat");
        chatLabel.setFont(new Font("Serif", Font.BOLD, 25));
        chatLabel.setForeground(Color.WHITE);
        chatLabel.setHorizontalAlignment(JLabel.CENTER);

        center.add(chatLabel, BorderLayout.NORTH);

        // Create and add the Text area.
        chatArea = new JTextArea();
        chatArea.setBackground(Color.GRAY.darker());
        chatArea.setForeground(Color.green.brighter());
        chatArea.setEditable(false);

        center.add(chatArea, BorderLayout.CENTER);

        // Create, set up the action listener and add the Text field
        inputField = new JTextField();
        inputField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                message = new ChatMessage(inputField.getText(), frame.getClient().getPlayerID());
                frame.getClient().push(message);

                inputField.setText("");
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

    // METHODS
    /** Initialises the chat panel */
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.90f, 0.90f, 0.90f, 1.0f); // set the transparency
                                                    // colour
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // clear
                                                                     // the
                                                                     // screen
                                                                     // for the
                                                                     // first
                                                                     // time
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if (frame.getSize().width != width) { // if the width has changed then
                                              // scale the chat panel
            width = frame.getSize().width;
            setPreferredSize(new java.awt.Dimension((int) (width * panelScale),
                    frame.getSize().height));
        }
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
