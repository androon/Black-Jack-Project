package TestCases;

import org.junit.Test;

import ClassSource.Deck;
import ClassSource.GameLogic;
import ClassSource.GameManager;

import static org.junit.Assert.*;

public class GameManagerTest {

    private GameManager gameManager;


    @Test
    public void testGameManagerConstructor() {
    	gameManager = new GameManager();
        assertNotNull(gameManager);
        assertNotNull(gameManager.getGameLogic());
    }

    @Test
    public void testGetPlayerID() {
    	gameManager = new GameManager();
        int playerID1 = gameManager.getPlayerID();
        int playerID2 = gameManager.getPlayerID();

        assertTrue(playerID2 > playerID1);
    }

    @Test
    public void testGetDeck() {
    	gameManager = new GameManager();
        Deck deck = gameManager.getDeck();
        
        assertNotNull(deck);
        assertTrue(deck.getRemainingCards() > 0);
    }

    @Test
    public void testGetGameLogic() {
    	gameManager = new GameManager();
        GameLogic gameLogic = gameManager.getGameLogic();

        assertNotNull(gameLogic);
    }

    @Test
    public void testResetDeck() {
    	gameManager = new GameManager();
        Deck initialDeck = gameManager.getDeck();

        gameManager.resetDeck();
        Deck resetDeck = gameManager.getDeck();

        assertNotEquals(initialDeck, resetDeck);
        assertTrue(resetDeck.getRemainingCards() > 0);
    }
}