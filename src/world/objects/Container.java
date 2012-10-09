package world.objects;

import java.awt.Color;
import java.util.Map;

import world.RiemannCube;
import world.cubes.Cube;
import world.objects.items.GameItem;

/**
 * Containers are linked by colors, ie every blue container has the same item inside.
 * There can only be one item in each set of containers.
 * @author sandilalex, schmiddomi
 *
 */
public class Container extends GameObject{

    private final Map<Color, GlobalHolder> containers;
    
    private Color color;
    public Color color(){ return color;}
    
    public Container(Cube cube, Color col, Map<Color, GlobalHolder> containers){
        super(cube);
        this.containers = containers;
        
        if(containers != null && !containers.containsKey(col))
            containers.put(col, new GlobalHolder());
            
        this.color = col;
    }
    
    @Override
    public boolean canUse(GameItem i){
        if(i == null)
            return containers.get(color).getItem() != null;
         else 
            return containers.get(color).getItem() == null;
        
    }

    @Override
    public GameItem use(GameItem i){
        if(i == null){
            return containers.get(color).popItem();
        } else {
            containers.get(color).setItem(i);
            return null;
        }
    }
    
    @Override
    public String getClassName() {
        return "Container";
    }
    
}
