import java.io.IOException;
import java.util.Scanner;

public class Player {
	private String username;
	private int bankRoll;
	private int playerID;
	private int numWin;
	private int numLoss;
	private Client client;
	
	public Player(String username, int playerID, int bankRoll, int numWin, int numLoss, Client client) throws IOException {
		this.username = username;
		this.playerID = playerID;
		this.numWin = numWin;
		this.numLoss = numLoss;
		this.client = client;
		this.bankRoll = bankRoll;
		displayGUI();
	}
	
	private void displayGUI() throws IOException {
		Scanner myScanner = new Scanner(System.in);
		System.out.println(username);
		System.out.println(playerID);
		System.out.println(numWin);
		System.out.println(numLoss);
		System.out.println(bankRoll);

		
		while(true) {
			System.out.println("1. place bet");
			System.out.println("2. hit");
			System.out.println("3. stand");
			System.out.println("4. double down");
			int choice = myScanner.nextInt();
			switch(choice) {
				case 1:
					System.out.println(username);
					placeBet();
					break;
				case 2:
					System.out.println(playerID);
					break;
					
				case 3: 
					System.out.println(numWin);
					break;
					
				case 4:
					System.out.println(numLoss);
					break;
					
				case 5:
					System.out.println(bankRoll);
					break;
			}
		}

	}
	
	public void placeBet() throws IOException {
		Scanner placeBetScanner = new Scanner(System.in);
		System.out.println("How much do you want to bet?");
		int betAmount = placeBetScanner.nextInt();	
		client.sendBetRequest(playerID, betAmount);
		//placeBetScanner.close();
		return;
	}
}
