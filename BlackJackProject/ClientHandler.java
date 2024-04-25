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
	GameLogic gameLogic;
	
	private boolean stand = false;
	private boolean betPlaced = false;
	private boolean turnEnd = false;
	private boolean isDealer = false;
	
	private int handAfterHit = 0;
	
	public ClientHandler(Socket socket, LoadUserData userDataFile, GameManager gameManager, GamePlayers gamePlayers, Server server){
			clientSocket=socket;
			this.userDataFile = userDataFile;
			this.gameManager = gameManager;
			this.gamePlayers = gamePlayers;
			this.server = server;
		}
	
	public void run() {
		try {
			outputStream = clientSocket.getOutputStream();
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			objectOutputStream = new ObjectOutputStream(outputStream);
			gameLogic = gameManager.getGameLogic();
			userData = userDataFile.getUserList();
			
			//Waiting for message type login
			while(true) {
				ClientMessage fromClient = (ClientMessage) objectInputStream.readObject();
				List<PlayerData> allGamePlayers = gamePlayers.getGamePlayers();
				
				if(fromClient.getType() == MessageType.LOGIN) {
					checkLogin(fromClient);
				}else if(fromClient.getType() == MessageType.BET) {
					//go to game player data and add bet to player in game
					//add a bet for that player id
					addPlayerBet(fromClient, allGamePlayers);
				}else if(fromClient.getType() == MessageType.HIT) {
					GameLogic gameLogic = gameManager.getGameLogic();
					handAfterHit = gameLogic.addCardToPlayer(fromClient, allGamePlayers);
					System.out.println("Hand after hit: " + handAfterHit);
					if(isDealer == true) {
						response = new Response();
						if(handAfterHit < 17) {
							response.setType(ResponseType.REQUEST_DEALER_HIT);
						}else if(handAfterHit >= 17 && handAfterHit <= 21) {
							response.setType(ResponseType.REQUEST_END_ROUND);
						}else if(handAfterHit > 21) {
							dealerAutoLose();
							response.setType(ResponseType.REQUEST_END_ROUND);
						}
						objectOutputStream.writeObject(response);
					}else {
						if(handAfterHit <= 21) {
							response = new Response();
							response.setType(ResponseType.PLAYER_TURN);
							objectOutputStream.writeObject(response);
						}else {
							stand = true;
							turnEnd = true;
							for(int i = 0; i < gamePlayers.getNumPlayers(); i++) {
								PlayerData currPlayer = allGamePlayers.get(i);
								if(currPlayer.getPlayerID() == playerID) {
									currPlayer.setStand();
									currPlayer.setPlayerBust();
								}
							}
							response = new Response();
							response.setType(ResponseType.PLAYER_TURN_END);
							objectOutputStream.writeObject(response);
						}
					}
					
					
					System.out.println("player wants to hit");
				}else if(fromClient.getType() == MessageType.STAND) {
					for(int i = 0; i < gamePlayers.getNumPlayers(); i++) {
						PlayerData gamePlayer = allGamePlayers.get(i);
						if(fromClient.getPlayerID() == gamePlayer.getPlayerID()) {
							gamePlayer.setStand();
							stand = true;
							turnEnd = true;
							break;
						}
					}
					System.out.println("player wants to stand");
				}else if(fromClient.getType() == MessageType.DOUBLE_DOWN) {
					System.out.println("Player wants to double down");
				}else if(fromClient.getType() == MessageType.START_ROUND) {
					isDealer = true;
					server.sendBetRequestToAllPlayers();
					gameLogic.initialDeal(allGamePlayers);
					server.checkBetPlaced();
					server.findClient();
					boolean allPlayersDone = false;
					while(!allPlayersDone) {
						allPlayersDone = server.checkAllPlayersDone();
					}
					if(allPlayersDone) {
						debug(allGamePlayers);
						for(int i = 0; i < allGamePlayers.size(); i++) {
							PlayerData dealerCheck = allGamePlayers.get(i);
							if(dealerCheck.getPlayerID() == 0) {
								if(dealerCheck.getHandValue() < 17) {
									response = new Response();
									response.setType(ResponseType.REQUEST_DEALER_HIT);
									objectOutputStream.writeObject(response);
								}else if(dealerCheck.getHandValue() >= 17 && dealerCheck.getHandValue() <= 21) {
									response = new Response();
									response.setType(ResponseType.REQUEST_END_ROUND);
									objectOutputStream.writeObject(response);
								}
							}
						}
						response = new Response();
						response.setType(ResponseType.ALL_PLAYERS_DONE);
						objectOutputStream.writeObject(response);
					}
					System.out.println("Dealer wants to start game");
				}else if(fromClient.getType() == MessageType.END_ROUND) {
					System.out.println("Dealer wants to end game");
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
	
	//Method for validating login
	public void checkLogin(ClientMessage fromClient) throws IOException {
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
		
	}
	
	//Method for adding player bet to the player
	public void addPlayerBet(ClientMessage fromClient, List<PlayerData> allGamePlayers) {
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
	}
		
	public int getClientHandlerID() {
		return playerID;
	}
	
	public void requestBet() throws IOException{
		response = new Response();
		response.setType(ResponseType.ALL_BETS);
		objectOutputStream.writeObject(response);
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
	
	public boolean getTurnState() {
		return turnEnd;
	}
	
	public void debug(List<PlayerData> allGamePlayers) {
		for(int i = 0; i < allGamePlayers.size(); i++) {
			System.out.println("DEBUG SETTING");
			PlayerData gamePlayer = allGamePlayers.get(i);
			System.out.println("player" + "ID: " + gamePlayer.getPlayerID());
			System.out.println("player" + i + "Bet: " + gamePlayer.getBetAmount());
			System.out.println("player" + i + "Hand: " + gamePlayer.getHandValue());
			System.out.println("player" + i + "Stand: " + gamePlayer.getStand());
			System.out.println("player" + i + "Bust: " + gamePlayer.getBust());
			response = new Response();
		}
	}
	
	public void dealerAutoLose() {
		
	}
}
		
	

	

