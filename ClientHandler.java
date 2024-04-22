package blackjack;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.io.EOFException;

public class ClientHandler implements Runnable{
	private final Socket clientSocket;
	Response response;
	LoadUserData userDataFile;
	List<UserData> userData;
	private int playerID;
	GameManager gameManager;
	OutputStream outputStream;
	ObjectInputStream objectInputStream;
	ObjectOutputStream objectOutputStream;
	GamePlayers gamePlayers;
	Server server;
	private boolean stand = false;
	private boolean betPlaced = false;
	
	//added 
	GameLogic gameLogic;
	Card card;
	
	public ClientHandler(Socket socket, LoadUserData userDataFile, GameManager gameManager, GamePlayers gamePlayers, Server server,GameLogic gameLogic){
			clientSocket=socket;
			this.userDataFile = userDataFile;
			this.gameManager = gameManager;
			this.gamePlayers = gamePlayers;
			this.gameLogic=gameLogic;
			this.server = server;
		}
	
	public void run() {
		try {
			outputStream = clientSocket.getOutputStream();
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			objectOutputStream = new ObjectOutputStream(outputStream);
			
			userData = userDataFile.getUserList();
			
			//Waiting for message type login
			while(true) {
				ClientMessage fromClient = (ClientMessage) objectInputStream.readObject();
				List<PlayerData> allGamePlayers = gamePlayers.getGamePlayers();
				
				if(fromClient.getType() == MessageType.LOGIN) {
					boolean match = false;
					//Some validation
					for(int i = 0; i < userData.size(); i++) {
						UserData user = userData.get(i);
						if((fromClient.getUsername().compareTo(user.getUsername())== 0) && (fromClient.getPassword().compareTo(user.getPassword()) == 0)){
							
							if(user.getIsDealer() == true) {
								//Setting response values
								response = new Response();
								response.setPlayerID(0);
								response.setDealer(true);
								response.setType(ResponseType.LOGIN_SUCCESS);
								playerID = 0;
								//Adding dealer to the game
								PlayerData dealer = new PlayerData(user.getUsername(), 0);
								gamePlayers.addPlayer(dealer);
								
								//writing response to client
								objectOutputStream.writeObject(response);
							}else {
								response = new Response();
								playerID = gameManager.getPlayerID();
								response.setPlayerID(playerID);
								response.setType(ResponseType.LOGIN_SUCCESS);
								response.setWinAmount(user.getWinAmount());
								response.setLossAmount(user.getLossAmount());
								response.setBankRoll(user.getBankroll());
								response.setUsername(user.getUsername());
								
								PlayerData player = new PlayerData(user.getUsername(), playerID);
								gamePlayers.addPlayer(player);
								objectOutputStream.writeObject(response);
							}
							match = true;
							break;
						}
					}
					
					if(!match) {
						response = new Response();
						response.setType(ResponseType.LOGIN_FAIL);
						objectOutputStream.writeObject(response);
					}
				}else if(fromClient.getType() == MessageType.BET) {
					//go to game player data and add bet to player in game
					//add a bet for that player id
					for(int i = 0; i < gamePlayers.getNumPlayers(); i++) {
						PlayerData gamePlayer = allGamePlayers.get(i);
						System.out.println(gamePlayer.getUserName());
						System.out.println(gamePlayer.getPlayerID());
					}
					for(int i = 0; i < gamePlayers.getNumPlayers(); i++) {
						PlayerData gamePlayer = allGamePlayers.get(i);
						if(fromClient.getPlayerID() == gamePlayer.getPlayerID()) {
							gamePlayer.setBetAmount(fromClient.getBetAmount());
							betPlaced = true;
							break;
						}
					}
					System.out.println("Player wants to make a bet");
					
				}else if(fromClient.getType() == MessageType.HIT) {
					
					for(int i = 0; i < gamePlayers.getNumPlayers(); i++) {
						PlayerData gamePlayer = allGamePlayers.get(i);
						if(fromClient.getPlayerID() == gamePlayer.getPlayerID()) {
							
							gameLogic.setGamePlayer(gamePlayers,gamePlayer.getPlayerID());
							fromClient.setMessageType(gameLogic.game());
							//gameLogic.drawCard();
							//gameLogic.checkHandValue();
	
						}
					
					}
				
				}else if(fromClient.getType() == MessageType.STAND) {
					for(int i = 0; i < gamePlayers.getNumPlayers(); i++) {
						PlayerData gamePlayer = allGamePlayers.get(i);
						if(fromClient.getPlayerID() == gamePlayer.getPlayerID()) {
							gamePlayer.setStand();
							stand = true;
							break;
						}
					}
					System.out.println("player wants to stand");
				}else if(fromClient.getType() == MessageType.DOUBLE_DOWN) {
					System.out.println("Player wants to double down");
				}else if(fromClient.getType() == MessageType.START_ROUND) {
					server.checkBetPlaced();
					server.findClient();
					System.out.println("Dealer wants to start game");
				}else if(fromClient.getType() == MessageType.END_ROUND) {
					System.out.println("Dealer wants to end game");
				}
				
				
				
				else if(fromClient.getType() == MessageType.DEBUG) {
					for(int i = 0; i < gamePlayers.getNumPlayers(); i++) {
						PlayerData gamePlayer = allGamePlayers.get(i);
						System.out.println(gamePlayer.getBetAmount());
					}
				}
			}
			
			
		
		

	}
		catch(EOFException e){
			//System.out.println("when is it closeing this");	
			//e.printStackTrace();
		}
		catch (IOException e) {
				e.printStackTrace();
			} 
		catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
	}
		
	public int getClientHandlerID() {
		return playerID;
	}
	
	public void requestAction() throws IOException {
		response = new Response();
		response.setType(ResponseType.PLAYER_TURN);
		objectOutputStream.writeObject(response);
	}
	
	public void requestDealerEnd() throws IOException{
		response = new Response();
		response.setType(ResponseType.ROUND_DONE);
		objectOutputStream.writeObject(response);
	}
	
	public boolean getStandState() {
		return stand;
	}
	
	public boolean getBetPlaced() {
		return betPlaced;
	}
	
	
}
		
	

	
