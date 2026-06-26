/*************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: April 23, 2026
/* Due Date: May 15, 2026
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: CasinoCardGame.java
/* Purpose: Abstract base class for a general casino card game
/*************************************************************/
package fiveCard;

/**
 * Abstract base class for a general casino card game.
 * @author Minh Quang Nguyen Tong
 */
public abstract class CasinoCardGame {
    protected Deck deck;
    protected Hand dealer;
    protected Hand player;

    public CasinoCardGame() {
        player = new PokerHand();
        dealer = new PokerHand();
    }

    public abstract void play(String[] args);
}