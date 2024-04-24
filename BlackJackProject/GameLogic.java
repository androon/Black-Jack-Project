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

	

	

	public void drawCard(int a) 

	{

		handValue=0;

		playerList=getPlayerData();

		card=deck.drawCard();

		handValue=handValue+playerList.get(a).getHandValue();

		handValue=handValue+card.getValue();

		if(handValue<21){playerList.get(a).setHandValue(handValue);}

		else if(handValue==21){System.out.println("Player Wins");}

		else  {System.out.println("Bust! Dealer Wins");}

	}

	



	public void checkHandValue(int a)

	{

		playerList=gamePlayer.getGamePlayers();

		System.out.println("Hand value:"+playerList.get(a).getHandValue()+"for Player ID:"+playerList.get(a).getUserName());

	}

		



		

}
