package com.akgames.biriba;

import java.util.ArrayList;
import java.util.HashMap;

public class Game {
    //<editor-fold desc="----- INITIALIZE -----">

    // Players
    private ArrayList<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;

    // Discarded Cards
    private ArrayList<Card> discardedCards = new ArrayList<>();
    private boolean discardedCardsLocked = true;
    private Card tempDiscarded;

    // Biribakia
    private ArrayList<Card> biribaki1 = new ArrayList<>(10);
    private ArrayList<Card> biribaki2 = new ArrayList<>(10);
    private boolean biribakiaLocked = true;
    private boolean biribaki1Locked = true;
    private boolean biribaki2Locked = true;

    // Messages
    private HashMap<String, String> msgs = new HashMap<>();
    private String statusMessage;
    private String statusCode;

    // Deck
    private Deck deck;
    private ArrayList<Card> allCards;

    // Trites
    private Triti selectedTriti;

    public boolean inAddTritiMode = false;


    public Game() {
        createDeck();
        createPlayers(2);
        createBiribakia();
        createDiscards();
        statusCode = "PREGAME";
        statusMessage = getStatusMessage();

        createMsgs();
        debugTrites();
        play(statusCode);

    }

    private void createPlayers(int numOfPlayers) {
        for (int i=0; i<numOfPlayers; i++) {
            players.add(new Player("Player" + (i+1)));
            players.get(i).addHandCard(deck.getCards(10));
        }
    }

    private void createDiscards() {
        Card topCard = deck.getTopCard();
        topCard.setDiscard(true);
        this.discardedCards.add(topCard);
    }

    private void createBiribakia() {
        biribaki1.addAll(deck.getCards(10));
        biribaki2.addAll(deck.getCards(10));
    }

    private void createDeck() {
        this.deck = new Deck(2,2);
        this.allCards = (ArrayList<Card>) deck.getFullDeck().clone();
        this.deck.shuffleDeck();
    }
    //</editor-fold>

    //<editor-fold desc="----- GAME LOGIC -----">
    private void createMsgs() {
        /*
            Creates the messages / Game logic / game flow.
         */
        msgs.put("PREGAME", "Press start to start the game.");
        /*  0
            Lock everything
            TO: P1-TURN-START
            deck:l    p1hand:l    p2hand:l    discards:l     biribakia:l */

        msgs.put("P1-TURN-START", "Player's 1 Turn : Pick Discards or a card from the Deck");
         /* 1.0
            From: P2-TURN-END or PREGAME.
            Unlock Deck, P1 hand, Discards
            deck:u    p1hand:u    p2hand:l    discards:u     biribakia:l
            TO: P1-TURN-DECK or P1-TURN-DISCARDS */

        // ------------------------OPTION 1: DECK.------------------------------------ //
        msgs.put("P1-TURN-DECK","Card from Deck picked.");
        /*  1.1.0
            FROM: P1-TURN-START
            Card from deck picked.
            Lock Deck, Discards
            deck:l    p1hand:u    p2hand:l    discards:l     biribakia:l
            TO: P1-TURN-DECK-END */
        // --------------------------------------------------------------------------- //

        msgs.put("P1-TURN-DECK-END","Discard a card");
        /*  1.1.1
            FROM: P1-TURN-DECK
            Discard Card.
            deck:l    p1hand:u    p2hand:l    discards:l     biribakia:l
            TO: P1-TURN-END */
        // --------------------------------------------------------------------------- //

        // ------------------------OPTION 2: DISCARDS.-------------------------------- //
        msgs.put("P1-TURN-DISCARDS", "Discards picked.");
        /*  1.2.0
            FROM: P1-TURN-START
            Card from discard picked.
            Lock Deck, Discards
            deck:l    p1hand:u    p2hand:l    discards:l     biribakia:l
            TO: P1-TURN-TRITI-START */

        msgs.put("P1-TURN-TRITI-START", "Show one of the card(s) picked to a triti.");
        /*  1.2.1
            FROM: P1-TURN-DISCARD
            Must select at least on of them and use it in a valid triti,
            either existing or create a new one.
            deck:l    p1hand:u    p2hand:l    discards:l     biribakia:l
            TO: P1-TURN-TRITI-END */

        msgs.put("P1-TURN-TRITI-END", "Triti shown. Discard a card.");
        /*  1.2.2
            FROM: P1-TURN-TRITI-START
            Validated condition. Discard a card.
            deck:l    p1hand:u    p2hand:l    discards:l     biribakia:l
            TO: P1-TURN-END */
        // --------------------------------------------------------------------------- //

        // -----------------------------END TURN 1.----------------------------------- //
        msgs.put("P1-TURN-END", "Player's 1 Turn Ended.");
        /*  1.3
            FROM: P1-TURN-DECK or P1-TURN-DISCARDS.
            Card discarded
            Lock p1hand, wait(1)
            deck:l    p1hand:l    p2hand:l    discards:l     biribakia:l
            TO: P2-TURN-START */
        // --------------------------------------------------------------------------- //

        // *************************************************************************** //

        // ----------------------------START TURN 2.---------------------------------- //
        msgs.put("P2-TURN-START", "Player'2 Turn : Pick Discards or a card from the Deck");
        /*  2.0
            From: P1-TURN-END.
            Unlock Deck, P2 hand, Discards
            deck:u    p1hand:l    p2hand:u    discards:u     biribakia:l
            TO: P2-TURN-DECK or P2-TURN-DISCARDS*/

        // ------------------------OPTION 1: DECK.------------------------------------ //
        msgs.put("P2-TURN-DECK","Card from Deck picked. Discard a card");
        /*  2.1.0
            FROM: P2-TURN-START
            Card from deck picked.
            Lock Deck, Discards
            deck:l    p1hand:l    p2hand:u    discards:l     biribakia:l
            TO: P2-TURN-END */
        // --------------------------------------------------------------------------- //

        msgs.put("P2-TURN-DECK-END","Discard a card");
        /*  2.1.1
            FROM: P2-TURN-DECK
            Discard Card.
            deck:l    p1hand:l    p2hand:u    discards:l     biribakia:l
            TO: P2-TURN-END */
        // --------------------------------------------------------------------------- //

        // ------------------------OPTION 2: DISCARDS.-------------------------------- //
        msgs.put("P2-TURN-DISCARDS", "Discards picked.");
        /*  2.2.0
            FROM: P2-TURN-START
            Card from discard picked.
            Lock Deck, Discards
            deck:l    p1hand:l    p2hand:u    discards:l     biribakia:l
            TO: P2-TURN-TRITI-START */

        msgs.put("P2-TURN-TRITI-START", "Show one of the card(s) picked to a triti.");
        /*  2.2.1
            FROM: P2-TURN-DISCARD
            Must select at least on of them and use it in a valid triti,
            either existing or create a new one.
            deck:l    p1hand:l    p2hand:u    discards:l     biribakia:l
            TO: P2-TURN-TRITI-END */

        msgs.put("P2-TURN-TRITI-END", "Triti shown. Discard a card.");
        /*  2.2.2
            FROM: P2-TURN-TRITI-START
            Validated condition.
            deck:l    p1hand:l    p2hand:u    discards:l     biribakia:l
            TO: P2-TURN-END */
        // --------------------------------------------------------------------------- //

        // -----------------------------END TURN 2.----------------------------------- //
        msgs.put("P2-TURN-END", "Player's 2 Turn Ended.");
        /*  2.3
            FROM: P2-TURN-DECK or P2-TURN-TRITI-END.
            Card discarded.
            Lock p2hand, wait(1)
            deck:l    p1hand:l    p2hand:l    discards:l     biribakia:l
            TO: P1-TURN-START */
        // --------------------------------------------------------------------------- //
    }
    //</editor-fold>

    //<editor-fold desc="----- PROPERTIES METHODS -----">

    // Deck.
    public Deck getDeck() {
        return deck;
    }
    public ArrayList<Card> getAllCards() {
        return allCards;
    }
    // ----------------------------- //

    // Players
    public Player getPlayer(int index) {
        return players.get(index);
    }
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }
    public ArrayList<Player> getAllPlayers() {
        return players;
    }
    public Player getOtherPlayer() {
        return getPlayer((getCurrentPlayerIndex() + 1) % 2);
    }
    // ----------------------------- //

    // Hand Cards
    //TODO: check if locking the playerhand will block the mouse events on the cars
    //      thus avoiding adding and removing events on cards.
    public void lockHandCards(Player player) {
        for (Card card : player.getHandCards()) {
            card.lock();
        }
    }
    public void unlockHandCards(Player player) {
        for (Card card : player.getHandCards()) {
            card.unlock();
        }
    }

    // ----------------------------- //

    // Discarded
    public ArrayList<Card> getAllDiscardedCards() {
        return discardedCards;
    }
    public void addToDiscarded(Card card) {
        discardedCards.add(card);
    }
    public void addToDiscarded(ArrayList<Card> cards) {
        discardedCards.addAll(cards);
    }
    public boolean isDiscardedLocked() {
        return discardedCardsLocked;
    }
    public void lockDicarded() {
        discardedCardsLocked = true;
    }
    public void unlockDicarded() {
        discardedCardsLocked = false;
    }

    public void setTempDiscarded(Card card) {
        tempDiscarded = card;
    }
    public Card getTempDiscarded() {
        return tempDiscarded;
    }
    // ----------------------------- //

    // Biribakia
    public boolean isBiribakiaLocked() {
        return biribakiaLocked;
    }
    public void unlockBiribakia() {
        biribakiaLocked = false;
    }
    public void lockBiribakia() {
        biribakiaLocked = true;
    }
    // ----------------------------- //

    // Status
    public String getStatusMessage() {
        return statusMessage;
    }
    public String getStatusCode() {
        return statusCode;
    }
    public void setStatusMessage(String code) {
        statusCode = code;
        statusMessage = msgs.get(code);
    }
    // ----------------------------- //

    // Trites
    public void setSelectedTriti(Triti triti) {
        selectedTriti = triti;
    }
    public Triti getSelectedTriti() {
        return selectedTriti;
    }
    // ----------------------------- //

    //</editor-fold>

    //<editor-fold desc="----- METHODS -----">
    public Card discardACard(Player player) {
        Card card = player.getChoosenHandCards().iterator().next();
        player.clearChosenHandCards();
        addToDiscarded(card);
        player.removeHandCard(card);
        card.setDiscard(true);
        card.unselect();
        card.getImageView().setY(0);
        card.lock();
        setTempDiscarded(card);
        return card;
    }

    public void returnDiscards(Player player) {
        for (Card card : player.getPickedFromDiscards()) {
            card.setDiscard(true);
            card.unselect();
            player.removeHandCard(card);
        }
        addToDiscarded(player.getPickedFromDiscards());
        player.clearPickedFromDiscards();
        player.setHasPickedFromDiscards(false);

    }
    public Card returnDiscardedCardTo(Player player) {
        tempDiscarded.unselect();
        tempDiscarded.setDiscard(false);
        tempDiscarded.unlock();
        player.addHandCard(tempDiscarded);
        discardedCards.remove(tempDiscarded);
        return tempDiscarded;
    }

    public ArrayList<Card> emptyDiscards() {
        ArrayList<Card> discards = new ArrayList<>(discardedCards);
        getCurrentPlayer().addHandCard(discardedCards);
        discardedCards.clear();
        for (Card card : discards) {
            card.unselect();
            card.setDiscard(false);
            card.unlock();
        }
        return discards;
    }

    private Card pickACardFromDeck() {
        Card deckTopCard = deck.getTopCard();
        getCurrentPlayer().addHandCard(deckTopCard);
        deckTopCard.unlock();
        deckTopCard.unselect();
        return deckTopCard;
    }

    public void changeTurn() {
        currentPlayerIndex =
                (currentPlayerIndex + 1) % players.size();
    }
    public void endTurn() {
        Debug.print("G.endTurn");
        lockHandCards(getCurrentPlayer());
        unlockHandCards(getOtherPlayer());
        changeTurn();
    }
    //</editor-fold>

    //<editor-fold desc="----- UTILITIES -----">
    private void lockDiscardedCards() {
        for (Card card : getAllDiscardedCards()) {
            card.lock();
        }
    }
    private void unlockDiscardedCards() {
        for (Card card : getAllDiscardedCards()) {
            card.unlock();
        }
    }

    private ArrayList<Card> cardToArray(Card card) {
        ArrayList<Card> cardArrayList = new ArrayList<>();
        cardArrayList.add(card);
        return cardArrayList;
    }
    //</editor-fold>

    //<editor-fold desc="----- PLAY -----">
    public ArrayList<Card> play(String state) {
        //TODO: use enum and chack if valid
        statusCode = state;
        setStatusMessage(state);
        switch (state) {
            case "PREGAME":
                Debug.print("G.Play: state = " + state);
                unlockHandCards(getCurrentPlayer());
                lockHandCards(getOtherPlayer());
                deck.lock();
                lockDicarded();
                break;

            // PLAYER 1
            case "P1-TURN-START":
                Debug.print("G.Play: state = " + state);
                getOtherPlayer().setHasPickedFromDeck(false);
                getOtherPlayer().setHasPickedFromDiscards(false);
                getOtherPlayer().setCanEndTurn(false);
                unlockDiscardedCards();
                deck.unlock();
                unlockDicarded();
                break;

            case "P1-TURN-DECK":
                Debug.print("G.Play: state = " + state);
                deck.lock();
                lockDiscardedCards();
                lockDicarded();
                getCurrentPlayer().setHasPickedFromDeck(true);
                getCurrentPlayer().setCanEndTurn(true);
                return cardToArray(pickACardFromDeck());

            case "P1-TURN-DECK-END":
                Debug.print("G.Play: state = " + state);
                return cardToArray(discardACard(getCurrentPlayer()));

            case "P1-TURN-DISCARDS":
                Debug.print("G.Play: state = " + state);
                deck.lock();
                lockDicarded();
                getCurrentPlayer().setHasPickedFromDiscards(true);
                return emptyDiscards();

            case "P1-TURN-TRITI-START":
                Debug.print("G.Play: state = " + state);
                ArrayList<Card> choosenCards = getCurrentPlayer().getChoosenHandCards();
                boolean success = getCurrentPlayer().createTriti(choosenCards);
                if (!success) {
                    statusMessage = "Not a valid Triti.";
                }
                Debug.print("G.Play: trites number = " + getCurrentPlayer().getTrites().size());
                break;

            case "P1-TURN-TRITI-END":
                Debug.print("G.Play: state = " + state);
                getCurrentPlayer().setCanEndTurn(true);
                return cardToArray(discardACard(getCurrentPlayer()));

            case "P1-TURN-END":
                Debug.print("G.Play: state = " + state);
                if (getCurrentPlayer().isAllowedToEndTurn()) {
                    endTurn();
                    play("P2-TURN-START");
                } else {
                    Debug.print("G.Play: NOT allowed to end turn.");
                }
                break;

            // PLAYER 2
            case "P2-TURN-START":
                Debug.print("G.Play: state = " + state);
                getOtherPlayer().setHasPickedFromDeck(false);
                getOtherPlayer().setHasPickedFromDiscards(false);
                getOtherPlayer().setCanEndTurn(false);
                unlockDiscardedCards();
                deck.unlock();
                unlockDicarded();
                break;

            case "P2-TURN-DECK":
                Debug.print("G.Play: state = " + state);
                deck.lock();
                lockDiscardedCards();
                lockDicarded();
                getCurrentPlayer().setHasPickedFromDeck(true);
                getCurrentPlayer().setCanEndTurn(true);
                return cardToArray(pickACardFromDeck());

            case "P2-TURN-DECK-END":
                Debug.print("G.Play: state = " + state);
                return cardToArray(discardACard(getCurrentPlayer()));

            case "P2-TURN-DISCARDS":
                Debug.print("G.Play: state = " + state);
                deck.lock();
                lockDicarded();
                getCurrentPlayer().setHasPickedFromDiscards(true);
                return emptyDiscards();

            case "P2-TURN-TRITI-START":
                Debug.print("G.Play: state = " + state);
                ArrayList<Card> choosenCards2 = getCurrentPlayer().getChoosenHandCards();
                boolean success2 = getCurrentPlayer().createTriti(choosenCards2);
                if (!success2) {
                    statusMessage = "Not a valid Triti.";
                }
                break;

            case "P2-TURN-TRITI-END":
                Debug.print("G.Play: state = " + state);
                getCurrentPlayer().setCanEndTurn(true);
                return cardToArray(discardACard(getCurrentPlayer()));

            case "P2-TURN-END":
                Debug.print("G.Play: state = " + state);
                if (getCurrentPlayer().isAllowedToEndTurn()) {
                    endTurn();
                    play("P1-TURN-START");
                } else {
                    Debug.print("G.Play: NOT allowed to end turn.");
                }
                break;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="----- DEBUG -----">
    public void debugTrites() {
        ArrayList<Card> tr = new ArrayList<>();
        tr.add(new Card(0));
        tr.add(new Card(1));
        tr.add(new Card(2));
        tr.add(new Card(14));
        tr.add(new Card(13));
        tr.add(new Card(-1));


        players.get(0).getHandCards().addAll(tr);
        allCards.addAll(tr);
    }

    //TODO: select triti
    //TODO: triti complete
    //TODO: game flow

    //TODO: biribaki
    //TODO: game end conditions
    //TODO: score
    //</editor-fold>
}



