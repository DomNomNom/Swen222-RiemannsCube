package world.cubes;

import world.Player;

public class Entrance extends Floor{
	
	public Entrance (int playerNum){
		super();
		super.setPlayer(new Player(playerNum));
	}
}
