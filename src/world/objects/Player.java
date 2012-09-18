package world.objects;
import world.items.*;

public class Player extends GameObject {

    private GameItem item;
    public GameItem item() {  return item; }
    public void setItem(GameItem item) { this.item = item; }

    private LightSource torch;
    public LightSource torch() { return torch;    }
    public void setTorch(LightSource torch) { this.torch = torch; }
    
    private int num;
    
    public Player(int num){
        this.num = num;
    }
    
    

    public int num() {
        return num;
    }
}
