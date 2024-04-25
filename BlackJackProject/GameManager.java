
public class GameManager {
	private int playerID;
	private static int count = 0;
	private Deck deck;
	private GameLogic gameLogic;
	public GameManager() {
		deck = new Deck();
		gameLogic = new GameLogic(getDeck());
	}
	
	public int getPlayerID() {
		playerID = ++count;
		return playerID;
	}
	
	public Deck getDeck() {
		deck.shuffle();
		return deck;
	}
	
	public GameLogic getGameLogic() {
		return gameLogic;
	}
}
