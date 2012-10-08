package world.objects;

import java.awt.Color;

import world.RiemannCube;
import world.cubes.Cube;
import world.objects.items.GameItem;

/**
 * Containers are linked by colours, ie every blue container has the same item inside.
 * There can only be one item in each set of containers.
 *
 * @author sandilalex, schmiddomi
 */
public class Container extends GameObject{

    //private RiemannCube rCube;
    private final GlobalHolder holder;
    
    private final Color colour;
    public Color color(){ return colour;}
    
    /** This also initialises the colour group if it doesn't exist yet */
    public Container(Cube cube, Color col, RiemannCube rCube){
        super(cube);
        //this.rCube = rCube;
        this.colour = col;
        if (!rCube.containers.containsKey(col))
            rCube.containers.put(col, new GlobalHolder());

        this.holder = rCube.containers.get(col);
    }
    
    @Override
    public boolean canUse(GameItem i) {
        if (i == null) return holder.getItem() != null;
        else           return holder.getItem() == null;
    }

    @Override
    public GameItem use(GameItem i){
        if (i != null) holder.setItem(i); // put in
        else return    holder.popItem();  // get out
        return null;
    }
    
    @Override
    public String getClassName() {
        return "Container";
    }
    
}
