//package blackjack;

/*
 *Contains a list of all the players in the game 
 *Not similar to list of clients as clients are dealer and players
 *Has an instance of players data: attributes that are relevant to the game such as betAmount, actions
 * 
 * */
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
