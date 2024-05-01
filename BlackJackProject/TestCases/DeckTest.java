package TestCases;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ClassSource.Deck;
import ClassSource.Card;
public class DeckTest {

    private Deck deck;

    @BeforeEach
    public void setUp() {
        deck = new Deck();
    }

    @Test
    public void testDeckInitialization() {
        assertNotNull(deck);
        assertEquals(104, deck.getRemainingCards());
    }

    @Test
    public void testDrawCard() {
        int initialCardCount = deck.getRemainingCards();

        Card drawnCard = deck.drawCard();

        assertNotNull(drawnCard);
        assertEquals(initialCardCount - 1, deck.getRemainingCards());
    }

    @Test
    public void testDrawAllCards() {
        for (int i = 0; i < 104; i++) {
            assertNotNull(deck.drawCard());
        }

        assertNull(deck.drawCard());
    }
}