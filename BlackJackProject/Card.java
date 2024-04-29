package blackjack;

/*
 * Card class
 * */
import java.io.Serializable;

class Card implements Serializable{
    private int value;

    public Card(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
}

