import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
//Purpose: Request login and determine GUI
public class GUIManager {
	Client client;
	String address;
	Socket clientSocket;
	private boolean loginSuccess;
	ObjectOutputStream objectOutputStream;
	ObjectInputStream objectInputStream;
	
	public GUIManager(Client client, String address, Socket clientSocket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
		this.client = client;
		this.address = address;
		this.clientSocket = clientSocket;
		this.objectOutputStream = objectOutputStream;
		this.objectInputStream = objectInputStream;
	}
	
	public void getLogin() throws UnknownHostException, IOException, ClassNotFoundException {
			Scanner myScanner = new Scanner(System.in);
			
			//Not necessary now commented out in case needed but seems like the cleanup is working
			/*(OutputStream outputStream = clientSocket.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			
			ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			*/
			//ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			while(!loginSuccess) {
				ClientMessage login= new ClientMessage();
				System.out.println("Enter username:");
				String loginUsername = myScanner.nextLine();
				System.out.println("Enter password:");
				String loginPassword = myScanner.nextLine();
				
				login.setUsername(loginUsername);
				login.setPassword(loginPassword);
				login.setMessageType(MessageType.LOGIN);
				
				objectOutputStream.writeObject(login);
				
				
				//debugging - receive server response
				Response serverResponse = (Response) objectInputStream.readObject();
				
				//If the login is for a player and successful
				if(serverResponse.getType() == ResponseType.LOGIN_SUCCESS && serverResponse.getIsDealer() == false) {
					System.out.println(serverResponse.getUsername());
					System.out.println(serverResponse.getPlayerID());
					System.out.println(serverResponse.getBankroll());
					System.out.println(serverResponse.getWinAmount());
					String username = serverResponse.getUsername();
					int playerID = serverResponse.getPlayerID();
					int bankRoll = serverResponse.getBankroll();
					int winAmount = serverResponse.getWinAmount();
					int lossAmount = serverResponse.getLossAmount();
					//Use to break out of loop
					this.loginSuccess = true;
					
					Player player = new Player(username, playerID, bankRoll, winAmount, lossAmount, client, objectInputStream);
					
				}else if(serverResponse.getType() == ResponseType.LOGIN_SUCCESS && serverResponse.getIsDealer() == true) {
					System.out.println(serverResponse.getIsDealer());
					this.loginSuccess = true;
					Dealer dealer = new Dealer(client, serverResponse.getPlayerID(), serverResponse.getIsDealer(), objectInputStream);
				}else if(serverResponse.getType() == ResponseType.LOGIN_FAIL) {
					this.loginSuccess = false;
			}
			
			
		
		
		}
	}
}
