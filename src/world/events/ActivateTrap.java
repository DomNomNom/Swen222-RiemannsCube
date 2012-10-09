package world.events;

import world.objects.traps.Trap;

public class ActivateTrap extends Event {

    public final Trap trap;
    public final int playerID;
    
    ActivateTrap(Trap trap, int playerID){
        this.trap = trap;
        this.playerID = playerID;
    }
}
