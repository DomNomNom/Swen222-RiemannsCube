package world;

import world.cubes.*;
import world.objects.GameObject;

public class RiemannCube {
  public Cube[][][] cubes;
  
  public Cube[][] getSlice(int x, int y, int norm_x, int norm_y, int norm_z) {
    int sliceWd = 0; // TODO
    int sliceHt = 0;
    Cube[][] slice = new Cube[sliceWd][sliceHt];
    // TODO
    return slice;
  }
  private Cube[][][] map;
	private int width, height, depth;
	
	public RiemannCube(int width, int height, int depth){
		this.width = width;
		this.height = height;
		this.depth = depth;
		map = new Cube[width][height][depth];
		fillCubes();
	}
	
	private void fillCubes(){
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				for(int k = 0; k < depth; k++)
					map[i][j][k] = new Floor();
	}
	
	public void setCube(int x, int y, int z, Cube c){
		map[x][y][z] = c;
	}
	
	public void setObject(int x, int y, int z, GameObject o){
		map[x][y][z].setObject(o);
	}
	
	public int width(){
		return width;
	}
	
	public int height(){
		return height;
	}
	
	public int depth(){
		return depth;
	}
	
	public Cube[][] verticalSlice(int x){
		return map[x];
	}
	
	public Cube[][] horizontalSlice(int y){
		Cube[][] slice = new Cube[width][depth];
		for(int i = 0; i < width; i++){
			slice[i] = map[i][y];
		}
		return slice;
	}
	
	public Cube[][] orthogonalSlice(int z){
		Cube[][] slice = new Cube[width][height];
		for(int i = 0; i < width; i++)
			for(int j = 0; j < height; j++)
				slice[i][j] = map[i][j][z];
		return slice;
	}
	
	public Cube getCube(int x, int y, int z){
		return map[x][y][z];
	}
}
