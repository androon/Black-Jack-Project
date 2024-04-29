import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameLogicTest {

    private GameLogic gameLogic;

    @Before
    public void setUp() {
        gameLogic = new GameLogic();
    }

    @Test
    public void testGameLogicConstructor() {
        assertNotNull(gameLogic);
    }

    @Test
    public void testSetDeck() {
        Deck deck = new Deck(); 
        gameLogic.setDeck(deck);
        assertNotNull(deck); 
    }

    @Test
    public void testReset() {
        gameLogic.setDeck(new Deck());
        gameLogic.reset();

        assertEquals(0, gameLogic.playerCheck);
        assertEquals(0, gameLogic.count);
        assertEquals(1, gameLogic.playerIDOutcomeCheck);
    }
}