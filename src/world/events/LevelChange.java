package world.events;

public class LevelChange extends Event {
    private static final long serialVersionUID = 95977206723681385L;

    public final String levelName;
    
    public LevelChange(String levelName) {
        this.levelName = levelName;
    }
}
