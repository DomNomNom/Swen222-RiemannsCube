package world.events;

import utils.Float3;

/**
 * Just a little update on a players relative offset
 *
 * @author schmiddomi
 */
public class PlayerRelPos extends Action {
    private static final long serialVersionUID = 8669401209595701641L;
    
    public final int playerID;
    public final Float3 relpos;
    
    public PlayerRelPos(int playerID, Float3 relpos) {
        this.playerID = playerID;
        this.relpos = relpos;
    }
}
