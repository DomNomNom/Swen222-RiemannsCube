package utils;


/**
 * A very simple IMMUTABLE 3D integer vector.
 * The code is simple enough to be intuitive to anyone knowing about vectors 
 * It builds upon Int2 but extending it would not serve a useful purpose.
 * 
 * @author schmiddomi
 *
 */
public class Int3 {
    
    public final int x, y, z;
    
    
    /** Default constructor. Initializes ints to (0 0 0) */
    public Int3()                    { x=0; y=0; z=0; }
    public Int3(int X, int Y, int Z) { x=X; y=Y; z=Z; }
    
    /**
     * @return the new to the original vector (which now has updated its position
     */
    public Int3 add(Int3 o)              { return new Int3(x+o.x, y+o.y, z+o.z); }
    public Int3 add(int X, int Y, int Z) { return new Int3(x+  X, y+  Y, z+  Z); }
    
    //public FinalInt3 copy() { return new FinalInt3(x,y,z); }
}
