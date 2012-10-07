package world.objects;

import java.awt.Color;

import world.cubes.Cube;
import world.objects.items.GameItem;

public class Container extends GameObject{

    private Color color;
    public Color color(){ return color;}
    
    public Container(Cube cube, Color col){
        super(cube);
    }
    
    

    @Override
    public String getClassName() {
        return "Container";
    }
    
}
