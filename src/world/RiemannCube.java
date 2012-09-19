package world;

import world.cubes.*;
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


    
    
    
    
    // ====== Slicing ======
    
    public Cube[][] getSlice(int x, int y, int z, int norm_x, int norm_y, int norm_z) {
        int sliceWd = 0;
        int sliceHt = 0;
        Cube[][] slice = new Cube[sliceWd][sliceHt];

        // TODO

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
