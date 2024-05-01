package blackjack;
/*
 * after reading from the userData.txt file, objects of the class are instantiated that contain the username, password
 * each of the instances are then added to a linked list of type UserData
 * */
public class UserData {
	private String username;
	private String password;
	private boolean isDealer;
	private int winAmount;
	private int lossAmount;
	private int bankRoll;
	
	public UserData(String username, String password, boolean isDealer, int winAmount, int lossAmount, int bankRoll) {
		this.username = username;
		this.password = password;
		this.isDealer = isDealer;
		this.winAmount = winAmount;
		this.lossAmount = lossAmount;
		this.bankRoll = bankRoll;
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
	
	public int getWinAmount() {
		return this.winAmount;
	}
	
	public int getLossAmount() {
		return this.lossAmount;
	}
	
	public int getBankroll() {
		return this.bankRoll;
	}
	
	public void setBankroll(int bankRoll) {
		this.bankRoll = bankRoll;
	}
	
	public void setWinAmount(int winAmount) {
		this.winAmount = winAmount;
	}
	
	public void setLossAmount(int lossAmount) {
		this.lossAmount = lossAmount;
	}

}

