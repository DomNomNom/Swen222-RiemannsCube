package world;
import world.items.*;

public class Player {

	private GameItem item;
	private LightSource torch;
	private int num;
	
	public Player(int num){
		this.num = num;
	}
	
	public GameItem item() {
		return item;
	}
	public void setItem(GameItem item) {
		this.item = item;
	}
	public LightSource torch() {
		return torch;
	}
	public void setTorch(LightSource torch) {
		this.torch = torch;
	}

	public int num() {
		return num;
	}
}
