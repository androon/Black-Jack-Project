import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameManagerTest {

    private GameManager gameManager;

    @Before
    public void setUp() {
        gameManager = new GameManager();
    }

    @Test
    public void testGameManagerConstructor() {
        assertNotNull(gameManager);
        assertNotNull(gameManager.getGameLogic());
    }

    @Test
    public void testGetPlayerID() {
        int playerID1 = gameManager.getPlayerID();
        int playerID2 = gameManager.getPlayerID();

        assertTrue(playerID2 > playerID1);
    }

    @Test
    public void testGetDeck() {
        Deck deck = gameManager.getDeck();
        
        assertNotNull(deck);
        assertTrue(deck.getRemainingCards() > 0);
    }

    @Test
    public void testGetGameLogic() {
        GameLogic gameLogic = gameManager.getGameLogic();

        assertNotNull(gameLogic);
    }

    @Test
    public void testResetDeck() {
        Deck initialDeck = gameManager.getDeck();

        gameManager.resetDeck();
        Deck resetDeck = gameManager.getDeck();

        assertNotEquals(initialDeck, resetDeck);
        assertTrue(resetDeck.getRemainingCards() > 0);
    }
}