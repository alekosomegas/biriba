package com.akgames.biriba;

import javafx.scene.image.ImageView;
import java.util.*;

public class Player {
    //<editor-fold desc="----- INITIALIZE -----">

    private ArrayList<Triti> trites;
    private ArrayList<Card> handCards;
    private ArrayList<Card> choosenHandCards;
    private ArrayList<Card> pickedFromDiscards;

    private String name;

    private boolean hasPickedFromDeck;
    private boolean hasPickedFromDiscards;
    private boolean canEndTurn;
    private boolean tritesLocked;

    public Player(String name) {
        this.pickedFromDiscards = new ArrayList<>();
        this.choosenHandCards = new ArrayList<>();
        this.handCards = new ArrayList<>();
        this.trites = new ArrayList<>();
        this.canEndTurn = false;
        this.hasPickedFromDeck = false;
        this.hasPickedFromDiscards = false;
        this.name = name;
        this.tritesLocked = false;
    }
    //</editor-fold>

    //<editor-fold desc="----- METHODS -----">

    // Hand
    public void addHandCard(ArrayList<Card> cards){
        handCards.addAll(cards);
    }
    public void addHandCard(Card card){
        handCards.add(card);
    }
    public void removeHandCard(Card card){
        handCards.remove(card);
    }
    public ArrayList<Card> getHandCards() {
        return handCards;
    }

    public ArrayList<ImageView> getHandCardsIVs() {
        ArrayList<ImageView> returned = new ArrayList<>();
        for (Card card : getHandCards()) {
            returned.add(card.getImageView());
        }
        return returned;
    }

    public void sortHandCards() {
        handCards.sort(Collections.reverseOrder());
    }
    // ----------------------------- //

    // List of Cards chosen from Hand
    public ArrayList<Card> getChoosenHandCards() {
        return choosenHandCards;
    }
    public void removeChosenHandCards(Card card) {
        choosenHandCards.remove(card);
    }
    public void clearChosenHandCards() {
        choosenHandCards.clear();
    }
    public void addChosenHandCards(Card card) {
        if (choosenHandCards.contains(card)) {return;}
        choosenHandCards.add(card);
    }
    // ----------------------------- //

    // Properties
    public String getName() {
        return name;
    }

    public void setHasPickedFromDeck(Boolean picked) {
        this.hasPickedFromDeck = picked;
    }
    public boolean hasPickedFromDeck() {
        return hasPickedFromDeck;
    }

    public void setHasPickedFromDiscards(Boolean picked) {
        this.hasPickedFromDiscards = picked;
    }
    public boolean hasPickedFromDiscards() {
        return hasPickedFromDiscards;
    }
    public void setPickedFromDiscards(ArrayList<Card> discards) {
        this.pickedFromDiscards.addAll(discards);
    }
    public ArrayList<Card> getPickedFromDiscards() {
        return pickedFromDiscards;
    }
    public void clearPickedFromDiscards() {
        pickedFromDiscards.clear();
    }

    public boolean isAllowedToEndTurn() {
        return canEndTurn;
    }
    public void setCanEndTurn(Boolean pred) {
        if (pred) {
            if (hasPickedFromDeck){
                canEndTurn = true;
            } else if(hasPickedFromDiscards) {
                canEndTurn = checkShowTriti();;
            }
        } else {
            canEndTurn = false;
        }
    }

    public boolean isTritesLocked() {
        return tritesLocked;
    }
    public void setTritesLocked(boolean tritesLocked) {
        this.tritesLocked = tritesLocked;
    }
    // ----------------------------- //
    //</editor-fold>

    //<editor-fold desc="----- TRITES -----">
    public ArrayList<Triti> getTrites() {
        return trites;
    }

    public boolean checkShowTriti() {
        Debug.print("P.cST: size = " + pickedFromDiscards.size());

        for (Card card : pickedFromDiscards) {
            Debug.print("P.cST: picked card = " + card);
            if (card.isInTriti()) {
                return true;
            }
        } return false;
    }

//    public boolean createTriti(ArrayList<Card> cards) {
//       if (!isValidTriti(cards)) {return false;}
//        for (Card card : cards) {
//            card.setInTriti(true);
//            card.setDiscard(false);
//            card.lock();
//            card.unselect();
//        }
//        int tritiSuit = 0;
//        for (Card card : cards) {
//            if (card.isNotAJoker()) {
//                tritiSuit = card.getSuitValue();
//                break;
//            }
//        }
//
//        trites.add(new Triti());
//        handCards.removeAll(cards);
//        clearChosenHandCards();
//       return true;
//    }

    public boolean createTriti(ArrayList<Card> cards) {
        Triti tempTriti = new Triti();
        if (tempTriti.createNewTriti(cards)) {
            trites.add(tempTriti);

            for (Card card : cards) {
                card.setInTriti(true);
                card.setDiscard(false);
                card.lock();
                card.unselect();
            }

            handCards.removeAll(cards);
            clearChosenHandCards();
            return true;
        }
        Debug.print("Triti not created");
        return false;
    }

    public boolean addToTriti(Card card, Triti triti) {
        if (!isValidTritiCard(card, triti)) {return false;}
        triti.addToTriti(card);
        return true;
    }

    private boolean isValidTritiCard(Card card, Triti triti) {
        if(card.getSuitValue() != triti.getSuitValue() && card.isNotAJoker()) {
            return false;
        }
        if (card.isAJoker() && !triti.isClean()) {
            return false;
        }
        if ((triti.indexOfBottomCard() - card.getRankValue() != 1) ||
                triti.indexOfTopCard() - card.getRankValue() != -1) {
            return false;
        }
        return true;
    }

//    private boolean isValidTriti(ArrayList<Card> cards) {
//        //TODO: in between joker not working
//        if (cards.size() < 3) {return false;}
//        cards.sort(Collections.reverseOrder());
//        Card prevCard;
//        Card currCard;
//        int i = 0;
//        for (Card card : cards) {
//            if (i==0) {
//                prevCard = card;
//            } else {
//                prevCard = cards.get(i - 1);
//            }
//            currCard = card;
//            i++;
//            if (prevCard.getValue() - currCard.getValue() != 1 &&
//                    prevCard.getValue() != currCard.getValue() &&
//                        (prevCard.isNotAJoker() & currCard.isNotAJoker())) {
//                return false;
//            }
//            // if not a joker and not same value return false
//            if (prevCard.getSuitValue() != currCard.getSuitValue() &&
//                    (prevCard.isNotAJoker() & currCard.isNotAJoker())) {
//                return false;
//            }
//        } return true;
//    }

    public boolean hasClosedTriti() {
        for (Triti triti : trites) {
            if (triti.isClosed()) {
                return true;
            }
        }
        return false;
    }
    public boolean canTakeBiribaki() {
        if (handCards.size() == 0 && hasClosedTriti()) {
            return true;
        }
        return false;
    }

    //</editor-fold>
}
