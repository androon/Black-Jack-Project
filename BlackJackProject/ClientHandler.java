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
	private boolean initialDraw = true;
	
	private int handVal = 0;
	
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
				System.out.println("STILL IMPLEMENTING");
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
				
				if(user.getIsDealer() == true) {
					//Setting response values
					response = new Response();
					response.setPlayerID(0);
					response.setDealer(true);
					response.setType(ResponseType.LOGIN_SUCCESS);
					playerID = 0;
					//Adding dealer to the game
					PlayerData dealer = new PlayerData(user.getUsername(), 0, 0, true);
					
					gamePlayers.addPlayer(dealer);
					
					//writing response to client
					objectOutputStream.writeObject(response);
					
					response = new Response();
					response.setType(ResponseType.REQUEST_START_ROUND);
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
					
					PlayerData player = new PlayerData(user.getUsername(), playerID, user.getBankroll(), false);
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
				gamePlayer.setBankRoll(gamePlayer.getBankRoll() - fromClient.getBetAmount());
				gamePlayer.setBetAmount(fromClient.getBetAmount() + gamePlayer.getBetAmount());
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
	
	public void requestStartRound() throws IOException {
		response = new Response();
		response.setType(ResponseType.REQUEST_START_ROUND);
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
			List<Card> hand = gamePlayer.getCardsInHand();
			Card card1 = hand.get(0);
			Card card2 = hand.get(1);
			System.out.println("First Card: " + card1.getValue());
			System.out.println("Second Card: " + card2.getValue());
			
			System.out.println("player" + "ID: " + gamePlayer.getPlayerID());
			System.out.println("player" + gamePlayer.getPlayerID() + " Bet: " + gamePlayer.getBetAmount());
			System.out.println("player" + gamePlayer.getPlayerID() + " Hand: " + gamePlayer.getHandValue());
			
			
			if(gamePlayer.getHandWithAce() != 0) {
				System.out.println(gamePlayer.getPlayerID() + " Hand with ace: " + gamePlayer.getHandWithAce());
			}
			
			
			
			System.out.println("player" + gamePlayer.getPlayerID() + " Stand: " + gamePlayer.getStand());
			System.out.println("player" + gamePlayer.getPlayerID() + " Bust: " + gamePlayer.getBust());
			response = new Response();
		}
	}
	
	public void dealerAutoLose() {
		
	}
	
	public void reset() {
		if(isDealer == true) {
			handVal = 0;
		}else {
			stand = false;
			betPlaced = false;
			turnEnd = false;
			handVal = 0;
			initialDraw = true;
		}
	}
	
	
	public void sendGameStateToClient(List<PlayerData> allGamePlayers, int numPlayers) throws IOException {
	    System.out.println("SENDING GAMESTATE");
	    response = new Response();
	    response.setType(ResponseType.UPDATE);

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
	    				response.setInitialDraw(initialDraw);
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
		    			System.out.println("Plaers bet amount" + playerData.getBetAmount());
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
	
	public void setInitialDraw(boolean initialDraw) {
		this.initialDraw = initialDraw;
	}
	
	public void setStand(ClientMessage fromClient, List<PlayerData> allGamePlayers) throws IOException {
		System.out.println("player wants to stand");
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
			
			//Debug statements
			System.out.println("Dealers hand val: " + dealerData.getHandValue());
			
			//assign handVal to dealers current hand to determine action
			handVal = dealerData.getHandValue();
			
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
			
			//Get handValue after card is added
			handVal = playerData.getHandValue();
			System.out.println("Hand after hit" + playerData.getHandValue());
			//Check if player has an ace
			if(playerData.getHandWithAce() != 0) {
				System.out.println("Regular Hand: " + handVal);
				System.out.println("Ace Hand: " + playerData.getHandWithAce());
			}
			
			
			if(handVal <= 21) {
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
	}
	
	public void startRound(List<PlayerData> allGamePlayers) throws IOException{
		isDealer = true;
		server.sendBetRequestToAllPlayers();
		gameLogic.initialDeal(allGamePlayers);
		server.checkBetPlaced();
		debug(allGamePlayers);
		
		//Sending gamestate to all players
		server.sendGameState();
		
		server.findClient();
		
		
		boolean allPlayersDone = false;
		while(!allPlayersDone) {
			allPlayersDone = server.checkAllPlayersDone();
		}
		if(allPlayersDone) {
			server.setAllClientsInitialDraw();
			server.sendGameState();
			debug(allGamePlayers);
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
		}
	}
	
	public void endRound(GameLogic gameLogic, List<PlayerData> allGamePlayers) throws IOException {
		gameLogic.checkOutcome(allGamePlayers);
		server.sendGameState();
		server.resetGame();
		gameLogic.reset();
	}
	
}
		
	

	

