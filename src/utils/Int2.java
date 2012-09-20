package utils;


/**
 * A very simple 2D integer vector.
 * The source code is simple enough to be intuitive to anyone knowing about vectors 
 * 
 * 
 * @author schmiddomi
 *
 */
public class Int2 {
    
    public int x, y;
    
    
    /** Default constructor. Initializes ints to (0 0) */
    public Int2()             { set(0, 0); }
    public Int2(int X, int Y) { set(X, Y); }
    
    
    /** sets the x and y values to the parameters */
    public void set(int X, int Y) {
        x = X;
        y = Y;
    }
    
    public void add(Int2 o)       { x+=o.x;  y+=o.y; }
    public void add(int X, int Y) { x+=  X;  y+=  Y; }
    
    public Int2 copy() { return new Int2(x, y); }
}