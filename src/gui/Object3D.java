package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.media.opengl.GL2;

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
			if (tag.equals("v")) { //read a vertex
				vertexList.add(new Float3(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()));
			}
			else if (tag.equals("f")) { //read a face
				faceList.add(new Face(scan.nextInt(), scan.nextInt(), scan.nextInt(), scan.nextInt()));
			}
			else throw new Exception(); //bad
		}
		} catch (Exception e) {System.out.println("Not a valid wavefront file");};
	}
	
	public void render(GL2 gl) {
		for (Face f : faceList) {
			//get the faces vertices
			Float3 v1 = vertexList.get(f.first-1);
			Float3 v2 = vertexList.get(f.second-1);
			Float3 v3 = vertexList.get(f.third-1);
			Float3 v4 = vertexList.get(f.fourth-1);
			
			//start drawing the face
			gl.glBegin(GL2.GL_QUADS);
			gl.glVertex3f(v1.x, v1.y, v1.z);
			gl.glVertex3f(v2.x, v2.y, v2.z);
			gl.glVertex3f(v3.x, v3.y, v3.z);
			gl.glVertex3f(v4.x, v4.y, v4.z);
			gl.glEnd();
		}
	}
	
	public void renderTex(GL2 gl) {
		for (Face f : faceList) {
			//get the faces vertices
			Float3 v1 = vertexList.get(f.first-1);
			Float3 v2 = vertexList.get(f.second-1);
			Float3 v3 = vertexList.get(f.third-1);
			Float3 v4 = vertexList.get(f.fourth-1);
			
			//start drawing the face
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(v1.x, v1.y, v1.z);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(v2.x, v2.y, v2.z);
			gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(v3.x, v3.y, v3.z);
			gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(v4.x, v4.y, v4.z);
			gl.glEnd();
		}
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
