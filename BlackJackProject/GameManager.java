
public class GameManager {
	private int playerID;
	private static int count = 0;
	
	public GameManager() {
		
	}
	
	public int getPlayerID() {
		playerID = ++count;
		return playerID;
	}
}
