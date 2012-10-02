package utils;

import java.io.Serializable;


/**
 * A very simple IMMUTABLE 3D integer vector.
 * The code is simple enough to be intuitive to anyone knowing about vectors 
 * It builds upon Int2 but extending it would not serve a useful purpose.
 * 
 * @author schmiddomi
 *
 */
public class Int3 implements Serializable{
    
    public final int x, y, z;
    
    
    /** Default constructor. Initializes ints to (0 0 0) */
    public Int3()                    { x=0; y=0; z=0; }
    public Int3(int X, int Y, int Z) { x=X; y=Y; z=Z; }
    
    /**
     * @return the new to the original vector (which now has updated its position
     */
    public Int3 add(Int3 o)              { return new Int3(x+o.x, y+o.y, z+o.z); }
    public Int3 add(int X, int Y, int Z) { return new Int3(x+  X, y+  Y, z+  Z); }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)                return true;
        if (obj == null)                return false;
        if (!(obj instanceof Int3))     return false;
        
        Int3 other = (Int3) obj;
        if (x != other.x)         return false;
        if (y != other.y)         return false;
        if (z != other.z)         return false;
        return true;

    }
    
    public String toString() {
        return String.format("(%d %d %d)", x, y, z);
    }
    //public FinalInt3 copy() { return new FinalInt3(x,y,z); }
}
