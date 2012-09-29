package world.events;

public class ChatMessage extends ChatEvent {
    public final String message;
    public final int speakerID;
    
    public ChatMessage(String message, int speakerID) {
        this.message = message;
        this.speakerID = speakerID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + speakerID;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        System.out.println("COMARING : " + obj);
        if (this == obj)                                return true;
        if (obj == null)                                return false;
        if (getClass() != obj.getClass())               return false;
        ChatMessage other = (ChatMessage) obj;
        if (message == null && other.message != null)   return false;
        else if (!message.equals(other.message))        return false;
        if (speakerID != other.speakerID)               return false;
        return true;
    }
    
}
