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
    
    /**
     * Calculate the angle of rotation for this vector
     * @return the angle of rotation
     */
    public float heading() {
      float angle = (float) Math.atan2(y, x);
      return 1*angle;
    }
    
    @Override
   public int hashCode() {
       final int prime = 31;
       int result = 1;
       result = prime * result + Float.floatToIntBits(y);
       result = prime * result + Float.floatToIntBits(x);
       return result;
   }
   @Override
   public boolean equals(Object obj) {
       if (this == obj)                return true;
       if (obj == null)                return false;
       if (!(obj instanceof Float2))   return false;
       
       Float2 other = (Float2) obj;
       if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))         return false;
       if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))         return false;
       return true;
   }
    
    public String toString() {
    	return String.format("(%3.2f, %3.2f)", x, y);
    }

}