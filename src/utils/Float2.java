package utils;


/**
 * A very simple 2D float vector.
 * The source code is simple enough to be floatuitive to anyone knowing about vectors 
 * 
 * @author David Saxon 300199370
 */
public class Float2 {
    
    public float x, y;
    
    
    /** Default constructor. Initialises floats to (0 0) */
    public Float2()             { set(0, 0); }
    public Float2(float X, float Y) { set(X, Y); }
    
    
    /** sets the x and y values to the parameters */
    public void set(float X, float Y) {
        x = X;
        y = Y;
    }
    
    public void add(Float2 o)       { x+=o.x;  y+=o.y; }
    public void add(float X, float Y) { x+=  X;  y+=  Y; }
    
    public Float2 copy() { return new Float2(x, y); }
}