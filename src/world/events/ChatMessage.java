package world.events;

public class ChatMessage extends Event {
    public final String message;
    public final int speaker;
    
    public ChatMessage(String message, int speaker) {
        this.message = message;
        this.speaker = speaker;
    }
    
}
