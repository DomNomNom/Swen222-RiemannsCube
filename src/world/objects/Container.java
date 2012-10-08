package world.objects;

import java.awt.Color;

import world.RiemannCube;
import world.cubes.Cube;
import world.objects.items.GameItem;

/**
 * Containers are linked by colors, ie every blue container has the same item inside.
 * There can only be one item in each set of containers.
 * @author sandilalex
 *
 */
public class Container extends GameObject{

    private RiemannCube rCube;
    
    private Color color;
    public Color color(){ return color;}
    
    public Container(Cube cube, Color col, RiemannCube rCube){
        super(cube);
        this.rCube = rCube;
    }
    
    @Override
    public boolean canUse(GameItem i){
        if(rCube.containers.get(color).getItem() == null){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public GameItem use(GameItem i){
        rCube.containers.get(color).setItem(i);
        return null;
    }
    
    @Override
    public String getClassName() {
        return "Container";
    }
    
}
