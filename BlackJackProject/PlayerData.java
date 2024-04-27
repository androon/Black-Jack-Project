import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class PlayerData implements Serializable{

    private String userName;
    private int playerID;
    private int handValue;
    private boolean playerStand;
    private int betAmount;
    private boolean playerBust;
    private int bankRoll;
    private int handWithAce = 0;
    private boolean isDealer;
    private int numHand = 0; // Start with 0 cards in hand
    List<Card> hand = new LinkedList<>();

    public PlayerData(String userName, int playerID, int bankRoll, boolean isDealer)
    {
        this.userName = userName;
        this.playerID = playerID;
        this.handValue = 0;
        this.playerStand = false;
        this.betAmount = 0;
        this.bankRoll = bankRoll;
        this.isDealer = isDealer;
    }
    
    public void reset() {
    	handValue = 0;
    	playerStand = false;
    	playerBust = false;
    	betAmount = 0;
    	handWithAce = 0;
    	hand.clear();
    	
    }
    public String getUserName()
    {
        return userName;
    }

    public int getPlayerID()
    {
        return playerID;
    }

    public int getHandValue()
    {
        return handValue;
    }

    public int getBetAmount()
    {
        return betAmount;
    }
    
    public boolean getStand() {
    	return playerStand;
    }
    
    public boolean getBust() {
    	return playerBust;
    }
    
    public int getBankRoll() {
    	return bankRoll;
    }

    public void setHandValue(int value)
    {
        handValue=value;
    }

    public void setBetAmount(int amount)
    {
        betAmount=amount;
    }
    
    public void setStand() {
    	playerStand = true;
    }
    
    public void setPlayerBust() {
    	playerBust = true;
    }
    
    public void setBankRoll(int value) {
    	bankRoll = value;
    }
    
    public void addCardToHand(Card card) {
    	numHand++;
    	hand.add(card);
    }
    
    public List<Card> getCardsInHand(){
    	return hand;
    }
    
    public String toStringCards() {
    	String returnString = "";
    	for(int i = 0; i < hand.size(); i++) {
    		Card card = hand.get(i);
    		returnString += String.valueOf(card.getValue());
    		if(i < hand.size() -1 ) {
    			returnString += ", ";
    		}
    	}
    	
    	return returnString;
    }
    
    public void setHandWithAce(int value) {
    	handWithAce = value;
    }
    
    public int getHandWithAce() {
    	return handWithAce;
    }
    
    public boolean getIsDealer() {
    	return isDealer;
    }
    
    
    
    public void resetHand() {
    	hand.clear();
    }
    
   
}