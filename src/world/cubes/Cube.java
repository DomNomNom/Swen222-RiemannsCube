package world.cubes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import world.objects.Player;
import world.objects.GameObject;

public abstract class Cube {

    public final Collection<GameObject> objects = new HashSet<GameObject>();

    abstract public int type();
    
    public GameObject object() {
        for (GameObject go : objects)
            if (!(go instanceof Player))
                return go;

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

}