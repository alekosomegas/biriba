package com.akgames.biriba;

import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.Random;

public class Deck {
    //<editor-fold desc="----- INITIALIZE -----">

    private ArrayList<Card> fullDeck;
    private boolean locked;
    private Image image;

    public Deck(int numOFDecks, int numOfJokers){
        this.fullDeck = new ArrayList<>();
        this.locked = true;
        for (int i=0; i<52*numOFDecks; i++) {
            fullDeck.add(new Card(i%52));
        }
        for (int i=0; i < numOfJokers; i++) {
            fullDeck.add(new Card(-1));
        }
    }
    //</editor-fold>

    //<editor-fold desc="----- PROPERTIES -----">
    public ArrayList<Card> getCards(int numOfCards) {
        ArrayList<Card> returned = new ArrayList<>();
        for (int i =0; i<numOfCards; i++) {
            returned.add(fullDeck.remove(0));
        } return returned;
    }

    public ArrayList<Card> getFullDeck() {return fullDeck;}
    public Card getTopCard() {return fullDeck.remove(0);}
    public int getSize() {return fullDeck.size();}

    public boolean isLocked() {return locked;}
    public void unlock() {locked = false;}
    public void lock() {locked = true;}
    //</editor-fold>

    //<editor-fold desc="----- METHODS -----">
    public void shuffleDeck() {
        Random rand = new Random();
        for(int i = 0; i < getSize()-1; i++){
            int r = rand.nextInt(getSize());
            Card temp = fullDeck.get(i);
            fullDeck.set(i, fullDeck.get(r));
            fullDeck.set(r, temp);
        }
    }
    //</editor-fold>
}

