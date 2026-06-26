/************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: April 23, 2026
/* Due Date: May 15, 2026
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: Bettor.java
/* Purpose: Manages information for a user including their
/* name, balance, and all wager logic for a round
/************************************************************/
package fiveCard;

/**
 * Manages all betting information for the player in a Five Card Poker game.
 * @author Minh Quang Nguyen Tong
 */
public class Bettor {
    String name;
    double startBal;
    double currBal;
    private double[] wagers;
    private double totalWinnings;
    private double roundWinnings;
    public static final double SMALL_BLIND = 25.0;
    public static final double BIG_BLIND = 50.0;

    /**
     * Bettor constructor with the given name and starting balance.
     * @param name the player's display name
     * @param startBal the player's starting balance
     */
    public Bettor(String name, double startBal) {
        this.name = name;
        this.startBal = startBal;
        this.currBal = startBal;
        this.wagers = new double[2];
        this.totalWinnings = 0.0;
        this.roundWinnings = 0.0;
    }

    /**
     * Records a wager for the specified betting round and deducts it from
     * the player's current balance.
     * @param betAmount the amount to wager
     * @param round the betting round index (0 = first round, 1 = second round)
     * @throws PokerException if betAmount is non-positive or exceeds the current balance
     */
    public void placeBet(double betAmount, int round) throws PokerException {
        if(betAmount <= 0) throw new PokerException("Bet amount has to be greater than 0.");
        if(currBal < betAmount) {
            System.out.println();
            throw new PokerException("Insufficient balance, son. Current balance: $" + 
            String.format("%,3.2f", currBal));
        }
        wagers[round] += betAmount;
        currBal -= betAmount;
    }

    /**
     * Processes the outcome of a round by comparing the player's hand to the dealer's
     * updates the current balance and total winnings accordingly, and returns a
     * summary string.

     * @param player the player's hand
     * @param dealer the dealer's hand
     * @return a summary of the round result and amount won or lost
     */
    public String collectWinnings(PokerHand player, PokerHand dealer) {
        double pTotalBet = wagers[0] + wagers[1];
        double dTotalBet = pTotalBet;
        double pot = pTotalBet + dTotalBet;
        String wagerSummary;
        int result = player.compareTo(dealer);

        if(result > 0) {
            currBal += pot;
            roundWinnings = pot - pTotalBet;
            totalWinnings += roundWinnings;
            wagerSummary = "Round won! +$" + String.format("%,3.2f", roundWinnings);
        } else if(result < 0) {
            roundWinnings = -pTotalBet;
            totalWinnings += roundWinnings;
            wagerSummary = "Round lost! -$" + String.format("%,3.2f", -roundWinnings);
        } else {
            currBal += pTotalBet;
            roundWinnings = 0.0;
            wagerSummary = "Push! Returned $" + String.format("%,3.2f", pTotalBet);
        }
        return wagerSummary;
    }

    public void resetBet() {
        for(int i = 0; i < wagers.length; i++) {
            wagers[i] = 0.0;
        }
        roundWinnings = 0.0;
    }

    public double getBalance() {
        return currBal;
    }

    public double getRoundWinnings() {
        return roundWinnings;
    }

    public double getTotalWinnings() {
        return totalWinnings;
    }

    public double getWager(int round) {
        return wagers[round];
    }

    public String getName() {
        return name;
    }
}