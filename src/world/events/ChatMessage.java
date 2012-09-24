package world.events;

public class ChatMessage extends Event {
    public final String message;
    public final int speakerID;
    
    public ChatMessage(String message, int speakerID) {
        this.message = message;
        this.speakerID = speakerID;
    }
    
}
