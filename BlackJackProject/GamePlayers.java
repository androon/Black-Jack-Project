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
		//debug
		System.out.println(player.getUserName());
		System.out.println(player.getPlayerID());
		//
		
		gamePlayers.add(player);
		count++;
		
		System.out.println(count);
	}
	
	public int getNumPlayers() {
		return count;
	}
}
