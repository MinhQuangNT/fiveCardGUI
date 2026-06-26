/************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: April 23, 2026
/* Due Date: May 15, 2026
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: PokerException.java
/* Purpose: Custom-defined exception for issues related
/* to the game logic
/************************************************************/
package fiveCard;

public class PokerException extends Exception {
    public PokerException(String m) {
        super(m);
    }
}