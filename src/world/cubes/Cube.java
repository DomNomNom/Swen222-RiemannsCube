package world.cubes;


import world.objects.Player;
import world.objects.GameObject;

public abstract class Cube {

    private GameObject object;
    private Player player;
    
    abstract public int type();

    
    public void setObject(GameObject o){
        object = o;
    }
    
    public GameObject object(){
        return object;
    }

    public Player player() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}