/************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: April 23, 2026
/* Due Date: May 15, 2026
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: PokerCasinoGame.java
/* Purpose: Ask for name, display the hand ranks of the
/* player and dealer, and display the round result
/************************************************************/
package fiveCard;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class for the Five Card Poker casino game
 * <p>The player is always the Small Blind ($25) and the dealer is always
 * the Big Blind ($50). The player begins with a starting balance of 
 * $1000, which is configurable.
 * </p>
 * @author Minh Quang Nguyen Tong
 */
class PokerCasinoGame extends CasinoCardGame {
	private Scanner input;
  	private Bettor bettor;
  	private boolean reshuffleFlag;

	/**
     * Creates a game with a $1,000 starting balance
     * @param args optional command-line card arguments
     */
	public static void main(String[] args) {
		PokerCasinoGame game = new PokerCasinoGame(1000.0);
		game.play(args);
	}

	/**
     * PokerCasinoGame constructor
     * @param startBal the player's starting balance
     */
	public PokerCasinoGame(double startBal) {
		super();
		input = new Scanner(System.in);
		reshuffleFlag = false;

		try {
			deck = new Deck();
			deck.shuffle();
		} 
		catch(PokerException e) {
			System.out.println("Deck initialization failed");
			System.exit(1);
		}
  
		System.out.print("Enter Name: ");
		String name = input.nextLine();
		bettor = new Bettor(name, startBal);

		System.out.println();
		System.out.println("Welcome to Five Card Poker Game, " + bettor.getName());
		System.out.println("Starting balance:  $" + String.format("%,3.2f", bettor.getBalance()));
		System.out.println();
	}

	/**
     * Main game loop. Handles reshuffling, dealing, blind posting,
     * both betting rounds, the discard phase, and the result.
     * @param args optional command-line card arguments
     */
	@Override
	public void play(String[] args) {
		boolean cmdLineFlag = false;
		if( args.length > 0 ) {
			try {
				CmdLineInput.parseArguments(args, player, dealer); //STUDENT: Enter Hands here
				cmdLineFlag = true;
			}
			catch(PokerException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}

		boolean firstRound = true;
		boolean continueFlag = true;

		while(continueFlag) {
			if(reshuffleFlag) {
				player.clear();
            	dealer.clear();
        		System.out.println("Reshuffling...");
				try {
					deck.shuffle();
				}
				catch(PokerException e) {
					System.out.println("Deck reshuffle failed");
					System.exit(1);
				}
				reshuffleFlag = false;
			}	

			if (!cmdLineFlag || !firstRound) {
                player.clear();
                dealer.clear();
	
				boolean dealt = false;
				while(!dealt) {
					try {
						dealHands();
						dealt = true;
					}
					catch(PokerException e) {
						System.out.println("Reshuffling...");
						player.clear();  
        				dealer.clear();  
						try { 
							deck.shuffle(); 
						}
						catch(PokerException e2) { 
							System.out.println("Deck reshuffle failed"); 
							System.exit(1); 
						}
					}
				}
            }
            cmdLineFlag = false;
            firstRound  = false;
			bettor.resetBet();
			System.out.println("Current Balance: $" + String.format("%,3.2f", bettor.getBalance()));
			postBlinds();

			System.out.println("Player Hand: " + player);
      		displayHandRank((PokerHand)player, "Player"); 
			boolean hasFolded = firstBetRound();
			if(hasFolded) {
				System.out.println("L hand..., folded. Round over.");
				printBalance();
				continueFlag = isContinued();
				continue;
			}
			reshuffleFlag = playerDiscard(input, deck, (PokerHand)player, reshuffleFlag);
      		reshuffleFlag = dealerDiscard(deck, (PokerHand)dealer, reshuffleFlag);

			System.out.println();
			System.out.println("Player Hand: " + player);
			displayHandRank((PokerHand)player, "Player");
			boolean hasFolded2 = secondBetRound();
			if(hasFolded2) {
				System.out.println("L hand..., folded. Round over.");
				printBalance();
				continueFlag = isContinued();
				continue;
			}
			System.out.println("Dealer Hand: " + dealer);
			displayHandRank((PokerHand)dealer, "Dealer");
			System.out.println("Cards dealt: " + deck.getDealtCount() + " / " + deck.getReshuffleCount());
			String roundResult = bettor.collectWinnings((PokerHand)player, (PokerHand)dealer);
			System.out.println(roundResult);
			printBalance();

			if(reshuffleFlag)
				System.out.println("(Deck will be reshuffled at the start of the next round.)");
			
			continueFlag = isContinued();
		}//end rematch while loop

		System.out.println("So, your passion has finally gone cold, " + bettor.getName() + "? Do come back when you feel it's burning again.");
		System.out.println("Balance: $" + String.format("%,3.2f", bettor.getBalance()));
	}//end play

	/**
     * This method decides whether or not to play another round
     * @return Returns true if the player chooses to continue, otherwise, returns false
     */
	private boolean isContinued() {
        String vote = "";
        while (!vote.equalsIgnoreCase("Y") && !vote.equalsIgnoreCase("N")) {
            System.out.print("\nRematch? (Y/N): ");
            vote = input.nextLine().trim();
            if (!vote.equalsIgnoreCase("Y") && !vote.equalsIgnoreCase("N"))
                System.out.println("Please enter Y or N.");
        }
        return vote.equalsIgnoreCase("Y");
    }

	/**
     * Posts the Small Blind ($25) for the player and the Big Blind
     * ($50) for the dealer at the start of each round.
     */
	private void postBlinds() {
		System.out.println("Small Blind: $" + String.format("%,3.2f", Bettor.SMALL_BLIND));
		System.out.println("Big Blind: $" + String.format("%,3.2f", Bettor.BIG_BLIND));

		try {
			bettor.placeBet(Bettor.SMALL_BLIND, 0);
		} 
		catch(PokerException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}
		System.out.println();
	}

	/**
     * Runs the first betting round. The player may call (match the Big Blind),
     * raise (add more money into the pot), or fold (lose their Small Blind and end
     * the round). If the player raises, the dealer automatically calls.
     *
     * @return Returns true if the player folded, otherwise, returns false
     */
	private boolean firstBetRound() {
		System.out.println("First Betting Round");
		System.out.println("Pot: $" + String.format("%,3.2f", currPot()));
		System.out.print("Enter 'c' to call, 'r' to raise, or 'f' to fold: ");
		String choice = getBetChoice();

		if(choice.equalsIgnoreCase("F")) {
			return true;
		}

		if(choice.equalsIgnoreCase("C")) {
			double amountToMatch = Bettor.BIG_BLIND - Bettor.SMALL_BLIND;
			try {
				bettor.placeBet(amountToMatch, 0);
			}
			catch(PokerException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
			String myStr = "You called $%1$,3.2f. (-$%1$,3.2f)";
			System.out.println(String.format(myStr, amountToMatch));
		} else {
			double amountToRaise = getRaiseAmount(0); //round index
			System.out.print("You raised $" + String.format("%,3.2f", amountToRaise) + ". Dealer calls.");
		}
		System.out.println();
		System.out.println("Pot: $" + String.format("%,3.2f", currPot()));
		return false;
	}

	/**
     * Basically the same thing as firstBetRound(). The only difference is
	 * that checking doesn't place an additional bet.
     * @return Returns true if the player folded, otherwise, returns false
     */
	private boolean secondBetRound() {
		System.out.println("Second Betting Round");
		System.out.println("Pot: $" + String.format("%,3.2f", currPot()));
		System.out.print("Enter 'c' to check, 'r' to raise, or 'f' to fold: ");
		String choice = getBetChoice();

		if(choice.equalsIgnoreCase("F")) {
			return true;
		}

		if(choice.equalsIgnoreCase("C")) {
			System.out.println("You checked. Dealer also checks.");
		} else {
			double amountToRaise = getRaiseAmount(1); //round index
			System.out.print("You raised $" + String.format("%,3.2f", amountToRaise) + ". Dealer calls.");
		}
		System.out.println("Pot: $" + String.format("%,3.2f", currPot()));

		return false;
	}

	/**
     * Processes the betting choice from the player.
     * Accepts 'C' (call/check), 'R' (raise), or 'F' (fold), case-insensitive.
     * @return the validated single-character choice string
     */
	private String getBetChoice() {
		String choice = "";
		boolean choiceFlag = false;
		while(!choiceFlag) {
			choice = input.nextLine().trim();
			if(choice.equalsIgnoreCase("C") || choice.equalsIgnoreCase("R") || choice.equalsIgnoreCase("F"))
				choiceFlag = true;
			else {
				System.out.print("Try again. Only enter 'c', 'r', or 'f': ");
			}
		}
		return choice;
	}

	/**
     * Prompts the player for a raise amount, validates it is a positive number
     * within their balance, and deducts it from their wallet.
     * @param round the betting round index (0 = first round, 1 = second round)
     * @return the raise amount
     */
	private double getRaiseAmount(int round) {
		double amount = 0.0;
		boolean raiseFlag = false;
		while(!raiseFlag) {
			System.out.print("Enter raise: $");
			double inputAmount = input.nextDouble();
			input.nextLine();
			try {
				amount = inputAmount;
				bettor.placeBet(amount, round);
				raiseFlag = true;
			} 
			catch(NumberFormatException e) {
				System.out.println("Enter a valid number.");
			}
			catch(PokerException e) {
				System.out.print(e.getMessage());
			}
		}
		return amount;
	}

	/**
     * Deals five cards each to the player and dealer from the deck.
     * @throws PokerException if the deck's reshuffle threshold is reached during dealing
     */
  	private void dealHands() throws PokerException {
    	for(int i = 0; i < 5; i++) {
      		player.addCard(deck.deal());
      		dealer.addCard(deck.deal());
    	}
  	}

	/**
     * Handles the player's discard phase. Entering -1 skips the discard entirely. 
	 *  Sets the reshuffle flag if the deck cannot supply enough replacement cards.
     *
     * @param input the Scanner for terminal input
     * @param deck the deck to draw replacement cards from
     * @param player the player's hand
     * @param reshuffleFlag the current reshuffle flag state
     * @return the updated reshuffle flag
     */
	private boolean playerDiscard(Scanner input, Deck deck, PokerHand player, boolean reshuffleFlag) {
		System.out.print("Enter card(s) position in hand to discard (To cancel, enter '-1'): ");
		String cardLine = input.nextLine().trim();

		if(cardLine.equals("-1")) return reshuffleFlag;
		String[] tokens = cardLine.split(" ");
		int[] positions = new int[tokens.length];
		boolean checkStuff = true;
		for(int i = 0; i < tokens.length; i++) {
			try {
				positions[i] = Integer.parseInt(tokens[i]) -1;
				player.getCard(positions[i]);
			}
			catch(NumberFormatException e) {
				System.out.println("'" + tokens[i] + "' is not a number. Try again.");
				checkStuff = false;
				break;
			}
			catch(PokerException e) {
				System.out.println("Position " + tokens[i] + " is out of range. Try again.");
				checkStuff = false;
				break;
			}
		}

		if(!checkStuff)
			return playerDiscard(input, deck, player, reshuffleFlag); 
		ArrayList<Card> cardDiscardList = new ArrayList<>();
		for(int pos : positions) {
			try{
				cardDiscardList.add(player.getCard(pos));
			}
			catch(PokerException e) {
				System.out.println(e.getMessage());
			}
		}
		if(deck.getDealtCount() + cardDiscardList.size() > deck.getReshuffleCount()) {
			reshuffleFlag = true;
			return reshuffleFlag;
		}
		for(Card c : cardDiscardList) {
			try{
				player.removeCard(c);
			}
			catch(PokerException e) {
				System.out.print(e.getMessage());
			}
		}
		for(int i = 0; i < cardDiscardList.size(); i++) {
			try {
				player.addCard(deck.deal());
			}
			catch(PokerException e) {
				reshuffleFlag = true;
				System.out.println("Deck reached reshuffle threshold. Reshuffling after this round.");
				break;
			}
		}
	
		return reshuffleFlag;
  	} 

	/**
     * Handles the dealer's automated discard phase using the following logic:
     * <ul>
     *   <li>If the hand is higher than 3 of a Kind, discard nothing.</li>
     *   <li>Three of a Kind: discard the two non-matching cards.</li>
     *   <li>Two Pair: discard the lone unmatching card.</li>
     *   <li>One Pair: discard everything but the 2 matching cards.</li>
     *   <li>High Card: keep the highest-ranked card, discard the rest.</li>
     * </ul>
     * Sets the reshuffle flag if the deck cannot supply enough replacement cards.
     *
     * @param deck the deck to draw replacement cards from
     * @param dealer the dealer's hand
     * @param reshuffleFlag the current reshuffle flag state
     * @return the updated reshuffle flag
     */
	private boolean dealerDiscard(Deck deck, PokerHand dealer, boolean reshuffleFlag) {
		PokerHand.HandRank rank = dealer.getHandRank();
		int[] sameRankCount = new int[13];
		for(int i = 0; i < dealer.getCardCount(); i++) {
			try {
				sameRankCount[dealer.getCard(i).getRank().ordinal()]++;
			} catch(PokerException e) {
				System.out.println(e.getMessage());
			}
		}
		ArrayList<Card> cardDiscardList = new ArrayList<>();
		if(rank.ordinal() > PokerHand.HandRank.THREE_OF_A_KIND.ordinal()) {
		//discard nothing
		} else if(rank == PokerHand.HandRank.THREE_OF_A_KIND) {
			for(int i = 0; i < dealer.getCardCount(); i++) {
				try {
					Card c = dealer.getCard(i);
					if(sameRankCount[c.getRank().ordinal()] != 3) cardDiscardList.add(c);
				}
				catch(PokerException e) { System.out.println(e.getMessage()); }
			}
		} else if(rank == PokerHand.HandRank.TWO_PAIR) {
			for(int i = 0; i < dealer.getCardCount(); i++) {
				try {
					Card c = dealer.getCard(i);
					if(sameRankCount[c.getRank().ordinal()] == 1) cardDiscardList.add(c);
				}
				catch(PokerException e) { System.out.println(e.getMessage()); }
		}
		} else if(rank == PokerHand.HandRank.ONE_PAIR) {
			for(int i = 0; i < dealer.getCardCount(); i++) {
				try {
					Card c = dealer.getCard(i);
					if(sameRankCount[c.getRank().ordinal()] != 2) cardDiscardList.add(c);
				}
				catch(PokerException e) { System.out.println(e.getMessage()); }
			}
		} else {
			int maxRank = -1;
			for(int i = 0; i < dealer.getCardCount(); i++) {
				try {
					int tempRank = dealer.getCard(i).getRank().ordinal();
					if(tempRank > maxRank) maxRank = tempRank;
				}
				catch(PokerException e) { System.out.println(e.getMessage()); }
			}
			boolean keepMaxRank = false;
			for(int i = 0; i < dealer.getCardCount(); i++) {
				try {
					Card c = dealer.getCard(i);
					if(c.getRank().ordinal() == maxRank && !keepMaxRank)
						keepMaxRank = true; //keep the highest rank card
					else
						cardDiscardList.add(c);
				}
				catch(PokerException e) { System.out.println(e.getMessage()); }
			}
		}
		if(deck.getDealtCount() + cardDiscardList.size() > deck.getReshuffleCount()) {
			reshuffleFlag = true;
			return reshuffleFlag;
		}
		for(Card c : cardDiscardList) {
			try{
				dealer.removeCard(c);
			}
			catch(PokerException e) {
				System.out.print(e.getMessage());
			}
		}

		for(int i = 0; i < cardDiscardList.size(); i++) {
			try {
				dealer.addCard(deck.deal());
			}
			catch(PokerException e) {
				reshuffleFlag = true;
				System.out.println("Deck reached reshuffle threshold. Reshuffling after this round.");
				break;
			}
		}
	
		return reshuffleFlag;
	}

	/**
     * Prints the given hand's rank to the terminal, labelled with 
	 * the entity name.
     * @param hand the hand to evaluate and display
     * @param entity the label to prefix the rank with
     */
	private void displayHandRank(PokerHand hand, String entity) {
		System.out.println(entity + "'s Rank: " + hand.getHandRank());
		System.out.println();
	}

	/**
     * Prints the player's current balance and total winnings
     */
	private void printBalance() {
		System.out.println("Balance: $" + String.format("%,3.2f", bettor.getBalance()));
		System.out.println("Total Winnings: $" + String.format("%,3.2f", bettor.getTotalWinnings()));
	}

	/**
     * Calculates and returns the current pot value.
     * @return the current pot amount
     */
	private double currPot() {
		double playerBet = bettor.getWager(0) + bettor.getWager(1);
		double dealerBet = Math.max(Bettor.BIG_BLIND, playerBet);
		return playerBet + dealerBet;
	}

}//end PokerCasinoGame
