package world.events;

/**
 * This is a base class for any action a player can do with a Item 
 *
 * @author schmiddomi
 */
public abstract class ItemAction extends Action {

    public final int playerID;
    
    public ItemAction(int playerID) { this.playerID = playerID; }
    
}
