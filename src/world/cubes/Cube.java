package world.cubes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import utils.Int3;
import world.objects.Player;
import world.objects.GameObject;
import world.objects.items.GameItem;

/**
 * The base class for all "tile" cubes 
 *
 * @author schmiddomi
 */

public abstract class Cube {
    
    Cube(Int3 pos) {
        if (pos==null) throw new IllegalArgumentException("position may not be null");
        this.pos = pos;
    }
    
    protected Int3 pos;
    public Int3 pos() { return pos; }

    protected final Collection<GameObject> objects = new HashSet<GameObject>();

    private boolean spawnPoint;
    public boolean isSpawnPoint(){ return spawnPoint; }
    public void setSpawnPoint(boolean b){ spawnPoint = b;}
    
    
    public abstract CubeType type();
    
    /** @return A object that on this tile but not a player.    */
    public GameObject object() {
        for (GameObject gObj : objects)
            if (!(gObj instanceof Player))
                return gObj;

        return null;
    }

    /**
     * Checks whether a Object can be added to this cube
     * returns true by default
     * @param o The object to add
     * @return Whether a Object can be added to this cube
     */
    public boolean canAddObject(GameObject o) {
        return true;
    }
    
    /**
     * Adds a GameObject to this cube or returns false. assumes canAddObject()
     * @param o The object to add
     * @return Whether it was possible to add this object
     */
    public boolean addObject(GameObject o) {
        return objects.add(o);
    }
    
    /**
     * Removes a GameObject to this cube or returns false
     * @param o The object to remove
     * @return Whether it was possible to remove this object
     */
    public boolean removeObject(GameObject o) {
        return objects.remove(o);
    }

    /** returns whether there is a item in this cube that can be picked up */
    public boolean hasItem() {
        for (GameObject o : objects)
            if (o instanceof GameItem)
                return true;
        return false;
    }
        
    /**
     * Removes a GameItem to this cube or returns null
     * @return Whether it was possible to remove the item
     */
    public GameItem popItem() {
        GameItem i = null;
        for (GameObject o : objects)
            if (o instanceof GameItem)
                return i = (GameItem) o;
        objects.remove(i);
        return i; 
    }
    
    public boolean canUseItem(GameItem i) {
        for (GameObject o : objects)
            if (o.canUse(i))
                return true;
        return false;
    }
    
    /** returns a GameItem that is produced by this action (eg. ) */
    public GameItem useItem(GameItem i) {
        for (GameObject o : objects)
            if (o.canUse(i))
                return o.use(i);
        return null;
    }
    
    
    /** @return A player on this cube. null if there is none   */
    public Player player() {
        for (GameObject go : objects)
            if (go instanceof Player)
                return (Player) go;

        return null;
    }

    
    /** Helper for equals(). Checks that we have a element in b for all elements in a.*/
    private static boolean containsEquivalentContent(Cube a, Cube b) {
    	// equivalent contents?
        for (GameObject g : a.objects) {
            boolean foundEquals = false;
            for (GameObject g2 : b.objects) 
                if (g.equals(g2)) foundEquals = true;
            if (!foundEquals) return false;
        }
        return true;
    }
    @Override
    /**
     * Checks that the cubes other cube is of the same subtype of Cube.
     * Checks that they have equivalent contents. 
     */
    public final boolean equals(Object o) {
        if (o == null || ! (o instanceof Cube)) return false;
        
        Cube other = (Cube) o;
        if(getClass() != o.getClass()) return false; // same subclass?
        
        // equivalent contents
        if (!containsEquivalentContent(this, other)) return false;
        if (!containsEquivalentContent(other, this)) return false;
        
        return true;
    }
    
    /** 
     * Checks against cube constraints and all contained objects whether anything blocks the movement
     * By default returns false iff all objects do not block the player 
     */
    public boolean blocks(Player p) {
        for (GameObject o : objects)
            if (o.blocks(p))
                return true;
        return false;
    }
}