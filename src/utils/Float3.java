package utils;

public class Float3 {
    public float x, y, z;
    
    
    /** Default constructor. Initializes values to (0 0 0) */
    public Float3()                          { set(0, 0, 0); }
    public Float3(float X, float Y, float Z) { set(X, Y, Z); }
    
    
    /** sets the (x y z) values to the parameters */
    public void set(float X, float Y, float Z) {
        x = X;
        y = Y;
        z = Z;
    }
    public void set(Float3 copyFrom) {    set(copyFrom.x, copyFrom.y, copyFrom.z);  }

    public void add(Float3 o)            { x+=o.x; y+=o.y; z+=o.z; }
    public void add(int X, int Y, int Z) { x+=  X; y+=  Y; z+=  Z; }
    
    public Float3 copy() { return new Float3(x,y,z); }
}
