package blackjack;



/*
 * Assigns each client with a immutable player ID
 * Options to resetDeck
 * */
public class GameManager {
	private int playerID;
	private static int count = 0;
	private Deck deck;
	private GameLogic gameLogic;
	
	protected GameManager() {
		gameLogic = new GameLogic();
		resetDeck();
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
	
	public void resetDeck() {
		deck = new Deck();
		deck.shuffle();
		gameLogic.setDeck(deck);
	}
	
	public void subtractID() {
		if(count > 0) {
			count--;
		}
	}
	
}
