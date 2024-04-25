import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

public class Player {
	private String username;
	private int bankRoll;
	private int playerID;
	private int numWin;
	private int numLoss;
	private Client client;
	ObjectInputStream objectInputStream;
	public Player(String username, int playerID, int bankRoll, int numWin, int numLoss, Client client, ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
		this.username = username;
		this.playerID = playerID;
		this.numWin = numWin;
		this.numLoss = numLoss;
		this.client = client;
		this.bankRoll = bankRoll;
		this.objectInputStream = objectInputStream;
		displayGUI();
	}
	
	private void displayGUI() throws IOException, ClassNotFoundException {
		//If message recieved = player turn - enable buttons
		
		Scanner myScanner = new Scanner(System.in);
		/*System.out.println(username);
		System.out.println(playerID);
		System.out.println(numWin);
		System.out.println(numLoss);
		System.out.println(bankRoll);
		*/
		
		while(true) {
			
			
			Response fromServer = (Response) objectInputStream.readObject();
			if(fromServer.getType() == ResponseType.ALL_BETS) {
				placeBet();
			}
			if(fromServer.getType() == ResponseType.PLAYER_TURN) {
				
				System.out.println("1. hit");
				System.out.println("2. stand");
				System.out.println("3. double down");
				int choice = myScanner.nextInt();
				switch(choice) {
					case 1:
						hit();
						break;
						
					case 2:
						stand();
						break;
							
					case 3: 
						doubleDown();
						break;
						
					case 9:
						debug();
						break;
						
					}
				}else if(fromServer.getType() == ResponseType.PLAYER_TURN_END) {
					
				}
			}
		}


	
	public void placeBet() throws IOException {
		Scanner placeBetScanner = new Scanner(System.in);
		System.out.println("\nHow much do you want to bet?");
		int betAmount = placeBetScanner.nextInt();	
		client.sendBetRequest(playerID, betAmount);
	}
	
	public void hit() throws IOException{
		client.sendHitRequest(playerID);
	}
	
	public void stand() throws IOException{
		client.sendStandRequest(playerID);
	}
	
	public void doubleDown() throws IOException{
		client.sendDoubleDownRequest(playerID);
	}
	
	public void debug() throws IOException{
		client.debug();
	}
}
