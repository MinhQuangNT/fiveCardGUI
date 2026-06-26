/************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: April 23, 2026
/* Due Date: May 15, 2026
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: Hand.java
/* Purpose: Generic hand class for a standard game
/************************************************************/

package fiveCard;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents all data and related operations for a hand of playing cards
 * for use in various card games. Implemented in this package for the game Baccarat.
 */
public class Hand {
    /**
     * Dynamic Array structure for storing Cards in the hand
     */
    protected ArrayList<Card> hand;

    public Hand() {
        hand = new ArrayList<>();
    }//end constructor

    public Card getCard(int pos) throws PokerException {
        if(pos < 0 || pos >= hand.size())
            throw new PokerException("Unable to get card.");
        return hand.get(pos);
    }

    public int getCardCount() {
        return hand.size();
    }

    public void addCard(Card card) throws PokerException {
        if(card == null)
            throw new PokerException("Unable to add card.");
        hand.add(card);
    }

    public void removeCard(Card card) throws PokerException {
        if(card == null)
            throw new PokerException("Unable to remove card.");
        hand.remove(card);
    }

    public void clear() {
        hand.clear();
    }

/*************************************************************************/
/* Function name: toString
/* Description: Allows for more efficient String printing
/* Parameters:
/* Return Value: String
/*************************************************************************/
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for (Card card : hand) {
            strBuilder.append(card.toString() + ", ");
        }
        strBuilder.deleteCharAt(strBuilder.length() - 2); // delete the last comma
        System.out.println();

        return strBuilder.toString();
    }

}//end Hand
