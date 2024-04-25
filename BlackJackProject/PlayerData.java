public class PlayerData {

    private String userName;
    private int playerID;
    private int handValue;
    private boolean playerStand;
    private int betAmount;
    private boolean playerBust;

    public PlayerData(String userName, int playerID)
    {
        this.userName = userName;
        this.playerID = playerID;
        this.handValue = 0;
        this.playerStand = false;
        this.betAmount = 0;
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
    
    

}