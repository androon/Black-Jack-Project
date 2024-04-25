import java.util.List;

public class GameLogic {
	private Deck deck;
	private int playerCheck;
	private int count = 0;
	public GameLogic(Deck deck) {
		this.deck = deck;
	}
	
	public int addCardToPlayer(ClientMessage fromClient, List<PlayerData> allGamePlayers) {
		int newHandVal = 0;
		
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData currPlayer = allGamePlayers.get(i);
			if(fromClient.getPlayerID() == currPlayer.getPlayerID()) {
				Card drawnCard = deck.drawCard();
				int cardVal = drawnCard.getValue();
				newHandVal = currPlayer.getHandValue() + cardVal;
				currPlayer.setHandValue(newHandVal);
			}
		}
		
		return newHandVal;
	}
	
	public void initialDeal(List<PlayerData> allGamePlayers) {
		
		while(count != allGamePlayers.size() * 2) {
			System.out.println("still looping");
			System.out.println("count: " + count);
			System.out.println("Playercheck: " + playerCheck);
			for(int i = 0; i < allGamePlayers.size();i++) {
				PlayerData currPlayer = allGamePlayers.get(i);
				if(currPlayer.getPlayerID() == playerCheck) {
					Card drawnCard = deck.drawCard();
					int cardVal = drawnCard.getValue();
					int newHandVal = currPlayer.getHandValue() + cardVal;
					currPlayer.setHandValue(newHandVal);
					playerCheck++;
					count++;
				}
			}
			if(playerCheck == allGamePlayers.size()) {
				playerCheck = 0;
			}
		}
	}
	
}
