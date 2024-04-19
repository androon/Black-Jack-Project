public class PlayerData {

    private String userName;
    private int playerID;
    private int handValue;
    private boolean playerStand;
    private int betAmount;

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
    
    

}