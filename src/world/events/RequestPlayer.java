package world.events;

/**Requests a player form the server
 * 
 * @author David Saxon 300199370
 */
public class RequestPlayer extends Event {
    
    private String playerName;
    public String playerName(){ return playerName;}
    
	//CONSTRUCTOR
	/**Creates a new player request*/
	public RequestPlayer(String playerName) {
	    this.playerName = playerName;
	}
	
}
