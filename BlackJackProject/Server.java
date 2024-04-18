import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;




public class Server {
	static LoadUserData loadData;
	static GameManager gameManager;
	static GamePlayers gamePlayers;
	static ServerSocket serverSocket;
	List<ClientHandler> clients;
	ClientHandler clientHandler;
	private int currPlayer = 1;
	private int currPlayerBet = 1;
	
	public static void main(String[]  args) throws IOException, ClassNotFoundException {
		loadData = new LoadUserData();
		gameManager = new GameManager();
		gamePlayers = new GamePlayers();
		Server server = new Server(777);
		server.start();
	}
	
	public Server(int port) {
		clients = new LinkedList<>();
		try {
			serverSocket = new ServerSocket(port);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public void start() {
		try {
			InetAddress localhost = InetAddress.getLocalHost();
            System.out.println("Server IP Address: " + localhost.getHostAddress());
			while(true) 
			{
				Socket client=serverSocket.accept();
				clientHandler = new ClientHandler(client, loadData, gameManager, gamePlayers, this);
				clients.add(clientHandler);
				new Thread(clientHandler).start();	
			}
			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//goes to client handler with playerID 1 - whatever incrementally
	
	public void findClient() throws IOException {
		while(currPlayer != clients.size()) {
			System.out.println(currPlayer);
			System.out.println(clients.size());
			for(int i = 0; i < clients.size(); i++) {
				ClientHandler currentClientHandler = clients.get(i);
				if(currentClientHandler.getClientHandlerID() == currPlayer) {
					currentClientHandler.requestAction();
					waitForResponse(currentClientHandler);
				}
			}
		}
		if(currPlayer == clients.size() - 1) {
			for(int i = 0; i < clients.size(); i++) {
				ClientHandler currentClientHandler = clients.get(i);
					if(currentClientHandler.getClientHandlerID() == 0) {
						currentClientHandler.requestDealerEnd();
					}
			}
		}
	}
	
	public void waitForResponse(ClientHandler currClient) {
		boolean end = false;
		while(!end) {
			System.out.print("");
			if(currClient.getStandState() == true) {
				currPlayer++;
				end = true;
			}
		}
	}
	
	//Dealer starts round -> players make a bet -> only until all clienthandlers
	//betplaced = true then go to requesting action from player
	
	public void checkBetPlaced() throws IOException{
		boolean allBetsPlaced = false;
		
		while(allBetsPlaced == false) {
			System.out.println("still looping");
			for(int i = 0; i < clients.size(); i++) {
				ClientHandler clientHandler = clients.get(i);
				if(clientHandler.getClientHandlerID() == currPlayerBet) {
					waitForBet(clientHandler);
				}
				
				if(currPlayerBet == clients.size()) {
					allBetsPlaced = true;
					break;
				}
			}
			
		}
		
	}
	
	
	public void waitForBet(ClientHandler clientHandler) {
		boolean end = false;
		while(!end) {
			System.out.print("");
			if(clientHandler.getBetPlaced() == true) {
				currPlayerBet++;
				end = true;
			}
		}
	}
	
}

