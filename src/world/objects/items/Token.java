package world.objects.items;

import world.cubes.Cube;

/** Awards bonus points if the player leaves the level holding this Token.
 * 
 * @author mudgejayd
 *
 */
public class Token extends GameItem {

    public Token(Cube pos) {
        super(pos);
    }

    @Override
    public String getClassName() {
        return "token";
    }

}
