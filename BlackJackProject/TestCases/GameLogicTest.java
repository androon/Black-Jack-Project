package TestCases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ClassSource.Deck;
import ClassSource.GameLogic;

public class GameLogicTest {

    private GameLogic gameLogic;

    @BeforeEach
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