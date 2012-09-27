package world.cubes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import world.objects.Player;
import world.objects.GameObject;

/**
 * The base class for all "tile" cubes 
 *
 * @author schmiddomi
 */

public abstract class Cube {

    public final Collection<GameObject> objects = new HashSet<GameObject>();

    private boolean spawnPoint;
    public boolean isSpawnPoint(){ return spawnPoint;}
    public void setSpawnPoint(boolean b){ spawnPoint = b;}
    
    public abstract CubeType type();
    
    public GameObject object() {
        for (GameObject gObj : objects)
            if (!(gObj instanceof Player))
                return gObj;

        return null;
    }

    public void addObject(GameObject o) {
        objects.add(o);
    }

    
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
}