package utils;

/**
 * A very brief 3D integer vector
 * @author schmiddomi
 *
 */
public class Int3 extends Int2 {
    public int x, y, z;
    
    public Int3(int X, int Y, int Z) {
        super(X, Y);
        this.z = Z;
    }

    public void add(int X, int Y, int Z) { x+=X; y+=Y; z+=Z; }
    public void add(Int3 o) { x+=o.x; y+=o.y; z+=o.z; }
    
    public Int3 copy() { return new Int3(x,y,z); }
}
