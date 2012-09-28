package server;

import world.events.Event;

/**
 * A class that represents a change to the world.
 * Holds the client ID that made the change and the Event.
 * @author feshersiva
 */
public class Change {
    
    protected int clientId;
    protected Event event;
    
    public Change(int id, Event e){
        this.clientId = id;
        this.event = e;
    }
}
