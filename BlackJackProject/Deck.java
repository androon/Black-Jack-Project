import java.util.LinkedList;
import java.util.Queue;
import java.util.Collections;

class Deck {
    private Queue<Card> cards;
    private int numberOfDecks;

    public Deck(int numberOfDecks) {
        this.numberOfDecks = numberOfDecks;
        cards = new LinkedList<>();
        initializeDeck();
    }
    
    public void initializeDeck() {
        cards.clear();
        for (int deck = 0; deck < numberOfDecks; deck++) {
            for (int i = 0; i < 4; i++) { 
                for (int j = 1; j <= 13; j++) {
                    cards.add(new Card(j));
                }
            }
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
            System.out.println("No more cards");
            return null;
        }
        return cards.remove();
    }
    
    public int getRemainingCards() {
        return cards.size();
    }
}