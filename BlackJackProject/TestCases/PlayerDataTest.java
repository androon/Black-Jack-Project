package TestCases;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ClassSource.Card;
import ClassSource.Deck;
import ClassSource.PlayerData;

class PlayerDataTest {
	PlayerData newPlayer;
	
	@BeforeEach
	public void setUp() {
		String username = "test";
		int playerID = 1;
		int bankRoll = 10;
		boolean isDealer = false;
		int numWin = 42;
		int numLoss = 49;
		newPlayer = new PlayerData(username,playerID,bankRoll,isDealer,numWin,numLoss);
	}

	@Test
	public void testPlayerDataConstructor() {
		String username = "test";
		int playerID = 1;
		int bankRoll = 10;
		boolean isDealer = false;
		int numWin = 42;
		int numLoss = 49;
		PlayerData newPlayer = new PlayerData(username,playerID,bankRoll,isDealer,numWin,numLoss);
		
		assertEquals(username, newPlayer.getUserName());
		assertEquals(playerID, newPlayer.getPlayerID());
		assertEquals(bankRoll, newPlayer.getBankRoll());
		assertFalse(newPlayer.getIsDealer());
		assertEquals(numWin, newPlayer.getWinAmount());
		assertEquals(numLoss, newPlayer.getLossAmount());
		assertFalse(newPlayer.getStand());
		assertEquals(0, newPlayer.getHandValue());
		assertEquals(0, newPlayer.getBetAmount());
	}
	
	@Test
	public void testSetPlayerID() {
		newPlayer.setPlayerID(2);
		assertEquals(2, newPlayer.getPlayerID());
	}
	
	@Test
	public void testSetHandValue() {
		newPlayer.setHandValue(19);
		assertEquals(19, newPlayer.getHandValue());
	}
	
	@Test
	public void testSetBetAmount() {
		newPlayer.setBetAmount(10);
		assertEquals(10, newPlayer.getBetAmount());
	}
	
	@Test
	public void testSetStand() {
		newPlayer.setStand();
		assertTrue(newPlayer.getStand());
	}
	
	@Test
	public void testSetPlayerBust() {
		newPlayer.setPlayerBust();
		assertTrue(newPlayer.getBust());
	}
	
	@Test
	public void testAddCardToHand() {
		Deck deck = new Deck();
		deck.shuffle();
		Card card = deck.drawCard();
		newPlayer.addCardToHand(card);
		
		
		List<Card> testHand = newPlayer.getCardsInHand();
		
		assertNotNull(testHand);
		assertEquals(1, testHand.size());
	}
	
	@Test
	public void testSetHandWithAce() {
		newPlayer.setHandWithAce(21);
		assertEquals(21, newPlayer.getHandWithAce());
	}
	
	@Test
	public void testSetWinAmount() {
		newPlayer.setWinAmount(24);
		assertEquals(24, newPlayer.getWinAmount());
	}
	
	@Test
	public void testSetLossAmount() {
		newPlayer.setLossAmount(29);
		assertEquals(29, newPlayer.getLossAmount());
	}
}
