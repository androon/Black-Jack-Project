package ClassSource;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Collections;

public class Deck {
    private Queue<Card> cards;

    public Deck() {
        cards = new LinkedList<>();
        initializeDeck();
    }
    
    public void initializeDeck() {
        for(int i = 0; i < 8; i++) {
        	for(int j = 1; j <= 9; j++) {
        		cards.add(new Card(j));
        	}
        }
        
        for(int i = 0; i < 32; i++) {
        	cards.add(new Card(10));
        }
    }
    
    public void shuffle() {
        LinkedList<Card> cardList = new LinkedList<>(cards);
        Collections.shuffle(cardList);
        cards.clear();
        for (int i = 0; i < cardList.size(); i++) {
            cards.add(cardList.get(i)); 
        }
    }
    
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove();
    }
    
    public int getRemainingCards() {
        return cards.size();
    }
}