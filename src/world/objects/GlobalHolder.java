package world.objects;

import java.awt.Color;

import world.objects.items.GameItem;

/**
 * A class used by containers to store a global object. For example,
 * every blue container points to one GlobalHolder, and can all access the
 * item it holds.
 * Can only hold one item at a time.
 * @author sandilalex
 *
 */
public class GlobalHolder{

    private GameItem item;

    /**
     * Returns the item inside the containers, but doesn't remove it!
     * @return GameItem
     */
    public GameItem getItem() {
        return item;
    }

    /**
     * Removes and returns the item in these containers.
     * @return GameItem
     */
    public GameItem popItem() {
        GameItem temp = item;
        item = null;
        return temp;
    }

    /**
     * When a players puts an item into the container this will set the item.
     * @param item
     */
    public void setItem(GameItem item) {
        if (item == null) {
            this.item = item;
        } else {
            throw new Error("Should be checked by container");
        }
    }
}
