package world.items;

import world.cubes.Cube;
import world.objects.GameObject;

/**
 * A GameItem is a GameObject that the player can carry around
 * (IMO, this should just be a gameObject with a "carriable" property ~Dom) 
 *
 * @author schmiddomi
 */
public abstract class GameItem extends GameObject {

    public GameItem(Cube pos) {
        super(pos);
    }

}
