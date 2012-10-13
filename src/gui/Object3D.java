package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import utils.Float3;

/**Holds an object file that can be rendered onto the screen
 * 
 * @author David Saxon 300199370*/
public class Object3D {

	//FIELDS
	private List<Float3> vertexList = new ArrayList<Float3>(); //a list of all the vertices
	private List<Face> faceList = new ArrayList<Face>(); //a list of all the faces
	
	
	//CONSTRUCTOR
	/**Contruct a new 3d object
	 * @param file the file to read the object from
	 */
	Object3D(File file) {
		parse(file);
	}
	
	//METHODS
	/**Reads the object file
	 * @param file the file to read from*/
	private void parse(File file) {
		try {
		Scanner scan = new Scanner(file);
		while (scan.hasNext()) { //read through the entire file 
			String tag = scan.next(); //read the tag
			System.out.println(tag);
			if (tag.equals("v")) { //read a vertex
				scan.nextFloat();
				scan.nextFloat();
				scan.nextFloat();
			}
			else if (tag.equals("f")) { //read a face
				scan.nextInt();
				scan.nextInt();
				scan.nextInt();
				scan.nextInt();
			}
			else throw new Exception(); //bad
		}
		} catch (Exception e) {System.out.println("Not a valid wavefront file");};
	}
	
	/**Represents a face of that holds indexes to its vertices*/
	private class Face {
		
		//FIELDS
		//the faces vertices
		public int first;
		public int second;
		public int third;
		public int fourth;
		
		//CONSTRUCTOR
		/**Constructs a new face*/
		public Face(int first, int second, int third, int fourth) {
			this.first = first;
			this.second = second;
			this.third = third;
			this.fourth = fourth;
		}
	}
	
	
}
