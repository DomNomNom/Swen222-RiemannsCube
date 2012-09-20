package utils;


/**
 * A very simple 2D integer vector.
 * The source code is simple enough to be intuitive to anyone knowing about vectors 
 * 
 * @author schmiddomi
 *
 */
public class Int2 {
    
    public int x, y;
    
    
    public Int2(int X, int Y) {
        x = Y;
        y = Y;
    }
    
    
    public void add(Int2 o)              { x+=o.x;  y+=o.y; }
    public void add(int X, int Y, int Z) { x+=  X;  y+=  Y; }
    
    public Int2 copy() { return new Int2(x, y); }
}