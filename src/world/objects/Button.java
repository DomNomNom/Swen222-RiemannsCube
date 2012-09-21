package world.objects;

import java.awt.Color;

public class Button extends Trigger {

	Color color;
	int ID;
	
	public Button(int ID, Color color){
        this.color = color;
        this.ID = ID;
    }
	
	public Button(Color color){
		this.color = color;
	}
	
	public Color color(){
		return color;
	}
	
	public int getID(){
	    return ID;
	}

    @Override
    public String getClassName() {
        return "Trigger";
    }
}
