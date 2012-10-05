package world.events;

public class FullStateUpdate extends Event {

    private static final long serialVersionUID = -3780390585049581312L;

    public final String level;
    
    public FullStateUpdate(String level) {
        this.level = level;
    }

}
