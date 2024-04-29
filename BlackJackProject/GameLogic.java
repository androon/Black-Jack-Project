import java.util.List;

public class GameLogic {
	private Deck deck;
	private int playerCheck = 0;
	private int count = 0;
	private int playerIDOutcomeCheck = 1;
	
	public GameLogic() {
	}
	
	public int addCardToPlayer(ClientMessage fromClient, List<PlayerData> allGamePlayers) {
		int newHandVal = 0;
		int cardVal =0;
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData currPlayer = allGamePlayers.get(i);
			if(fromClient.getPlayerID() == currPlayer.getPlayerID()) {
				Card drawnCard = deck.drawCard();
				currPlayer.addCardToHand(drawnCard);
				cardVal = drawnCard.getValue();
				
				//If card drawn is an ace check if it busts the hand
				if(cardVal == 1){
					if(currPlayer.getHandValue() + 11 <= 21) {
						currPlayer.setHandWithAce(currPlayer.getHandValue() + 11);
					}else if(currPlayer.getHandValue() + 11 > 21) {
						currPlayer.setHandWithAce(0);
					}
				}else if(currPlayer.getHandWithAce() != 0 && cardVal != 1) {
					int checkAceAdd = currPlayer.getHandWithAce() + cardVal;
					//If it's over 21 get rid of the ace hand
					if(checkAceAdd > 21) {
						currPlayer.setHandWithAce(0);
					}else if(checkAceAdd <= 21) {
						currPlayer.setHandWithAce(currPlayer.getHandWithAce() + cardVal);
					}
				}
				
				//Low end of cards will always be changed after a hit regadless of ace or not
				newHandVal = currPlayer.getHandValue() + cardVal;
				currPlayer.setHandValue(newHandVal);
			}
		}
		
		return newHandVal;
	}
	
	public void initialDeal(List<PlayerData> allGamePlayers){

		
		try{
		while(count != allGamePlayers.size() * 2) {
			for(int i = 0; i < allGamePlayers.size();i++) {
				PlayerData currPlayer = allGamePlayers.get(i);
				if(currPlayer.getPlayerID() == playerCheck) {
					Card drawnCard = deck.drawCard();
					currPlayer.addCardToHand(drawnCard);
					int cardVal = drawnCard.getValue();
					
					if(cardVal == 1) {
						currPlayer.setHandWithAce(currPlayer.getHandValue() + 11);
						
						//If 2 aces = 22 which is not allowed
						if(currPlayer.getHandWithAce() > 21) {
							currPlayer.setHandWithAce(0);
						}
					}else if(currPlayer.getHandWithAce() != 0) {
						currPlayer.setHandWithAce(11 + cardVal);
					}
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
		}catch(Exception e){
			
		}
	}
	
	public void checkOutcome(List<PlayerData> allGamePlayers) {
		PlayerData dealerData = null;
		boolean allOutcomesChecked = false;
		int countOutcome = 0;
		//Find dealer in list
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData currPlayer = allGamePlayers.get(i);
			if(currPlayer.getPlayerID() == 0) {
				dealerData = currPlayer;
				break;
			}
		}
		
		//Start checking players vs dealer
		while(allOutcomesChecked == false) {
			for(int i = 0; i < allGamePlayers.size(); i++) {
				PlayerData currPlayer = allGamePlayers.get(i);
				if(currPlayer.getPlayerID() == playerIDOutcomeCheck) {
					if(currPlayer.getBust() == true) {
						currPlayer.setLossAmount(currPlayer.getLossAmount() + 1);					
					}else if(dealerData.getHandValue() > 21 && currPlayer.getBust() == false){
						currPlayer.setBankRoll(currPlayer.getBankRoll() + (currPlayer.getBetAmount() * 2));
						currPlayer.setWinAmount(currPlayer.getWinAmount() + 1);
					}else {
						if(dealerData.getHandValue() > currPlayer.getHandValue()) {
							currPlayer.setLossAmount(currPlayer.getLossAmount() + 1);
						}else if(dealerData.getHandValue() < currPlayer.getHandValue()) {
							currPlayer.setBankRoll(currPlayer.getBankRoll() + (currPlayer.getBetAmount() * 2));
							currPlayer.setWinAmount(currPlayer.getWinAmount() + 1);
						}else if(dealerData.getHandValue() == currPlayer.getHandValue()) {
							currPlayer.setBankRoll(currPlayer.getBankRoll() + currPlayer.getBetAmount());
						}
					}
					playerIDOutcomeCheck++;
					countOutcome++;
				}
			}
			if(countOutcome == allGamePlayers.size() - 1) {
				allOutcomesChecked = true;
			}
		}
	}
	
	
	
	public void setDeck(Deck deck) {
		this.deck = deck;
	}
	
	public void reset() {
		this.playerCheck = 0;
		this.count = 0;
		this.playerIDOutcomeCheck = 1;
	}
}
