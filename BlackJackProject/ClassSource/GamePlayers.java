package ClassSource;

import java.util.LinkedList;
import java.util.List;

public class GamePlayers {
	List<PlayerData> gamePlayers = new LinkedList<PlayerData>();
	private static int count = 0;
	
	public GamePlayers() {
	}
	
	
	public List<PlayerData> getGamePlayers(){
		return gamePlayers;
	}
	
	public void addPlayer(PlayerData player) {		
		gamePlayers.add(player);
		count++;
	}
	
	public int getNumPlayers() {
		return count;
	}
}
