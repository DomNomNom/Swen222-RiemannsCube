package world.cubes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import world.objects.Player;
import world.objects.GameObject;

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
        for (GameObject go : objects) {
            if (go instanceof Player) {
                return (Player) go;
            }
        }

        return null;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == null || ! (o instanceof Cube)) return false;
        
        Cube other = (Cube) o;
        if(getClass() != o.getClass()) return false; // since this is for subclasses
        for (GameObject g :       objects) if (!other.objects.contains(g)) return false;
        for (GameObject g : other.objects) if (!      objects.contains(g)) return false;
        return true;
    }
}