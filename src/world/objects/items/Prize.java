package world.objects.items;

import world.cubes.Cube;

public class Prize extends GameItem {

    public Prize(Cube pos) {
        super(pos);
    }

    @Override
    public String getClassName() {
        return "prize";
    }

}
