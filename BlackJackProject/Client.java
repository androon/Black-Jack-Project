import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client {
	private GUIManager gui;
	Socket clientSocket;
	OutputStream outputStream;
	ObjectOutputStream objectOutputStream;
	ObjectInputStream objectInputStream;
	
	public static void main(String[]  args) throws  IOException, ClassNotFoundException 
	{
		Client client = new Client();
		client.setupClient();
	}
	
	public void setupClient() throws ClassNotFoundException, UnknownHostException, IOException{

				Scanner myScanner = new Scanner(System.in);
				System.out.println("Enter Server Address");
				
				String address = myScanner.nextLine();
				clientSocket = new Socket(address, 777);
				objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
				gui = new GUIManager(this, address, clientSocket, objectOutputStream, objectInputStream);
				
				gui.getLogin();

				
	}
	
	public void sendBetRequest(int playerID, int betAmount) throws IOException {
		ClientMessage betMessage = new ClientMessage();
		betMessage.setPlayerID(playerID);
		betMessage.setBetAmount(betAmount);
		betMessage.setMessageType(MessageType.BET);
		objectOutputStream.writeObject(betMessage);
	}
	
	
}


