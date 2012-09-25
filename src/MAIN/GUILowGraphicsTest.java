package MAIN;

import world.RiemannCube;
import world.cubes.Glass;
import world.cubes.Wall;
import gui.GameFrame;

/**A testing class that runs the GUI in the low grahics mode
 * 
 * @author David Saxon 300199370
 */
public class GUILowGraphicsTest {
    
    public static void main(String[] args) {
    	//create a level for testing the game
    	RiemannCube testLevel = new RiemannCube(6, 6, 6);
    	for (int x = 0; x < 6; ++x) {
    		for (int y = 0; y < 6; ++y) {
    			for (int z = 0; z < 6; ++z) {
    				if (x == 0) testLevel.setCube(x, y, z, new Wall());
    				else if (x == 5) testLevel.setCube(x, y, z, new Wall());
    				else if (y == 0) testLevel.setCube(x, y, z, new Wall());
    				else if (y == 5) testLevel.setCube(x, y, z, new Wall());
    				else if (z == 0) testLevel.setCube(x, y, z, new Wall());
    				else if (z == 5) testLevel.setCube(x, y, z, new Wall());
    			}
    		}
    	}
    	
    	//add some glass
    	testLevel.setCube(3, 1, 0, new Glass());
    	
    	//add some empty spaces
    	testLevel.setCube(2, 1, 0, null);
    
    	GameFrame window = new GameFrame(false, false, testLevel);
        window.init();
        window.execute();
    }
}