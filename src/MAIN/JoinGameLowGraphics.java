package MAIN;

import world.RiemannCube;
import world.cubes.Glass;
import world.cubes.Wall;
import gui.GameFrame;

/**A testing class that runs the GUI in the low grahics mode
 * 
 * @author David Saxon 300199370
 */
public class JoinGameLowGraphics {
    
    public static void main(String[] args) {
    	
    	//settings
    	GameFrame.high = false;
		GameFrame.free = true;
		GameFrame.noFloor = true;
    	GameFrame window = new GameFrame(null);
        window.init();
        window.execute();
    }
}