package world.objects;

import java.awt.Color;
import java.util.Map;

import world.RiemannCube;
import world.cubes.Cube;
import world.objects.items.GameItem;

/**
 * Containers are linked by colors, ie every blue container has the same item inside.
 * There can only be one item in each set of containers.
 */
public class Container extends GameObject{

    private final Map<Color, GlobalHolder> containers;
    
    private int rotation = 0;
    private float loop1 = 0.00f;
    private float loop2 = 0.05f;
    private float loop3 = 0.1f;
    
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
    public boolean canUseStart(GameItem i){
        if(i == null)
            return containers.get(color).getItem() != null;
         else 
            return containers.get(color).getItem() == null;
        
    }

    @Override
    public GameItem useStart(GameItem i){
        if(i == null){
            return containers.get(color).popItem();
        } else {
            containers.get(color).setItem(i);
            return null;
        }
    }
    
    public void animate() {
    	++rotation;
    	loop1 += 0.005f;
    	if (loop1 >= 0.15f) loop1 = 0.01f;
    	loop2 += 0.005f;
    	if (loop2 >= 0.15f) loop2 = 0.01f;
    	loop3 += 0.005f;
    	if (loop3 >= 0.15f) loop3 = 0.01f;
    	if (rotation == 360) rotation = 0;
    }
    
    public float getLoop1() {
    	return loop1;
    }
    
    public float getLoop2() {
    	return loop2;
    }
    
    public float getLoop3() {
    	return loop3;
    }
    
    public int getRotate() {
    	return rotation;
    }
    
    @Override
    public String getClassName() {
        return "Container";
    }
}
