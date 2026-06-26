/************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: April 23, 2026
/* Due Date: May 15, 2026
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: Card.java
/* Purpose: Simple Card class
/************************************************************/

package fiveCard;

/**
 * This Card class represents all data and related operations for a single playing
 * card in a standard deck of playing cards.
 */
public class Card {

    enum Rank { TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE }
    enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }

    private Rank rank;
    private Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public String toString() {
        return rank + " of " + suit;
    }


}//end Card
