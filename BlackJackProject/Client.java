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
	
	public void sendHitRequest(int playerID) throws IOException {
		ClientMessage hitMessage = new ClientMessage();
		hitMessage.setPlayerID(playerID);
		hitMessage.setMessageType(MessageType.HIT);
		objectOutputStream.writeObject(hitMessage);
	}
	
	public void sendStandRequest(int playerID) throws IOException{
		ClientMessage standMessage = new ClientMessage();
		standMessage.setPlayerID(playerID);
		standMessage.setMessageType(MessageType.STAND);
		objectOutputStream.writeObject(standMessage);
	}
	
	public void sendDoubleDownRequest(int playerID) throws IOException{
		ClientMessage doubledownMessage = new ClientMessage();
		doubledownMessage.setPlayerID(playerID);
		doubledownMessage.setMessageType(MessageType.DOUBLE_DOWN);
		objectOutputStream.writeObject(doubledownMessage);
	}
	
	public void sendStartRoundRequest() throws IOException {
		ClientMessage startRoundMessage = new ClientMessage();
		startRoundMessage.setMessageType(MessageType.START_ROUND);
		objectOutputStream.writeObject(startRoundMessage);
	}
	
	public void sendEndRoundRequest() throws IOException{
		ClientMessage endRoundMessage = new ClientMessage();
		endRoundMessage.setMessageType(MessageType.END_ROUND);
		objectOutputStream.writeObject(endRoundMessage);
	}
	
	
	public void debug() throws IOException{
		ClientMessage debug = new ClientMessage();
		debug.setMessageType(MessageType.DEBUG);
		objectOutputStream.writeObject(debug);
	}
	
}


