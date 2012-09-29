package MAIN;

import world.RiemannCube;
import world.cubes.Glass;
import world.cubes.Wall;
import gui.GameFrame;

/**A testing class that runs the GUI
 * 
 * @author David Saxon 300199370
 */
public class GUITest {
    
    public static void main(String[] args) {
    	//create a level for testing the game
    
		//settings
		GameFrame.high = true;
		GameFrame.free = true;
		GameFrame.noFloor = false;
		GameFrame.showFps = false;
    	GameFrame window = new GameFrame(null);
        window.init();
        window.execute();
    }
}