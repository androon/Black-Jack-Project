//package blackjack;

/*
 * clientHandler processes client requests
 * implements an interface called runnable from Java
 * opens a communication channel by implementing objectInputStream and objectOutputStream for communication between clients and server
 * messages are received from clients are separated between players and dealers
 * each thread receives messages for login, and the actions set in the Client class
 * each message is processed accordingly to the type of message received and by the type of the client
 * after the processing the message, the thread pushes back a response by the server to the client 
 * a separate method,checkLogin(ClientMessage) validates user login
 * addPlayerBet() for the betting system
 * and the sendGameStatetoClient() method to update the clients attributes during the game of blackJack
 * */

package ClassSource;

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
	private String username;
	GameManager gameManager;
	OutputStream outputStream;
	ObjectInputStream objectInputStream;
	ObjectOutputStream objectOutputStream;
	GamePlayers gamePlayers;
	Server server;
	GameLogic gameLogic;
	private volatile boolean listenToClient = true;
	
	
	private boolean stand = false;
	private boolean betPlaced = false;
	private boolean turnEnd = false;
	private boolean isDealer = false;
	private boolean initialDraw = true;
	
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
			
			while(listenToClient) {
				ClientMessage fromClient = (ClientMessage) objectInputStream.readObject();
				processClientMessage(fromClient);
				}
		}catch(EOFException e){
			e.printStackTrace();
		}catch (IOException e) {
				e.printStackTrace();
		}catch (ClassNotFoundException e) {
				e.printStackTrace();
		}
	}
	
	public void stopListening() {
		listenToClient = false;
		if(Thread.currentThread().isInterrupted()) {
			Thread.currentThread().interrupt();
		}
	}
	
	public void processClientMessage(ClientMessage fromClient) throws IOException {
		List<PlayerData> allGamePlayers = gamePlayers.getGamePlayers();
		switch (fromClient.getType()) {
			case LOGIN:
				checkLogin(fromClient);
				break;
			
			case BET:
				addPlayerBet(fromClient,allGamePlayers);
				break;
			
			case HIT:
				gameLogic = gameManager.getGameLogic();
				checkHit(gameLogic,allGamePlayers, fromClient);
				break;
			
			case STAND:
				setStand(fromClient,allGamePlayers);
				break;
			
			case DOUBLE_DOWN:
				gameLogic = gameManager.getGameLogic();
				checkHit(gameLogic, allGamePlayers, fromClient);
				setStand(fromClient,allGamePlayers);
				addPlayerBet(fromClient,allGamePlayers);
				break;
				
			case START_ROUND:
				startRound(allGamePlayers);
				break;
				
			case END_ROUND:
				gameLogic = gameManager.getGameLogic();
				endRound(gameLogic, allGamePlayers);
				break;
				
			case DEPOSIT:
				depositFunds(fromClient, allGamePlayers);
				break;
				
			case LOGOUT:
				logout(fromClient);
				break;
			default:
				System.out.println("UNKNOWNS RESPONSE: " + fromClient.getType());
				break;
		
		}
	}

	
	//Method for validating login
	public void checkLogin(ClientMessage fromClient) throws IOException {
		boolean match = false;
		//Some validation
		for(int i = 0; i < userData.size(); i++) {
			UserData user = userData.get(i);
			if((fromClient.getUsername().compareTo(user.getUsername())== 0) && (fromClient.getPassword().compareTo(user.getPassword()) == 0)){
				List<PlayerData> allGamePlayers = gamePlayers.getGamePlayers();
				for(int j = 0; j < allGamePlayers.size(); j++) {
					PlayerData checkInGame = allGamePlayers.get(j);
					if(checkInGame.getUserName().equals(fromClient.getUsername())) {
						response = new Response();
						response.setType(ResponseType.LOGIN_FAIL);
						objectOutputStream.writeObject(response);
						return;
					}
				}
				if(user.getIsDealer() == true) {
					//Setting response values
					response = new Response();
					response.setPlayerID(0);
					response.setDealer(true);
					response.setType(ResponseType.LOGIN_SUCCESS);
					playerID = 0;
					username = fromClient.getUsername();
					//Adding dealer to the game
					PlayerData dealer = new PlayerData(user.getUsername(), 0, 0, true, 0, 0);
					
					gamePlayers.addPlayer(dealer);
					
					//writing response to client
					objectOutputStream.writeObject(response);
					
					response = new Response();
					response.setType(ResponseType.REQUEST_START_ROUND);
					objectOutputStream.writeObject(response);
				}else {
					response = new Response();
					playerID = gameManager.getPlayerID();
					username = fromClient.getUsername();
					response.setPlayerID(playerID);
					response.setType(ResponseType.LOGIN_SUCCESS);
					response.setWinAmount(user.getWinAmount());
					response.setLossAmount(user.getLossAmount());
					response.setBankRoll(user.getBankroll());
					response.setUsername(user.getUsername());
					
					PlayerData player = new PlayerData(user.getUsername(), playerID, user.getBankroll(), false, user.getWinAmount(), user.getLossAmount());
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
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData gamePlayer = allGamePlayers.get(i);
			if(fromClient.getPlayerID() == gamePlayer.getPlayerID()) {
				gamePlayer.setBankRoll(gamePlayer.getBankRoll() - fromClient.getBetAmount());
				gamePlayer.setBetAmount(fromClient.getBetAmount() + gamePlayer.getBetAmount());
				betPlaced = true;
				break;
			}
		}
	}
		
	
	
	public void requestBet() throws IOException{
		response = new Response();
		response.setType(ResponseType.ALL_BETS);
		objectOutputStream.writeObject(response);
	}
	public void sendGameStateToClient(List<PlayerData> allGamePlayers, int numPlayers) throws IOException {
	    response = new Response();
	    response.setType(ResponseType.UPDATE);
	    
	    updateRecord(allGamePlayers);
	    boolean allPlayersUpdated = false;
	    int count = 0;
	    
	    
	    while(!allPlayersUpdated) {
	    	for(int i = 0; i < allGamePlayers.size();i++) {
	    		PlayerData playerData = allGamePlayers.get(i);
	    		if(playerData.getPlayerID() == count) {
	    			//If clientHandler is handling dealer send full dealer information
	    			if(playerData.getPlayerID() == 0 && playerID == 0) {
	    				response = new Response();
	    				response.setType(ResponseType.UPDATE);
	    				response.setInitialDraw(true);
	    				response.setPlayerID(playerData.getPlayerID());
	    				response.setHandValue(playerData.getHandValue());
	    				if(playerData.getHandWithAce() != 0) {
	    					response.setHandWithAce(playerData.getHandWithAce());
	    				}
	    				response.setCardHandString(playerData.toStringCards());
	    			}else if(initialDraw == true && playerData.getPlayerID() == 0) {
	    				response = new Response();
		    			response.setType(ResponseType.UPDATE);
	    				response.setPlayerID(0);
	    				response.setInitialDraw(this.initialDraw);
	    				//isolating one card to reveal
	    				List<Card> hand = playerData.getCardsInHand();
	    				Card revealedCard = hand.get(1);
	    				response.setHandValue(revealedCard.getValue());
	    				
	    				String cardReveal = String.valueOf(revealedCard.getValue());
	    				
	    				response.setCardHandString(cardReveal + ", Hidden");
	    				response.setHandWithAce(playerData.getHandWithAce());
	    				
	    			}else {
	    				response = new Response();
		    			response.setType(ResponseType.UPDATE);
	    				if(playerData.getHandWithAce() != 0) {
	    					response.setHandWithAce(playerData.getHandWithAce());
	    				}
	    				response.setInitialDraw(this.initialDraw);
		    			response.setPlayerID(playerData.getPlayerID());
		    			response.setHandValue(playerData.getHandValue());
		    			response.setCardHandString(playerData.toStringCards());
		    			response.setBetAmount(playerData.getBetAmount());
		    			response.setBankRoll(playerData.getBankRoll());
	    			}
	    			objectOutputStream.writeObject(response);
	    			objectOutputStream.flush();
	    			count++;
	    		}
	    	}
	    	
	    	if(count == allGamePlayers.size()) {
	    		allPlayersUpdated = true;
	    	}
	    }
	    
	}
	
	public void setStand(ClientMessage fromClient, List<PlayerData> allGamePlayers) throws IOException {
		for(int i = 0; i < gamePlayers.getNumPlayers(); i++) {
			PlayerData gamePlayer = allGamePlayers.get(i);
			if(fromClient.getPlayerID() == gamePlayer.getPlayerID()) {
				gamePlayer.setStand();
				//If player stands with ace in their hand, replace their hand value with value with ace
				if(gamePlayer.getHandWithAce() != 0) {
					gamePlayer.setHandValue(gamePlayer.getHandWithAce());
				}
				stand = true;
				turnEnd = true;
				break;
			}
		}
		
		response = new Response();
		response.setType(ResponseType.PLAYER_TURN_END);
		objectOutputStream.writeObject(response);
	}
	
	public void checkHit(GameLogic gameLogic, List<PlayerData> allGamePlayers, ClientMessage fromClient) throws IOException {

		PlayerData dealerData = null;
		gameLogic.addCardToPlayer(fromClient, allGamePlayers);

		server.sendGameState();
		
		//Logic for dealer after hit
		if(isDealer == true) {
			//Find dealer in the list
			for(int i = 0; i < allGamePlayers.size(); i++) {
				PlayerData dealerCheck = allGamePlayers.get(i);
				if(dealerCheck.getPlayerID() == playerID) {
					dealerData = dealerCheck;
				}
			}
			//If dealer has a hand with ace check if its over 17
			if(dealerData.getHandWithAce() != 0) {
				if(dealerData.getHandWithAce() >= 17 && dealerData.getHandWithAce() <= 21) {
					dealerData.setHandValue(dealerData.getHandWithAce());
					dealerData.setHandWithAce(0);
				}
			}
			response = new Response();
			if(dealerData.getHandValue() < 17) {
				response.setType(ResponseType.REQUEST_DEALER_HIT);
				objectOutputStream.writeObject(response);
			}else if(dealerData.getHandValue() >= 17 && dealerData.getHandValue() <= 21) {
				response.setType(ResponseType.REQUEST_END_ROUND);
				objectOutputStream.writeObject(response);
			}else if(dealerData.getHandValue() > 21) {
				response.setType(ResponseType.REQUEST_END_ROUND);
				objectOutputStream.writeObject(response);
			}
		}
		
		
		//Logic for player after hit
		else {
			PlayerData playerData = null;
			//Find player in list
			for(int i = 0; i < allGamePlayers.size(); i++) {
				PlayerData playerCheck = allGamePlayers.get(i);
				if(playerCheck.getPlayerID() == playerID) {
					playerData = playerCheck;
				}
			}
			if(playerData.getHandValue() <= 21) {
				response = new Response();
				response.setType(ResponseType.PLAYER_TURN);
				objectOutputStream.writeObject(response);
			}else {
				stand = true;
				turnEnd = true;
				for(int i = 0; i < allGamePlayers.size(); i++) {
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
		
	}
	
	public void startRound(List<PlayerData> allGamePlayers) throws IOException{
		isDealer = true;
		server.sendBetRequestToAllPlayers();
		gameLogic.initialDeal(allGamePlayers);
		server.checkBetPlaced();
		
		//Sending gamestate to all players
		server.sendGameState();
		server.findClient();
		
		boolean allPlayersDone = false;
		while(!allPlayersDone) {
			allPlayersDone = server.checkAllPlayersDone();
		}
		if(allPlayersDone) {
			server.setAllClientsInitialDraw();
			for(int i = 0; i < allGamePlayers.size(); i++) {
				PlayerData dealerCheck = allGamePlayers.get(i);
				if(dealerCheck.getPlayerID() == 0) {
					
					//Check if dealer has a hand with ace
					if(dealerCheck.getHandWithAce() != 0) {
						if(dealerCheck.getHandWithAce() >= 17 && dealerCheck.getHandWithAce() <= 21) {
							dealerCheck.setHandValue(dealerCheck.getHandWithAce());
							response = new Response();
							response.setType(ResponseType.REQUEST_END_ROUND);
							objectOutputStream.writeObject(response);
						}else if(dealerCheck.getHandWithAce() < 17) {
							response = new Response();
							response.setType(ResponseType.REQUEST_DEALER_HIT);
							objectOutputStream.writeObject(response);
						}
					}else {
						//If no ace check dealers regular hand
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
			}
			server.sendGameState();
		}
	}
	
	public void endRound(GameLogic gameLogic, List<PlayerData> allGamePlayers) throws IOException {
		gameLogic.checkOutcome(allGamePlayers);
		server.sendGameState();
		server.resetGame();
		gameLogic.reset();
	}
	
	public void depositFunds(ClientMessage fromClient, List<PlayerData> allGamePlayers) throws IOException {
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData playerToDeposit = allGamePlayers.get(i);
			if(playerToDeposit.getPlayerID() == fromClient.getPlayerID()) {
				playerToDeposit.setBankRoll(playerToDeposit.getBankRoll() + fromClient.getDepAmount());
				response = new Response();
				response.setType(ResponseType.UPDATE_BANKROLL);
				response.setPlayerID(playerToDeposit.getPlayerID());
				response.setBankRoll(playerToDeposit.getBankRoll());
				objectOutputStream.writeObject(response);
				break;
			}
		}	
	}
	
	public void logout(ClientMessage fromClient) throws IOException {
		int playerIDRemove= fromClient.getPlayerID();
		List<PlayerData> allGamePlayers = gamePlayers.getGamePlayers();
		//Remove player from gamePlayers
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData playerToRemove = allGamePlayers.get(i);
			if(playerToRemove.getPlayerID() == playerIDRemove) {
				allGamePlayers.remove(i);
				break;
			}
		}
		
		//Decrementing all Id's > than id to remove
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData currPlayer = allGamePlayers.get(i);
			int currPlayerID = currPlayer.getPlayerID();
				
			if(currPlayerID > playerIDRemove) {
				currPlayer.setPlayerID(currPlayerID - 1);
			}
		}
		

		for(int i = 0; i < userData.size(); i++) {
			UserData playerToUpdate = userData.get(i);
			if(playerToUpdate.getUsername().equals(fromClient.getUsername())) {
				playerToUpdate.setBankroll(fromClient.getBankRoll());
				playerToUpdate.setWinAmount(fromClient.getWinAmount());
				playerToUpdate.setLossAmount(fromClient.getLossAmount());
			}
		}
		
		userDataFile.saveData();
		server.removeClient(username);
		server.updateAllID();
		gameManager.subtractID();
		stopListening();
		
		objectOutputStream.close();
		objectInputStream.close();
		clientSocket.close();
		
	}
	
	
	public void updatePlayerID(List<PlayerData> allGamePlayers) throws IOException {
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData currPlayer = allGamePlayers.get(i);
			if(currPlayer.getUserName().equals(username)) {
				this.playerID = currPlayer.getPlayerID();
				response = new Response();
				response.setType(ResponseType.UPDATE_PLAYERID);
				response.setPlayerID(playerID);
				objectOutputStream.writeObject(response);
			}
		}
	}
	
	public void updateRecord(List<PlayerData> allGamePlayers) throws IOException {
		for(int i = 0; i < allGamePlayers.size(); i++) {
			PlayerData currPlayer = allGamePlayers.get(i);
			if(currPlayer.getPlayerID() == playerID) {
				response = new Response();
				response.setPlayerID(currPlayer.getPlayerID());
				response.setType(ResponseType.UPDATE_RECORD);
				response.setWinAmount(currPlayer.getWinAmount());
				response.setLossAmount(currPlayer.getLossAmount());
				objectOutputStream.writeObject(response);
			}
		}
	}
	
	public String getClientUsername() {
		return username;
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
	
	public void requestStartRound() throws IOException {
		response = new Response();
		response.setType(ResponseType.REQUEST_START_ROUND);
		objectOutputStream.writeObject(response);
	}
	
	public int getClientHandlerID() {
		return playerID;
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
	
	public void reset() throws IOException {
		if(!isDealer) {
			stand = false;
			betPlaced = false;
			turnEnd = false;
			initialDraw = true;
		}
		
		//Send player the round has ended
		response = new Response();
		response.setType(ResponseType.ROUND_DONE);
		objectOutputStream.writeObject(response);
	}
	
	
	
	
	public void setInitialDraw(boolean initialDraw) {
		this.initialDraw = initialDraw;
	}
	
}
		
	

	

