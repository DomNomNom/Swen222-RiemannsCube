package world;

import utils.Int2;
import utils.Int3;
import world.cubes.*;
import world.events.Action;
import world.objects.GameObject;

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

    
    /**
     * Tries to apply the given action.
     * This uses isValidAction() for action validation.
     * 
     * @param a The action that should be applied
     * @return Whether it succeeded.
     */
    public boolean applyAction(Action a) {
        
        return true;
    }
    
    public boolean isValidAction(Action a) {
        return true;
    }
    
    
    
    
    // ====== Slicing ======
    
    /**
     * Gets the cube slice though the cube.
     * The slice plane is defined by the position it passes though and the normal vector of the plane
     *   
     * @param pos The position it passes
     * @param normal The normal vector of the plane 
     * @return The slice
     */
    public Cube[][] getSlice(Int3 pos, Int3 normal) {
        Int2 sliceSize = new Int2(0, 0);
        Cube[][] slice = new Cube[sliceSize.x][sliceSize.y];

        slice = verticalSlice(0); // FIXME do this properly

        return slice;
    }

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
