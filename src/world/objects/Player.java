package world.objects;
import world.items.*;

public class Player extends GameObject {

    
    
    private GameItem item;
    public GameItem item() {  return item; }
    public void setItem(GameItem item) { this.item = item; }

    private LightSource torch;
    public LightSource torch() { return torch;    }
    public void setTorch(LightSource torch) { this.torch = torch; }
    
    private int id;
    public int id() {
        return id;
    }
    
    public Player(int num){
        this.id = num;
    }
    
    
    
    @Override
    public String getClassName() {
        return "Player";
    }
    
}
