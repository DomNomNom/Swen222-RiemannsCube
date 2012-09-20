package utils;

/**
 * A very simple 3D integer vector
 * The code is simple enough to be intuitive to anyone knowing about vectors 
 * 
 * @author schmiddomi
 *
 */
public class Int3 {
    
    public int x, y, z;
    
    public Int3(int X, int Y, int Z) {
        x = X;
        y = Y;
        z = Z;
    }
    

    public void add(Int3 o)              { x+=o.x; y+=o.y; z+=o.z; }
    public void add(int X, int Y, int Z) { x+=  X; y+=  Y; z+=  Z; }
    
    public Int3 copy() { return new Int3(x,y,z); }
}
