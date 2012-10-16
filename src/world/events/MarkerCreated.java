package world.events;

import utils.Int3;

public class MarkerCreated extends Action {

    private static final long serialVersionUID = 9170487971399333409L;

    public final int playerID;
    public final Int3 pos;
    
    public MarkerCreated(int playerID, Int3 pos) {
        this.playerID = playerID;
        this.pos = pos;
    }

}
