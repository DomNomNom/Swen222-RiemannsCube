package world.objects;

import java.awt.Color;

import world.objects.items.GameItem;

public class GlobalHolder{

    private Color col;
    
    private GameItem item;
    public GameItem getItem(){ return item;}
    public GameItem popItem() {
        GameItem temp = item;
        item = null;
        return temp;
    }
    public void addItem(GameItem item) {
        if (item == null) {
            this.item = item;
        } else {
            throw new Error("Should be checked by container");
        }
    }
    
    public GlobalHolder(Color col){
        this.item = null;
        this.col = col;
    }
    
}
