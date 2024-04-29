package blackjack;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
/*
 * this class is called when the server is instantiated
 * reads a text file called userData.txt that contains login credentials and creates a linked list of each user with their username, password, whether the client is a dealer or player
 * also has an access modifier to get that list  
 * */
public class LoadUserData {
	List<UserData> userData = new LinkedList<UserData>();
	
	public LoadUserData() {
		startLoad();
	}
	
	public void startLoad() {
		File userFile = new File("userData.txt");	
		try{
			Scanner fileScanner = new Scanner(userFile);
		while(fileScanner.hasNextLine()) {
					String wholeLine = fileScanner.nextLine();
					String[] data = wholeLine.split(",");
					if(data.length >= 6) {
						String username = data[0];
						String password = data[1];
						boolean isDealer = Boolean.parseBoolean(data[2]);
						int winAmount = Integer.parseInt(data[3]);
						int lossAmount = Integer.parseInt(data[4]);
						int bankRoll = Integer.parseInt(data[5]);
						UserData newUser = new UserData(username, password, isDealer, winAmount, lossAmount, bankRoll);
						userData.add(newUser);
					}
				}
				fileScanner.close();
			}catch(Exception e) {
				System.out.println(e);
			}
	}
	
	public List<UserData> getUserList(){
		return userData;
	}
}
