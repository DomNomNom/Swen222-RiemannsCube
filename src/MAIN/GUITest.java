package MAIN;

import gui.GameFrame;

/**A testing class that runs the GUI
 * 
 * @author David Saxon 300199370
 */
public class GUITest {
    
    public static void main(String args[]) {        
        GameFrame window = new GameFrame();
        window.init();
        window.execute();
    }
}