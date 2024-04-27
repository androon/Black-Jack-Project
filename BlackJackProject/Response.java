import java.io.Serializable;

public class Response implements Serializable{
	private ResponseType type;
	private boolean validated;
	private int playerID;
	private boolean isDealer;
	private int winAmount;
	private int lossAmount;
	private int bankRoll;
	private String username;
	//private GamePlayers gamePlayers;
	
	public Response() {
		this.type = ResponseType.UNKNOWN;
		this.validated = false;
		this.playerID = 0;
		this.isDealer = false;
		this.winAmount = 0;
		this.lossAmount = 0;
		this.bankRoll = 0;
	}
	
	public ResponseType getType() {
		return this.type;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public boolean getValidated() {
		return this.validated;
	}
	
	public int getPlayerID() {
		return this.playerID;
	}
	
	public boolean getIsDealer() {
		return this.isDealer;
	}
	
	public int getBankroll() {
		return this.bankRoll;
	}
	
	public int getWinAmount() {
		return this.winAmount;
	}
	
	public int getLossAmount() {
		return this.lossAmount;
	}
	
	public void setType(ResponseType type) {
		this.type = type;
	}
	
	public void setValidated(boolean validated) {
		this.validated = validated;
	}
	
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	
	public void setDealer(boolean isDealer) {
		this.isDealer = isDealer;
	}
	
	public void setWinAmount(int winAmount) {
		this.winAmount = winAmount;
	}
	
	public void setLossAmount(int lossAmount) {
		this.lossAmount = lossAmount;
	}
	
	public void setBankRoll(int bankRoll) {
		this.bankRoll = bankRoll;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
}
