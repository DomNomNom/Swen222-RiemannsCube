package world.objects.items;

import world.cubes.Cube;

public class Token extends GameItem {

    public Token(Cube pos) {
        super(pos);
    }

    @Override
    public String getClassName() {
        return "token";
    }

}
