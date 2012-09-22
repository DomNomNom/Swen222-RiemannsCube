package MAIN;

import gui.GameFrame;

/**A testing class that runs the GUI in the low grahics mode
 * 
 * @author David Saxon 300199370
 */
public class GUILowGraphicsTest {
    
    public static void main(String[] args) {
    	GameFrame window = new GameFrame(false);
        window.init();
        window.execute();
    }
}