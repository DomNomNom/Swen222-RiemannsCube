package world.objects;

import java.awt.Color;

public class Button extends Trigger {

	Color color;
	
	public Button(Color color){
		this.color = color;
	}
	
	public Color color(){
		return color;
	}

    @Override
    public String getClassName() {
        return "Trigger";
    }

    @Override
    public int id() {
        return 0; //FIXME
    }
}
