package world.objects.traps;

import utils.Float3;
import world.objects.Player;

public class Reorientate implements Trap{

    public void activate(Player p) {
        Float3 rot = p.rotation;
        p.rotation = new Float3(-rot.x, -rot.y, -rot.z);
    }

}
