import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class GUIManager {
	Client client;
	String address;
	Socket clientSocket;
	
	public GUIManager(Client client, String address) {
		this.client = client;
		this.address = address;
		
	}
	
	public void getLogin() throws UnknownHostException, IOException, ClassNotFoundException {
		while(true) {
			Scanner myScanner = new Scanner(System.in);
			clientSocket = new Socket(address, 777);
			OutputStream outputStream = clientSocket.getOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			
			ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			
			ClientMessage login= new ClientMessage();
			System.out.println("Enter username:");
			String username = myScanner.nextLine();
			System.out.println("Enter password:");
			String password = myScanner.nextLine();
			
			login.setUsername(username);
			login.setPassword(password);
			login.setMessageType(MessageType.LOGIN);
			
			objectOutputStream.writeObject(login);
			
			
			//debugging - receive server response
			Response serverResponse = (Response) objectInputStream.readObject();
			if(serverResponse.getType() == ResponseType.LOGIN_SUCCESS) {
				System.out.println(serverResponse.getPlayerID());
			}
			
			
			
			/*if(serverResponse.getType() == ResponseType.LOGIN_SUCCESS) {
				if(!serverResponse.getIsDealer()) {
					//Player player = new Player();
				}else {
					//Dealer dealer = new Dealer
				}
			}*/
		}
		
	}
}
