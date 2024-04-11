import java.io.Serializable;

public class ClientMessage implements Serializable{
	private String username;
	private String password;
	private boolean isDealer;
	private int depAmount;
	private int withdrawAmount;
	private int playerID;
	private int winAmount;
	private int lossAmount;
	private int betAmount;
	private MessageType type;
	
	//Default constructor setting default values
	public ClientMessage() {
		this.username = "Undefined";
		this.password = "undefined";
		this.isDealer = false;
		this.depAmount = 0;
		this.withdrawAmount = 0;
		this.playerID = 0;
		this.winAmount = 0;
		this.lossAmount = 0;
		this.betAmount = 0;
		this.type = MessageType.UNKNOWN;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public boolean getIsDealer() {
		return this.isDealer;
	}
	
	public int getDepAmount() {
		return this.depAmount;
	}
	
	public int getWithdrawAmount() {
		return this.withdrawAmount;
	}
	
	public int getPlayerID() {
		return this.playerID;
	}
	
	public int getWinAmount() {
		return this.winAmount;
	}
	
	public int getLossAmount() {
		return this.lossAmount;
	}
	
	public int getBetAmount() {
		return this.betAmount;
	}
	public MessageType getType() {
		return this.type;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	
	public void setIsDealer(boolean isDealer) {
		this.isDealer = isDealer;
	}
	
	public void setDepAmount(int depAmount) {
		this.depAmount = depAmount;
	}
	
	public void setWithdrawAmount(int withdrawAmount) {
		this.withdrawAmount = withdrawAmount;
	}
	
	public void setWinAmount(int winAmount) {
		this.winAmount = winAmount;
	}
	
	public void setLossAmount(int lossAmount) {
		this.lossAmount = lossAmount;
	}
	
	public void setBet(int betAmount) {
		this.betAmount = betAmount;
	}
	
	public void setMessageType(MessageType type) {
		this.type = type;
	}
	
	
}
