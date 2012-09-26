package world;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import utils.Int2;
import utils.Int3;
import world.cubes.*;
import world.events.Action;
import world.events.PlayerMove;
import world.objects.GameObject;
import world.objects.Player;
import world.objects.Trigger;


/**
 * The RiemannCube holds all world state
 * 
 * @author schmiddomi
 *
 */
public class RiemannCube {

    /**
     * this is the cube-accessing interface. no encapsulation required as these are final.
     * 
     * how to use:
     * cubes[x][y][z]
     * 
     * X        Y       Z 
     * RIGHT    DOWN    TOWARDS 
     * Therefore (0,0,0) is
     * top-left-deep
     */
    public final Cube[][][] cubes;
    public Cube getCube(int x, int y, int z) {    return cubes[x][y][z];  }
    public void setCube(int x, int y, int z, Cube c) {   cubes[x][y][z] = c;  }

    public final Map<Integer, Player > players  = new HashMap<Integer, Player >();
    public final Map<Integer, Trigger> triggers = new HashMap<Integer, Trigger>();
    
    public final int width, height, depth;

    
    
    /**
     * Creates the RiemannCube with the given dimensions.
     * It will be filled with FloorTiles
     * 
     * @param width
     * @param height
     * @param depth
     */
    public RiemannCube(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        cubes = new Cube[width][height][depth];
        fillCubes();
    }

    private void fillCubes() {
        for (int x=0; x<width; ++x)
            for (int y=0; y<height; ++y)
                for (int z=0; z<depth; ++z)
                    cubes[x][y][z] = new Floor();
    }

    
    public boolean isValidPlayer(int playerID) {
        return players.containsKey(playerID);
    }
    
    // ====== Action ======
    
    /**
     * Tries to apply the given action.
     * This uses isValidAction() for action validation.
     * 
     * @param a The action that should be applied
     * @return Whether the action is valid and has been applied.
     */
    public boolean applyAction(Action a) {
        if (a instanceof PlayerMove)
            return movePlayer((PlayerMove) a);
        else return false;
    }
    
    private boolean movePlayer(PlayerMove action) {
        if (!isValidPlayer(action.playerID)) return false;
        
        Int3 to = players.get(action.playerID).getPos().copy().add(action.movement);
        // TODO check bounds, etc
        return true; // TODO
    }
    
    

    // ====== equals (used for testing) ======
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)        return true;
        if (obj == null)        return false;
        if (!(obj instanceof RiemannCube))        return false;
        
        RiemannCube other = (RiemannCube) obj;
        
        if (depth != other.depth || height != other.height || width != other.width)    return false;
        
        for (int x=width; x --> 0;)
            for (int y=0; y<height; ++y)
                if (!Arrays.equals(cubes[x][y], other.cubes[x][y]))
                    return false;
 
        return true;
    }
    
    
    // ====== Slicing (used in editor) ======
    


    public Cube[][] verticalSlice(int x) {
        return cubes[x];
    }

    public Cube[][] horizontalSlice(int y) {
        Cube[][] slice = new Cube[width][depth];
        for (int i = 0; i < width; i++) {
            slice[i] = cubes[i][y];
        }
        return slice;
    }

    public Cube[][] orthogonalSlice(int z) {
        Cube[][] slice = new Cube[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                slice[i][j] = cubes[i][j][z];
        return slice;
    }


    
}
