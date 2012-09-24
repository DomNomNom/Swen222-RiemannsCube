package utils;


/**
 * A very simple 3D integer vector.
 * The code is simple enough to be intuitive to anyone knowing about vectors 
 * It builds upon Int2 but extending it would not serve a useful purpose.
 * 
 * @author schmiddomi
 *
 */
public class Int3 {
    
    public int x, y, z;
    
    
    /** Default constructor. Initializes ints to (0 0 0) */
    public Int3()                    { set(0, 0, 0); }
    public Int3(int X, int Y, int Z) { set(X, Y, Z); }
    
    
    /** sets the (x y z) values to the parameters */
    public void set(int X, int Y, int Z) {
        x = X;
        y = Y;
        z = Z;
    }
    public void set(Int3 copyFrom) {    set(copyFrom.x, copyFrom.y, copyFrom.z);  }

    public void add(Int3 o)              { x+=o.x; y+=o.y; z+=o.z; }
    public void add(int X, int Y, int Z) { x+=  X; y+=  Y; z+=  Z; }
    
    public Int3 copy() { return new Int3(x,y,z); }
}
