package utils;

/**Pair stores a pair of objects that can be accessed and changed
 * 
 * @author David Saxon 300199370*/
public class Pair <E, F> {
	
	//VARIABLES
	private E first;
	private F second;

	//CONSTRUCTORS
	/**Construct a new pair with first and second set to null*/
	public Pair() {
		first = null;
		second = null;
	}
	
	/**Construct a new pair with the given objects
	 * @param first
	 * @param second*/
	public Pair(E first, F second) {
		this.first = first;
		this.second = second;
	}
	
	//METHODS
	/**Set the value of the first element
	 * @param first*/
	public void first(E first) {
		this.first = first;
	}
	
	/**Set the value of the second element
	 * @param second*/
	public void second(F second) {
		this.second = second;
	}
	
	/**return the first element
	 * @return the first element*/
	public E first() {
		return first;
	}
	
	/**return the second element
	 * @return the second element*/
	public F second() {
		return second;
	}
}
