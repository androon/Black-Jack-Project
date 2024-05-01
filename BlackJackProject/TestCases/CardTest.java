package TestCases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ClassSource.Card;

public class CardTest {

    @Test
    public void testConstructor() {
        int initialValue = 7;
        Card card = new Card(initialValue);
        assertNotNull(card);
        assertEquals(initialValue, card.getValue());
    }

    @Test
    public void testGetter() {
        int expectedValue = 10;
        Card card = new Card(expectedValue);
        int actualValue = card.getValue();
        assertEquals(expectedValue, actualValue);
    }
}