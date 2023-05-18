package com.akgames.biriba;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

//TODO: use inheritance for special cards
public class Card implements Comparable<Card> {
    //<editor-fold desc="----- INITIALIZE -----">

    private int suit, rank, value;
    public int suitJ, rankJ, valueJ;
    private boolean isJoker, isJoker2, isActiveJoker2, isAce;
    private final String[] verbose_suit = {"Diamond", "Club", "Heart", "Spade"};
    private final String[] verbose_rank = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private final String[] suit_symbol = {"\u2662", "\u2663", "\u2661", "\u2660"};

    private static Image backImage = new Image("file:src/main/resources/img/back.png");
    private ImageView imageView = new ImageView();
    private Image cardImage;

    private boolean selected = false;
    private boolean locked = true;
    private boolean discard = false;
    private boolean inTriti = false;

    private int score;

    // value should be 0-51, -1 for joker
    public Card(int value){
        this.value = value;
        if(0 <= value && value <= 51){
            suit = value/13;
            rank = value%13;
            isJoker = false;
            if (rank == 1) {
                isJoker2 = true;
                suitJ = suit;
                rankJ = rank;
                valueJ = value;
                suit = -2;
                rank = -2;
                this.value = -2;
                isActiveJoker2 = true;
            }

            else if (rank == 0) {
                isAce = true;
                isJoker2 = false;
            }
            else {isJoker2 = false;}
        }
        else if(value == -1){
            suit = -1;
            rank = -1;
            isJoker = true;
            isJoker2 = false;
        }
        setScoreValue();
    }

    private void setScoreValue() {
        if (isJoker) {score = 20; return;}
        if (isJoker2) {score = 25; return;}
        if (isAce) {score = 15; return;} // ace
        if (rank < 7) {score = 5; return;} // less than 8
        score = 10; // rest, more than 87
    }
    //</editor-fold>

    //<editor-fold desc="----- METHODS -----">
    @Override
    public int compareTo(Card c){
        return this.value - c.value;
    }

    @Override
    public String toString(){
        if (isJoker) {return "Joker";}
        if (isJoker2) {return suit_symbol[suitJ]+verbose_rank[rankJ];}
        return suit_symbol[suit]+verbose_rank[rank%13];
    }
    //</editor-fold>

    //<editor-fold desc="----- PROPERTIES METHODS -----">

    // Suit, Rank
    public String getSuit(){
        if (isJoker) {return "Joker";}
        if (isJoker2) {return verbose_rank[suitJ];}
        return verbose_suit[suit];
    }
    public int getSuitValue(){
        return suit;
    }
    public String getRank(){
        if (isJoker) {return "Joker";}
        if (isJoker2) {return verbose_rank[rankJ];}
        return verbose_rank[rank%13];
    }
    public int getRankValue(){
        return rank+1;
    }
    public void setRank(int rank) {
        this.rank = rank-1;
    }
    public int getValue() {
        return value;
    }
    public void setValue(int val) {
        this.value = val;
    }
    public void setSuit(int suit) {
        this.suit = suit;
    }
    public void setActiveJoker2(boolean bool) {
        isActiveJoker2 = bool;
    }
    public boolean getIsActiveJoker2() {
        return isActiveJoker2;
    }
    public boolean getIsJoker2() {
        return isJoker2;
    }
    // ----------------------------- //

    // Image
    public Image getImage(){
        // load card image only when needed
        if (isJoker) {
            cardImage = new Image("file:src/main/resources/img/joker.png");
        }
        else if (isJoker2) {
            cardImage = new Image(("file:src/main/resources/img/" +
                    verbose_rank[rankJ] + verbose_suit[suitJ] + ".png").toLowerCase());
        }
        else {
            cardImage = new Image(("file:src/main/resources/img/" +
                    verbose_rank[rank%13] + verbose_suit[suit] + ".png").toLowerCase());
        }
        return cardImage;
    }
    public ImageView getImageView() {
        imageView.setImage(getImage());
        imageView.setFitWidth(130);
        imageView.setPreserveRatio(true);
        imageView.setId("unselected");
        return imageView;
    }
    public static Image getBackImage(){
        return backImage;
    }
    // ----------------------------- //

    // Properties
    public boolean isLocked() {
        return locked;
    }
    public void unlock() {
        locked = false;
    }
    public void lock() {
        locked = true;
    }

    public boolean isSelected() {
        return selected;
    }
    public void select() {
        selected = true;
    }
    public void unselect() {
        selected = false;
    }

    public boolean isDiscard() {return discard;}
    public void setDiscard(Boolean discard) {
        this.discard = discard;
    }

    // Score
    public int getScore() {
        return score;
    }
    // ----------------------------- //

    // Triti Properties
    public void setInTriti(Boolean inTriti) {this.inTriti = inTriti;}
    public boolean isInTriti() {return inTriti;}

    public boolean isAJoker() {
        return (isJoker || isActiveJoker2);
    }

    public boolean isNotAJoker() {
        return !(isJoker || isActiveJoker2);
    }

    //</editor-fold>
}
