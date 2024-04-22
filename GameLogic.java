package blackjack;

import java.util.Scanner;
import java.util.List;

public class GameLogic {

	int id;
	Deck deck;
	GamePlayers gamePlayer;
	List<PlayerData> playerList;
	Card card; 
	Response response;
	int handValue;
	MessageType type=null;
	
	
	public GameLogic()
	{
		deck=new Deck(2);
		deck.shuffle();
	}
	
	
	public void setGamePlayer(GamePlayers gameplayer,int playerID) {
		this.gamePlayer=gameplayer;
		id=playerID;
	}
	
	public List<PlayerData> getPlayerData()
	{
		playerList=gamePlayer.getGamePlayers();
		return playerList;
	}
	
	
	public MessageType game() {
		
		Scanner scan= new Scanner(System.in);
		int choice= scan.nextInt();	
			
		while(choice!=0)
			
		{
				switch(choice) {
				case 1: //drawCard
					drawCard();
					break;
			
				case 2://checkHandValue
					checkHandValue();
					break;
			
				case 3://
					type=MessageType.STAND;		
				}
		}
	
		
	return type;
			
	}
	
	
	public void drawCard() 
	{
	
		playerList=getPlayerData();
		for(int i=0;i<playerList.size();i++) {
		
			if(playerList.get(i).getPlayerID()==id){
			
				card=deck.drawCard();
				handValue=handValue+card.getValue();
				setHandValue(id,handValue);
				System.out.println("what is the hand value:"+getHandValue());
			}
			
		}
	}
		
	
	public void checkHandValue()
	{
		handValue=getHandValue();
		System.out.println("Has to be the same as above"+ handValue);
		
		if(handValue>21)
		{
			System.out.println("Bust");
		}
		else
		{
			System.out.println("Send a message of hit or stand");
		}
	}
		
	
	public void setHandValue(int i,int value)
		{
			playerList.get(i).setHandValue(value);
		}
	
	public int getHandValue()
		{
			return playerList.getLast().getHandValue();
		}
		
}
	
	
	

/*
handValue=0;

for(int i=0;i<2;i++) {
	
	card=deck.drawCard();
	System.out.println("Value of the drawn card is:"+card.getValue());
	handValue=handValue+card.getValue();
	//System.out.println(deck.drawCard());
}

playerList=gamePlayer.getGamePlayers();

for(int i=0;i<playerList.size();i++) {
	System.out.println(playerList.get(i).getUserName());
}
//System.out.println(playerList.getLast().getBetAmount());
//playerList.getLast().setHandValue(handValue);
//System.out.println("Value of the Players Hand:"+playerList.getLast().getHandValue());


//response=new Response();
//	response.setWinAmount(value);
//	response.setType(ResponseType.PLACE_BET_SUCCESS);

//	outputStream.writeObject(response);
return handValue;*/


