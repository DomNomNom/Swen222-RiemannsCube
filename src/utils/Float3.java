package utils;

public class Float3 {
    public float x, y, z;
    
    
    /** Default constructor. Initialises values to (0 0 0) */
    public Float3()                          { set(0, 0, 0); }
    public Float3(float X, float Y, float Z) { set(X, Y, Z); }
    
    
    /** sets the (x y z) values to the parameters */
    public void set(float X, float Y, float Z) {
        x = X;
        y = Y;
        z = Z;
    }
    public void set(Float3 copyFrom) {    set(copyFrom.x, copyFrom.y, copyFrom.z);  }

    public Float3 add(Float3 o) {
    	x+=o.x; y+=o.y; z+=o.z;
    	return this;
	}
    
    public Float3 add(int X, int Y, int Z) {
    	x+=  X; y+=  Y; z+=  Z;
    	return this;
	}
    
    public Float3 sub(Int3 o) {
        x+=o.x; y+=o.y; z+=o.z;
        return this;
    }
    
    public Float3 copy() { return new Float3(x,y,z); }
    
     @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(y);
        result = prime * result + Float.floatToIntBits(x);
        result = prime * result + Float.floatToIntBits(z);
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)                return true;
        if (obj == null)                return false;
        if (!(obj instanceof Float3))   return false;
        
        Float3 other = (Float3) obj;
        if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))         return false;
        if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))         return false;
        if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))         return false;
        return true;
    }
    
    public String toString() {
    	return String.format("(%3.2f, %3.2f, %3.2f)", x, y, z);
    }
}
