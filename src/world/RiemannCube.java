    package world;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import utils.Float3;
import utils.Int3;
import utils.Int2;
import utils.Int3;
import world.cubes.*;
import world.events.Action;
import world.events.ItemAction;
import world.events.ItemDrop;
import world.events.ItemPickup;
import world.events.ItemUse;
import world.events.PlayerMove;
import world.events.PlayerSpawning;
import world.objects.GameObject;
import world.objects.GlobalHolder;
import world.objects.Player;
import world.objects.Trigger;


/** 
 * The RiemannCube holds all world state.
 * It should not change unless a Action is applied.
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
    public Cube getCube(Int3 v) { return cubes[v.x][v.y][v.z]; }
    public Cube getCube(int x, int y, int z) {    return cubes[x][y][z];  }
    public void setCube(int x, int y, int z, Cube c) {   cubes[x][y][z] = c;  }

    
    public final Map<Integer, Cube> spawnCubes = new HashMap<Integer, Cube>();
    public final Map<Integer, Player > players  = new HashMap<Integer, Player >();
    public final Map<Integer, Trigger> triggers = new HashMap<Integer, Trigger>();
    public final Map<Color, GlobalHolder> containers = new HashMap<Color, GlobalHolder>();
    
    public final Int3 size;

    
    // ====== Constructor======

    
    /**
     * Creates the RiemannCube with the given dimensions.
     * It will be filled with FloorTiles
     * size must be at least 1x1x1
     * 
     * @param size The dimensions of the cube
     */
    public RiemannCube(Int3 size) {
        if (size == null) throw new IllegalArgumentException();
        if (size.x <= 0 || size.z <= 0 || size.z <= 0) throw new IllegalArgumentException("A cube must be at least 1x1x1");
        
        this.size = size; // as it is a final vector, no copying is required
        cubes = new Cube[size.x][size.y][size.z];
        fillCubes();
    }

    private void fillCubes() {
        for (int x=0; x<size.x; ++x)
         for (int y=0; y<size.y; ++y)
          for (int z=0; z<size.z; ++z)
            cubes[x][y][z] = new Floor(new Int3(x,y,z));
    }


    // ====== validating ======
    // none of these will change the state
    
    public boolean isValidPlayer(int playerID) {
        return players.containsKey(playerID);
    }
    
    public boolean isInBounds(Int3 pos) {
        if (pos == null) return false;
        if (pos.x < 0 || pos.x >= size.x) return false;
        if (pos.y < 0 || pos.y >= size.y) return false;
        if (pos.z < 0 || pos.z >= size.z) return false;
        return true;
    }
    
    // ====== Action Validating ======
    
    
    public synchronized boolean isValidAction(Action a) {
        if (a == null) return false;
        if (a instanceof PlayerMove    )  return isValidMovePlayer ((PlayerMove)     a);
        if (a instanceof PlayerSpawning)  return isValidSpawnPlayer((PlayerSpawning) a);
        if (a instanceof ItemAction    )  return isValidItemAction ((ItemAction)     a);
        
        System.err.println(myName()+"OMG I haven't coded stuff for this action: " + a);
        return false;
    }
    
    private boolean isValidItemAction(ItemAction a) {
        if (!isValidPlayer(a.playerID)) return false;
        Player p = players.get(a.playerID);
        Cube pos = p.cube();
        if (a instanceof ItemPickup) return pos.hasItem() && !p.isHoldingItem(); 
        if (a instanceof ItemDrop  ) return p.isHoldingItem() && pos.canAddObject(p.item());
        if (a instanceof ItemUse   ) return pos.canUseItem(p.item());
        
        return false;
    }
    
    private boolean isValidMovePlayer(PlayerMove a) {
        if (!isValidPlayer(a.playerID)) return false;
        Player p = players.get(a.playerID);
        if (!isInBounds(a.movement)) return false;
        Cube to = getCube(a.movement);
        if (to.blocks(p)) // if the cube we are moving to blocks the player  
            return false;
        return true;
    }
    
    private boolean isValidSpawnPlayer(PlayerSpawning a) {
        if (isValidPlayer(a.playerID)) return false; // the player may not exist yet
        if (!isInBounds(a.pos)) return false;
        return true;
    }
    // ====== Actions ======
    
    /**
     * Tries to apply the given action.
     * This uses isValidAction() for action validation internally.
     * 
     * @param a The action that should be applied
     * @return Whether the action is valid and has been applied.
     */
    public synchronized boolean applyAction(Action a) {
        if (!isValidAction(a)) return false; // after this we assume nothing can go wrong
        else if (a instanceof PlayerMove    )  movePlayer((PlayerMove) a);
        else if (a instanceof PlayerSpawning)  spawnPlayer((PlayerSpawning) a);
        else if (a instanceof ItemAction    )  applyItemAction((ItemAction) a);
        else {
            System.err.println(myName()+"OMG I haven't coded stuff for this: " + a);
            return false;
        }
        
        return true;
    }
    
    private void applyItemAction(ItemAction a) {
        Player p = players.get(a.playerID);
        Cube cube = p.cube();
        if (a instanceof ItemPickup)   p.setItem(cube.popItem());
        else if (a instanceof ItemDrop) {
            cube.addObject(p.item());
            p.setItem(null);
        }
        else if (a instanceof ItemUse)  p.setItem(cube.useItem(p.item()));
        else  throw new Error("unhandeled ItemAction!");
    }
    
    private void movePlayer(PlayerMove action) {
        Player player = players.get(action.playerID); 
        Cube to = getCube(action.movement);
        if (!to.pos().equals(player.pos())) {
	        player.relPos.x -= 2*-(player.pos().x-to.pos().x);
	        player.relPos.y -= 2*-(player.pos().y-to.pos().y);
	        player.relPos.z -= 2*-(player.pos().z-to.pos().z);
        }
        player.move(to); 

    }
    
    private void spawnPlayer(PlayerSpawning action) {
        int id = action.playerID;
        Int3 pos = action.pos;
        
        Player p = new Player(getCube(pos), id);
        getCube(pos).addObject(p);
        players.put(p.id, p);
    }

    // ====== equals (used for testing) ======
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)        return true;
        if (obj == null)        return false;
        if (!(obj instanceof RiemannCube))        return false;
        
        RiemannCube other = (RiemannCube) obj;
        
        if (size.z != other.size.z || size.y != other.size.y || size.x != other.size.x)    return false;
        
        for (int x=size.x; x --> 0;) // oh, ben...
            for (int y=0; y<size.y; ++y)
                if (!Arrays.equals(cubes[x][y], other.cubes[x][y]))
                    return false;
 
        return true;
    }
    
    
    // ====== Slicing (used in editor) ======
    

    public Cube[][] verticalSlice(int x) {
        return cubes[x];
    }

    public Cube[][] horizontalSlice(int y) {
        Cube[][] slice = new Cube[size.x][size.z];
        for (int i = 0; i < size.x; i++) {
            slice[i] = cubes[i][y];
        }
        return slice;
    }

    public Cube[][] orthogonalSlice(int z) {
        Cube[][] slice = new Cube[size.x][size.y];
        for (int i = 0; i < size.x; i++)
            for (int j = 0; j < size.y; j++)
                slice[i][j] = cubes[i][j][z];
        return slice;
    }

    // ===== other stuff =====

    private String myName() { return "[RiemanCube] "; }
}
