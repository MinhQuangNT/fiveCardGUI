/************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: April 23, 2026
/* Due Date: MAy 15, 2026
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: Deck.java
/* Purpose: Deck class that can shuffle, deal, and count
/* the number of cards in the deck.
/************************************************************/

package fiveCard;

/**
 * This class represents all data and operations for a deck
 * of standard playing cards used in various casino
 * games. Implemented in this package for the game fiveCardPoker.
 */
public class Deck {

    private Card[] deck; //Cards in the Deck
    private int top;     //Pointer/Index of top card in Shoe (next card to deal)
    private int reshuffleCount;
    private int dealtCount;

    /**
     * Constructor
     */
    public Deck() throws PokerException {
        deck = new Card[52];
        top = 0;
        dealtCount = 0;

        int index = 0;
        for(Card.Suit s : Card.Suit.values()) {
            for(Card.Rank r : Card.Rank.values()) {
                deck[index] = new Card(r, s);
                index++;
            }
        }
        reshuffleCount = (int)(Math.random() * (43 - 11 + 1)) + 11; //11 - 43

        if(reshuffleCount > 43)
            throw new PokerException("Reshuffle threshold too large.");
        reshuffleCount = generateReshuffleCount();
    }//end constructor

    public int generateReshuffleCount() throws PokerException {
        int count = (int)(Math.random() * (43 - 11 + 1)) + 11; //11 - 43

        if(count > 43)
            throw new PokerException("Reshuffle threshold too large.");
        return count;
    }

    /**
     * Return all cards to the deck and shuffle
     * using the Fisher-Yates shuffling algorithm.
     */
    public void shuffle() throws PokerException {
        top = 0;
        dealtCount = 0;
        reshuffleCount = generateReshuffleCount();

        for(int i = deck.length - 1; i > top; i--) {
            int rnd = (int) (Math.random() * (i + 1) );
            Card temp = deck[rnd];
            deck[rnd] = deck[i];
            deck[i] = temp;
        }//end for

    }//end shuffle

    public int cardsLeft() {
        return deck.length - top;
    }

    public Card deal() throws PokerException {
        if(dealtCount >= reshuffleCount)
            throw new PokerException("Reshuffle needs to happen. Current Card dealt " + dealtCount + "/" + reshuffleCount + ".");
        dealtCount++;
        return deck[top++];
    }

    public boolean reshuffleFlag() {
        return dealtCount >= reshuffleCount;
    }

    public int getDealtCount() {
        return dealtCount;
    }

    public void setDealtCount(int dealtCount) {
        this.dealtCount = dealtCount;
    }
    
    public int getReshuffleCount() {
        return reshuffleCount;
    }

}//end Deck
