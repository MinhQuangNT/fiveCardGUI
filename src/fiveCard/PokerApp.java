/************************************************************/
/* Author: Minh Quang Nguyen Tong
/* Major: Information Technology
/* Creation Date: May 2026
/* Course: CSC243 010
/* Professor Name: Nye Griffin
/* Assignment: #6
/* Filename: PokerApp.java
/* Purpose: JavaFX GUI front-end for Five Card Draw Poker,
/* wired to the Project 4 back-end classes.
/************************************************************/

package fiveCard;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * JavaFX front-end for Five Card Draw Poker.
 *
 * <p>Game phases are tracked with the GamePhase enum so that
 * buttons are only enabled at appropriate times.</p>
 * 
 * @author Minh Quang Nguyen Tong
 */
public class PokerApp extends Application {

    /**
     * Represents the current state of the game.
     * Used to enable/disable buttons appropriately.
     */
    private enum GamePhase {
        WAITING,
        FIRST_BET,
        DISCARD,
        SECOND_BET,
        ROUND_OVER
    }

    private PokerCasinoGame game;
    private Bettor bettor;
    private Deck deck;
    private PokerHand playerHand;
    private PokerHand dealerHand;
    private boolean reshuffleFlag = false;
    private GamePhase phase = GamePhase.WAITING;
    private Label[] playerCardLabels = new Label[5];
    private Label[] dealerCardLabels = new Label[5];
    private StackPane[] playerCardPanes = new StackPane[5];
    private StackPane[] dealerCardPanes = new StackPane[5];
    private CheckBox[] discardBoxes = new CheckBox[5];
    private Label balanceLabel;
    private Label playerBetLabel;
    private Label deckCountLabel;
    private Label discardCountLabel;
    private Label dealerBetLabel;
    private Label potLabel;
    private Label playerRankLabel;
    private Label dealerRankLabel;
    private Label messageLabel;
    private Spinner<Integer> wagerSpinner;

    // Action buttons
    private Button dealBtn;
    private Button drawBtn;
    private Button callBtn;
    private Button raiseBtn;
    private Button foldBtn;
    private Button nextBtn;

    /**
     * Launches the application.
     *
     * @param args command-line arguments (unused in GUI mode)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initialises the back-end and builds the primary stage.
     *
     * @param primaryStage the window provided by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) {

        try {
            deck = new Deck();
            deck.shuffle();
            playerHand = new PokerHand();
            dealerHand = new PokerHand();
            bettor = new Bettor("Player", 1000.0);
        } catch (PokerException e) {
            System.out.println("Failed to initialise: " + e.getMessage());
            System.exit(1);
        }

        VBox root = new VBox(12);
        root.setPadding(new Insets(16));
        root.setStyle("-fx-background-color: #1a4a2e;");

        root.getChildren().addAll(
                titleBar(),
                dealerSection(),
                centerRow(),
                messageArea(),
                playerSection(),
                controlBar()
        );

        updateButtons();
        postMessage("Welcome! Press Deal to start a new round.");

        Scene scene = new Scene(root, 950, 730);
        primaryStage.setTitle("Five Card Draw Poker");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Builds the gold title bar at the top of the screen.
     *
     * @return a BorderPane containing the game title
     */
    private BorderPane titleBar() {
        Label title = new Label("\u2660  Five Card Draw Poker  \u2660");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 26));
        title.setTextFill(Color.web("#c9a84c"));

        BorderPane bar = new BorderPane(title);
        BorderPane.setAlignment(title, Pos.CENTER);
        return bar;
    }

    /**
     * Builds the dealer section: chips info box, 5 face-down card panes,
     * hand rank label, and dealer bet info box.
     *
     * @return a BorderPane representing the dealer area
     */
    private BorderPane dealerSection() {
        VBox chipsBox = infoBox("Dealer Chips", "Casino");
        dealerBetLabel = makeLabel("$0.00", 18, true);
        VBox betBox = infoBox("Dealer Bet", null);
        betBox.getChildren().add(dealerBetLabel);

        HBox cardRow = new HBox(10);
        cardRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            dealerCardPanes[i]  = makeCardPane(true, "???");
            dealerCardLabels[i] = (Label) dealerCardPanes[i].getChildren().get(0);
            cardRow.getChildren().add(dealerCardPanes[i]);
        }

        dealerRankLabel = makeLabel("Hand Ranking: ???", 12, false);
        Label heading   = makeLabel("Dealer Hand", 14, true);

        VBox centre = new VBox(6, heading, cardRow, dealerRankLabel);
        centre.setAlignment(Pos.CENTER);

        BorderPane section = new BorderPane();
        section.setLeft(chipsBox);
        section.setCenter(centre);
        section.setRight(betBox);
        BorderPane.setAlignment(chipsBox, Pos.CENTER);
        BorderPane.setAlignment(betBox,   Pos.CENTER);
        return section;
    }

    /**
     * Builds the center row containing the deck counter, pot display,
     * and discard/dealt counter.
     *
     * @return an HBox with the three center elements
     */
    private HBox centerRow() {
        potLabel = makeLabel("$0.00", 18, true);

        deckCountLabel    = makeLabel("? cards left", 14, false);
        discardCountLabel = makeLabel("? dealt",  14, false);

        VBox deckBox    = new VBox(4, makeLabel("Deck", 12, true), deckCountLabel);
        VBox discardBox = new VBox(4, makeLabel("Dealt", 12, true), discardCountLabel);
        VBox potBox     = new VBox(4, makeLabel("Pot", 12, true), potLabel);

        centerBoxStyle(deckBox);
        centerBoxStyle(discardBox);
        centerBoxStyle(potBox);

        HBox row = new HBox(60, deckBox, potBox, discardBox);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    /**
     * Builds the game-message area used to display instructions and events.
     *
     * @return a {@link VBox} containing the message panel
     */
    private VBox messageArea() {
        Label heading = makeLabel("Game Messages", 13, true);
        messageLabel  = makeLabel("", 12, false);
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);

        VBox box = new VBox(4, heading, messageLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle(
                "-fx-background-color: #00000066;" +
                "-fx-border-color: #c9a84c;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;");
        box.setMaxWidth(700);

        VBox wrapper = new VBox(box);
        wrapper.setAlignment(Pos.CENTER);
        return wrapper;
    }

    /**
     * Builds the player section: chips info box, 5 face-up card panes,
     * discard checkboxes, hand rank label, and player bet info box.
     *
     * @return a BorderPane representing the player area
     */
    private BorderPane playerSection() {
        balanceLabel = makeLabel("$" + String.format("%,.2f", bettor.getBalance()), 18, true);
        VBox chipsBox = infoBox("Player Chips", null);
        chipsBox.getChildren().add(balanceLabel);

        playerBetLabel = makeLabel("$0.00", 18, true);
        VBox betBox = infoBox("Current Bet", null);
        betBox.getChildren().add(playerBetLabel);

        HBox cardRow = new HBox(10);
        cardRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            playerCardPanes[i]  = makeCardPane(true, "???");
            playerCardLabels[i] = (Label) playerCardPanes[i].getChildren().get(0);
            cardRow.getChildren().add(playerCardPanes[i]);
        }

        HBox discardRow = new HBox();
        discardRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < 5; i++) {
            discardBoxes[i] = new CheckBox("Discard");
            discardBoxes[i].setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
            discardBoxes[i].setDisable(true);
            HBox wrapper = new HBox(discardBoxes[i]);
            wrapper.setAlignment(Pos.CENTER);
            wrapper.setPrefWidth(95);
            discardRow.getChildren().add(wrapper);
        }

        playerRankLabel = makeLabel("Hand Ranking: ???", 12, false);
        Label heading   = makeLabel("Player Hand", 14, true);

        VBox center = new VBox(6, heading, cardRow, discardRow, playerRankLabel);
        center.setAlignment(Pos.CENTER);

        BorderPane section = new BorderPane();
        section.setLeft(chipsBox);
        section.setCenter(center);
        section.setRight(betBox);
        BorderPane.setAlignment(chipsBox, Pos.CENTER);
        BorderPane.setAlignment(betBox, Pos.CENTER);
        return section;
    }

    /**
     * Builds the control bar containing all action buttons and the wager spinner.
     *
     * @return a {@link VBox} containing all controls
     */
    private VBox controlBar() {
        dealBtn  = makeButton("Deal", "#2980b9");
        drawBtn  = makeButton("Draw", "#245c38");
        callBtn  = makeButton("Call/Check", "#27ae60");
        foldBtn  = makeButton("Fold", "#c0392b");
        nextBtn  = makeButton("New Round", "#7d3c98");
        raiseBtn = makeButton("Raise", "#d4a017");

        wagerSpinner = new Spinner<>(10, 10000, 25, 25);
        wagerSpinner.setPrefWidth(95);

        dealBtn.setOnAction(e -> handleDeal());
        drawBtn.setOnAction(e -> handleDraw());
        callBtn.setOnAction(e -> handleCall());
        raiseBtn.setOnAction(e -> handleRaise());
        foldBtn.setOnAction(e -> handleFold());
        nextBtn.setOnAction(e -> handleNewRound());

        VBox innerVBox = new VBox(4, wagerSpinner, raiseBtn);
        innerVBox.setAlignment(Pos.CENTER);
        VBox raiseGroup = new VBox(2, makeLabel("Wager", 11, false), innerVBox);
        raiseGroup.setAlignment(Pos.CENTER);

        HBox bar = new HBox(12, dealBtn, drawBtn, callBtn, foldBtn, nextBtn);
        bar.setAlignment(Pos.CENTER);
        VBox finalBar = new VBox(12, bar, raiseGroup);
        finalBar.setAlignment(Pos.CENTER);
        return finalBar;
    }

    /**
     * Handles the Deal button press.
     * Deals from the existing deck. If coming from ROUND_OVER, resets the
     * card display first. Reshuffles mid-deal only if the deck runs low.
     */
    private void handleDeal() {
        //finished round? reset the visual display
        if (phase == GamePhase.ROUND_OVER) {
            for (int i = 0; i < 5; i++) {
                styleCardPane(playerCardPanes[i], true);
                playerCardLabels[i].setText("???");
                playerCardLabels[i].setTextFill(Color.web("#aaaaff"));
                styleCardPane(dealerCardPanes[i], true);
                dealerCardLabels[i].setText("???");
                dealerCardLabels[i].setTextFill(Color.web("#aaaaff"));
                discardBoxes[i].setSelected(false);
            }
            playerRankLabel.setText("Hand Ranking: ???");
            dealerRankLabel.setText("Hand Ranking: ???");
        }

        //clear hands
        playerHand.clear();
        dealerHand.clear();

        //reshuffle if the previous round flagged it
        if (reshuffleFlag) {
            try { deck.shuffle(); }
            catch (PokerException e) { postMessage("Reshuffle failed."); return; }
            reshuffleFlag = false;
        }

        //deal 5 cards each; reshuffle and retry if deck runs low mid-deal
        try {
            for (int i = 0; i < 5; i++) {
                playerHand.addCard(deck.deal());
                dealerHand.addCard(deck.deal());
            }
        } catch (PokerException e) {
            playerHand.clear();
            dealerHand.clear();
            try {
                deck.shuffle();
                for (int i = 0; i < 5; i++) {
                    playerHand.addCard(deck.deal());
                    dealerHand.addCard(deck.deal());
                }
            } catch (PokerException e2) {
                postMessage("Critical deal error."); return;
            }
        }

        //post small blind
        bettor.resetBet();
        try {
            bettor.placeBet(Bettor.SMALL_BLIND, 0);
        } catch (PokerException e) {
            postMessage("Cannot post blind: " + e.getMessage()); return;
        }

        //update GUI
        refreshPlayerCards(false);
        refreshDealerCards(true);
        playerRankLabel.setText("Hand Ranking: " + playerHand.getHandRank());
        dealerRankLabel.setText("Hand Ranking: ???");
        updateMoneyLabels();

        phase = GamePhase.FIRST_BET;
        updateButtons();
        postMessage("Cards dealt! Small Blind: $" + String.format("%.2f", Bettor.SMALL_BLIND) +
                "  |  Big Blind: $" + String.format("%.2f", Bettor.BIG_BLIND) +
                "\nFirst betting round — Call, Raise, or Fold.");
    }

    /**
     * Handles the Call/Check button press.
     */
    private void handleCall() {
        if (phase == GamePhase.FIRST_BET) {
            double toCall = Bettor.BIG_BLIND - Bettor.SMALL_BLIND;
            try {
                bettor.placeBet(toCall, 0);
            } catch (PokerException e) {
                postMessage(e.getMessage()); return;
            }
            updateMoneyLabels();
            postMessage("You called $" + String.format("%.2f", toCall) +
                    ". Select cards to discard, then press Draw.");
            phase = GamePhase.DISCARD;

        } else if (phase == GamePhase.SECOND_BET) {
            postMessage("You checked. Dealer also checks.\nRevealing hands...");
            revealResult();
            return;
        }
        updateButtons();
    }

    /**
     * Handles the Raise button press.
     */
    private void handleRaise() {
        double amount = wagerSpinner.getValue();
        int round = (phase == GamePhase.FIRST_BET) ? 0 : 1;
        try {
            bettor.placeBet(amount, round);
        } catch (PokerException e) {
            postMessage(e.getMessage()); return;
        }
        updateMoneyLabels();
        String next = (phase == GamePhase.FIRST_BET)
                ? "Select cards to discard, then press Draw."
                : "Revealing hands...";
        postMessage("You raised $" + String.format("%.2f", amount) + ". Dealer calls.\n" + next);

        if (phase == GamePhase.FIRST_BET) {
            phase = GamePhase.DISCARD;
            updateButtons();
        } else {
            revealResult();
        }
    }

    /**
     * Handles the Fold button press. Ends the round immediately.
     */
    private void handleFold() {
        postMessage("You folded. Round over.\nBalance: $" +
                String.format("%,.2f", bettor.getBalance()) +
                "  |  Total Winnings: $" +
                String.format("%,.2f", bettor.getTotalWinnings()));
        phase = GamePhase.ROUND_OVER;
        updateButtons();
    }

    /**
     * Handles the Draw button press.
     * Removes checked cards from the player's hand, deals replacements,
     * then runs the dealer's automated discard logic.
     */
    private void handleDraw() {
        java.util.ArrayList<Card> toDiscard = new java.util.ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (discardBoxes[i].isSelected()) {
                try { toDiscard.add(playerHand.getCard(i)); }
                catch (PokerException e) {}
            }
        }

        if (deck.getDealtCount() + toDiscard.size() > deck.getReshuffleCount()) {
            reshuffleFlag = true;
            postMessage("Not enough cards left to draw. Reshuffling after this round.\nProceeding without a discard.");
            toDiscard.clear();
        }

        for (Card c : toDiscard) {
            try { playerHand.removeCard(c); } catch (PokerException e) {}
        }
        int playerDiscardCount = toDiscard.size();
        for (int i = 0; i < playerDiscardCount; i++) {
            try { playerHand.addCard(deck.deal()); }
            catch (PokerException e) { reshuffleFlag = true; break; }
        }

        int dealerDiscardCount = dealerAutoDiscard();

        for (CheckBox cb : discardBoxes) cb.setSelected(false);

        refreshPlayerCards(false);
        playerRankLabel.setText("Hand Ranking: " + playerHand.getHandRank());

        phase = GamePhase.SECOND_BET;
        updateButtons();
        postMessage("You discarded " + playerDiscardCount + " card(s). Dealer discarded " +
                dealerDiscardCount + " card(s).\nSecond betting round — Check, Raise, or Fold.");
    }

    /**
     * Handles the New Round button.
     * Forces a full deck reshuffle and resets the card display.
     */
    private void handleNewRound() {
        // Force a full reshuffle
        try {
            deck.shuffle();
        } catch (PokerException e) {
            postMessage("Reshuffle failed: " + e.getMessage());
            return;
        }
        reshuffleFlag = false;

        // Reset card display
        for (int i = 0; i < 5; i++) {
            styleCardPane(playerCardPanes[i], true);
            playerCardLabels[i].setText("???");
            playerCardLabels[i].setTextFill(Color.web("#aaaaff"));
            styleCardPane(dealerCardPanes[i], true);
            dealerCardLabels[i].setText("???");
            dealerCardLabels[i].setTextFill(Color.web("#aaaaff"));
            discardBoxes[i].setSelected(false);
        }
        playerRankLabel.setText("Hand Ranking: ???");
        dealerRankLabel.setText("Hand Ranking: ???");

        phase = GamePhase.WAITING;
        updateButtons();
        updateMoneyLabels();
        postMessage("Deck reshuffled. Press Deal to begin.");
    }

    /**
     * Runs the dealer's automated discard phase and returns the number of
     * cards discarded.
     *
     * @return the number of cards the dealer discarded
     */
    private int dealerAutoDiscard() {
        PokerHand.HandRank rank = dealerHand.getHandRank();
        int[] sameRankCount = new int[13];
        for (int i = 0; i < dealerHand.getCardCount(); i++) {
            try { sameRankCount[dealerHand.getCard(i).getRank().ordinal()]++; }
            catch (PokerException e) {}
        }

        java.util.ArrayList<Card> cardDiscardList = new java.util.ArrayList<>();

        if (rank.ordinal() > PokerHand.HandRank.THREE_OF_A_KIND.ordinal()) {

        } else if (rank == PokerHand.HandRank.THREE_OF_A_KIND) {
            for (int i = 0; i < dealerHand.getCardCount(); i++) {
                try {
                    Card c = dealerHand.getCard(i);
                    if (sameRankCount[c.getRank().ordinal()] != 3) cardDiscardList.add(c);
                } catch (PokerException e) {}
            }

        } else if (rank == PokerHand.HandRank.TWO_PAIR) {
            for (int i = 0; i < dealerHand.getCardCount(); i++) {
                try {
                    Card c = dealerHand.getCard(i);
                    if (sameRankCount[c.getRank().ordinal()] == 1) cardDiscardList.add(c);
                } catch (PokerException e) {}
            }

        } else if (rank == PokerHand.HandRank.ONE_PAIR) {
            for (int i = 0; i < dealerHand.getCardCount(); i++) {
                try {
                    Card c = dealerHand.getCard(i);
                    if (sameRankCount[c.getRank().ordinal()] != 2) cardDiscardList.add(c);
                } catch (PokerException e) {}
            }

        } else {
            int maxRank = -1;
            for (int i = 0; i < dealerHand.getCardCount(); i++) {
                try {
                    int r = dealerHand.getCard(i).getRank().ordinal();
                    if (r > maxRank) maxRank = r;
                } catch (PokerException e) {}
            }
            boolean kept = false;
            for (int i = 0; i < dealerHand.getCardCount(); i++) {
                try {
                    Card c = dealerHand.getCard(i);
                    if (c.getRank().ordinal() == maxRank && !kept) kept = true;
                    else cardDiscardList.add(c);
                } catch (PokerException e) {}
            }
        }

        if (deck.getDealtCount() + cardDiscardList.size() > deck.getReshuffleCount()) {
            reshuffleFlag = true;
            return 0;
        }

        int count = cardDiscardList.size();
        for (Card c : cardDiscardList) {
            try { dealerHand.removeCard(c); } catch (PokerException e) {}
        }
        for (int i = 0; i < count; i++) {
            try { dealerHand.addCard(deck.deal()); }
            catch (PokerException e) { reshuffleFlag = true; break; }
        }
        return count;
    }

    /**
     * Reveals both hands, calculates the result, and updates all GUI labels.
     * Moves to ROUND_OVER phase.
     */
    private void revealResult() {
        refreshDealerCards(false);
        dealerRankLabel.setText("Hand Ranking: " + dealerHand.getHandRank());

        String result = bettor.collectWinnings(playerHand, dealerHand);
        updateMoneyLabels();

        String extra = reshuffleFlag ? "\n(Deck will reshuffle on next Deal.)" : "";
        postMessage(result +
                "\nBalance: $" + String.format("%,.2f", bettor.getBalance()) +
                "  |  Total Winnings: $" + String.format("%,.2f", bettor.getTotalWinnings()) +
                extra);

        phase = GamePhase.ROUND_OVER;
        updateButtons();
    }

    /**
     * Refreshes all 5 player card panes with the current hand contents.
     *
     * @param faceDown true to show card backs, false to show card faces
     */
    private void refreshPlayerCards(boolean faceDown) {
        for (int i = 0; i < 5; i++) {
            styleCardPane(playerCardPanes[i], faceDown);
            if (faceDown || i >= playerHand.getCardCount()) {
                playerCardLabels[i].setText("???");
                playerCardLabels[i].setTextFill(Color.web("#aaaaff"));
            } else {
                try {
                    Card c = playerHand.getCard(i);
                    playerCardLabels[i].setText(cardString(c));
                    boolean red = c.getSuit() == Card.Suit.HEARTS || c.getSuit() == Card.Suit.DIAMONDS;
                    playerCardLabels[i].setTextFill(Color.web(red ? "#c0392b" : "#222222"));
                } catch (PokerException e) {}
            }
        }
    }

    /**
     * Refreshes all 5 dealer card panes.
     *
     * @param faceDown true to hide dealer cards, false to reveal them
     */
    private void refreshDealerCards(boolean faceDown) {
        for (int i = 0; i < 5; i++) {
            styleCardPane(dealerCardPanes[i], faceDown);
            if (faceDown || i >= dealerHand.getCardCount()) {
                dealerCardLabels[i].setText("???");
                dealerCardLabels[i].setTextFill(Color.web("#aaaaff"));
            } else {
                try {
                    Card c = dealerHand.getCard(i);
                    dealerCardLabels[i].setText(cardString(c));
                    boolean red = c.getSuit() == Card.Suit.HEARTS || c.getSuit() == Card.Suit.DIAMONDS;
                    dealerCardLabels[i].setTextFill(Color.web(red ? "#c0392b" : "#222222"));
                } catch (PokerException e) {}
            }
        }
    }

    /**
     * Applies face-up or face-down CSS styling to a card StackPane.
     *
     * @param pane the card pane to restyle
     * @param faceDown true for dark-blue back, false for cream face
     */
    private void styleCardPane(StackPane pane, boolean faceDown) {
        pane.setStyle(
                "-fx-background-color: " + (faceDown ? "#1a237e" : "#fdf6e3") + ";" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: #888888;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 6;");
    }

    /**
     * Applies standard styling to a center-row info box.
     *
     * @param box the VBox to style
     */
    private void centerBoxStyle(VBox box) {
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 20, 10, 20));
        box.setStyle(
                "-fx-background-color: #0000004D;" +
                "-fx-border-color: #888888;" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;");
        box.setPrefWidth(140);
    }

    /**
     * Updates all money and deck-count labels from current back-end state.
     */
    private void updateMoneyLabels() {
        deckCountLabel.setText(deck.cardsLeft() + " cards");
        discardCountLabel.setText(deck.getDealtCount() + " dealt");
        balanceLabel.setText("$" + String.format("%,.2f", bettor.getBalance()));
        double playerBet = bettor.getWager(0) + bettor.getWager(1);
        playerBetLabel.setText("$" + String.format("%,.2f", playerBet));
        double dealerBet = Math.max(Bettor.BIG_BLIND, playerBet);
        dealerBetLabel.setText("$" + String.format("%,.2f", dealerBet));
        potLabel.setText("$" + String.format("%,.2f", playerBet + dealerBet));
    }

    /**
     * Enables or disables each button based on the current GamePhase.
     */
    private void updateButtons() {
        dealBtn.setDisable(phase != GamePhase.WAITING && phase != GamePhase.ROUND_OVER);
        callBtn.setDisable(phase != GamePhase.FIRST_BET && phase != GamePhase.SECOND_BET);
        raiseBtn.setDisable(phase != GamePhase.FIRST_BET && phase != GamePhase.SECOND_BET);
        foldBtn.setDisable(phase != GamePhase.FIRST_BET && phase != GamePhase.SECOND_BET);
        drawBtn.setDisable(phase != GamePhase.DISCARD);
        nextBtn.setDisable(phase != GamePhase.ROUND_OVER);
        wagerSpinner.setDisable(phase != GamePhase.FIRST_BET && phase != GamePhase.SECOND_BET);
        for (CheckBox cb : discardBoxes) cb.setDisable(phase != GamePhase.DISCARD);
    }

    /**
     * Posts a message to the game message area.
     *
     * @param msg the message to display
     */
    private void postMessage(String msg) {
        messageLabel.setText(msg);
    }


    /**
     * Creates a card StackPane with the given face-down state and label text.
     *
     * @param faceDown true for a card back
     * @param text the initial label text
     * @return a cool StackPane representing one card
     */
    private StackPane makeCardPane(boolean faceDown, String text) {
        StackPane card = new StackPane();
        card.setPrefSize(75, 105);
        styleCardPane(card, faceDown);
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        lbl.setTextFill(Color.web("#aaaaff"));
        card.getChildren().add(lbl);
        return card;
    }

    /**
     * Builds a small labelled info box for chips or bets.
     * If value is null, no value label is added and the caller adds their own.
     *
     * @param text  the heading label text
     * @param value the value to display, or null
     * @return a cool VBox
     */
    private VBox infoBox(String text, String value) {
        Label h = makeLabel(text, 12, false);
        h.setTextFill(Color.LIGHTGRAY);

        VBox box = new VBox(4, h);
        if (value != null) box.getChildren().add(makeLabel(value, 18, true));
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setStyle(
                "-fx-background-color: rgba(0,0,0,0.4);" +
                "-fx-border-color: #c9a84c;" +
                "-fx-border-width: 1.5;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;");
        return box;
    }

    /**
     * Creates a styled action button.
     *
     * @param text the button label
     * @param color the CSS background colour
     * @return a cool Button
     */
    private Button makeButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        btn.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 9 16;");
        return btn;
    }

    /**
     * Creates a white Label, bold or normal weight.
     *
     * @param text the label text
     * @param size the font size in points
     * @param bold true for bold weight
     * @return a cool Label
     */
    private Label makeLabel(String text, double size, boolean bold) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Georgia",
                bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        lbl.setTextFill(Color.WHITE);
        return lbl;
    }

    /**
     * Converts a Card to a short display string, e.g. "A♥" or "10♠".
     *
     * @param c the card to convert
     * @return a short string representation
     */
    private String cardString(Card c) {
        String[] rankSymbols = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        String[] suitSymbols = {"\u2663", "\u2666", "\u2665", "\u2660"};
        return rankSymbols[c.getRank().ordinal()] + suitSymbols[c.getSuit().ordinal()];
    }

}