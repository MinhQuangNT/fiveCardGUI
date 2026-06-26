/************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: April 23, 2026
/* Due Date: May 15, 2016
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: PokerHand.java
/* Purpose: Hand subclass. The logic for calculating hand
/* rank is stored here.
/************************************************************/
package fiveCard;
import java.util.ArrayList;
import java.util.Arrays;

public class PokerHand extends Hand implements Comparable<PokerHand> {
    enum HandRank { HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT, FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH, ROYAL_FLUSH }

    public PokerHand() {
        super();
    }

    @Override
    public void addCard(Card card) throws PokerException {
        super.addCard(card);
    }

    @Override
    public void removeCard(Card card) throws PokerException {
        super.removeCard(card);
    }

/*************************************************************************/
/* Function name: getHandRank
/* Description: Get the rank of the current hand
/* Parameters:
/* Return Value: HandRank
/*************************************************************************/
    public HandRank getHandRank() {
        int[] sameRankCount = new int[13];
        int[] sameSuitCount = new int[4];

        for (Card c : hand) {
            int rank = c.getRank().ordinal();
            int suit = c.getSuit().ordinal();

            sameRankCount[rank]++;
            sameSuitCount[suit]++;
        }
        boolean straight = straightCheck();
        boolean flush = flushCheck(sameSuitCount);
        boolean four = hasFour(sameRankCount);
        boolean three = hasThree(sameRankCount);

        int pairs = 0;
        for (int i = 0; i < sameRankCount.length; i++) {
            if(sameRankCount[i] == 2) pairs++;
        }

        if(straight && flush && highestRank() == 12)
            return HandRank.ROYAL_FLUSH;
        else if(straight && flush)
            return HandRank.STRAIGHT_FLUSH;
        else if(four)
            return HandRank.FOUR_OF_A_KIND;
        else if(three && pairs == 1)
            return HandRank.FULL_HOUSE;
        else if(flush)
            return HandRank.FLUSH;
        else if(straight)
            return HandRank.STRAIGHT;
        else if(three)
            return HandRank.THREE_OF_A_KIND;
        else if(pairs == 2)
            return HandRank.TWO_PAIR;
        else if (pairs == 1)
            return HandRank.ONE_PAIR;
        else
            return HandRank.HIGH_CARD;
    }

    @Override
    public int compareTo(PokerHand other) {
       int result = this.getHandRank().ordinal() - other.getHandRank().ordinal();
       if(result != 0) return result;
       
       return tieBreak(this, other);
    }

/*************************************************************************/
/* Function name: highestRank
/* Description: Checks if the current card is an Ace (12)
/* Parameters:
/* Return Value: int
/*************************************************************************/
    private int highestRank() {
        int maxRank = -1;
        int[] ranksInHand = new int[5];

        for(int i = 0; i < hand.size(); i++) {
            ranksInHand[i] = hand.get(i).getRank().ordinal();
        }
        Arrays.sort(ranksInHand);

        for(Card c : hand) {
            int rank = c.getRank().ordinal();
            if(rank > maxRank && ranksInHand[0] == 8)
                maxRank = rank;
        }
        return maxRank;
    }

/*************************************************************************/
/* Function name: flushCheck
/* Description: Checks if the hand is a flush
/* Parameters: int[] sameSuitCount
/* Return Value: boolean
/*************************************************************************/
    private boolean flushCheck(int[] sameSuitCount) {
        for(int i = 0; i < sameSuitCount.length; i++) {
            if(sameSuitCount[i] == 5)
                return true;
        }
        return false;
    }

/*************************************************************************/
/* Function name: straightCheck
/* Description: Checks if the hand is a straight
/* Parameters:
/* Return Value: boolean
/*************************************************************************/
    private boolean straightCheck() {
        int[] ranksInHand = new int[5];

        for(int i = 0; i < hand.size(); i++) {
            ranksInHand[i] = hand.get(i).getRank().ordinal();
        }
        Arrays.sort(ranksInHand);

        if(ranksInHand[0] == 0 &&
                ranksInHand[1] == 1 &&
                ranksInHand[2] == 2 &&
                ranksInHand[3] == 3 &&
                ranksInHand[4] == 12)
            return true;

        for(int i = 0; i < hand.size() - 1; i++) {
            if(ranksInHand[i] + 1 != ranksInHand[i+1])
                return false;
        }
        return true;
    }

/*************************************************************************/
/* Function name: hasFour
/* Description: Checks if the hand is a Four of a Kind
/* Parameters: int[] SameRankCount
/* Return Value: boolean
/*************************************************************************/
    private boolean hasFour(int[] sameRankCount) {
        for(int i = 0; i < 13; i++)
            if(sameRankCount[i] == 4)
                return true;
        return false;
    }

/*************************************************************************/
/* Function name: hasThree
/* Description: Checks if the hand is a Three of a Kind
/* Parameters: int[] SameRankCount
/* Return Value: boolean
/*************************************************************************/
    private boolean hasThree(int[] sameRankCount) {
        for(int i = 0; i < 13; i++)
            if(sameRankCount[i] == 3)
                return true;
        return false;
    }

/*************************************************************************/
/* Function name: tieBreak
/* Description: For tie-breaking purposes
/* Parameters: Hand player, Hand dealer
/* Return Value: int
/*************************************************************************/
    public static int tieBreak(PokerHand player, PokerHand dealer) {
        int[] pSameRankCount = new int[13];
        int[] dSameRankCount = new int[13];

        for(int i = 0; i < player.getCardCount(); i++) {
        try {
            pSameRankCount[player.getCard(i).getRank().ordinal()]++;
            dSameRankCount[dealer.getCard(i).getRank().ordinal()]++;
        } catch(PokerException e) {
            System.out.println(e.getMessage());
        }
        } 

        HandRank rank = player.getHandRank();
        int customRankValue = 0;
        if(rank == HandRank.FULL_HOUSE || rank == HandRank.THREE_OF_A_KIND) customRankValue = 3;
        else if(rank == HandRank.FOUR_OF_A_KIND) {
            customRankValue = 4;
        }
        else if(rank == HandRank.TWO_PAIR || rank == HandRank.ONE_PAIR) {
            customRankValue = 2; 
        }

        if(customRankValue != 0) {
            for(int i = 12; i >= 0; i--) {
                if(pSameRankCount[i] == customRankValue && dSameRankCount[i] != customRankValue) return 1;
                if(dSameRankCount[i] == customRankValue && pSameRankCount[i] != customRankValue) return -1;
            }
        }

        if(rank == HandRank.FULL_HOUSE) { //compare the pairs of two full house hands
            for(int i = 12; i >= 0; i--) {
                if(pSameRankCount[i] == 2 && dSameRankCount[i] != 2) return 1;
                if(dSameRankCount[i] == 2 && pSameRankCount[i] != 2) return -1;
            }
        }

        if(rank == HandRank.STRAIGHT || rank == HandRank.STRAIGHT_FLUSH) {
        int pMax = -1;
        int dMax = -1;
        for(int i = 12; i >= 0; i--) {
            if(pSameRankCount[i] > 0 && pMax == -1) pMax = i;
            if(dSameRankCount[i] > 0 && dMax == -1) dMax = i;
        }
        //ace-low check
        if(pSameRankCount[12] > 0 && pSameRankCount[11] == 0) pMax = 4;
        if(dSameRankCount[12] > 0 && dSameRankCount[11] == 0) dMax = 4;

        if(pMax > dMax) return 1;
        if(pMax < dMax) return -1;
        return 0;
        }

        for(int i = 12; i >= 0; i--) {
        if(pSameRankCount[i] > 0 && dSameRankCount[i] == 0) {
            //System.out.println("Max Rank: " + i);
            return 1;
        }
        if(dSameRankCount[i] > 0 && pSameRankCount[i] == 0) {
            //System.out.println("Max Rank: " + i);
            return -1;
        }
        }

        return 0;
    }
}