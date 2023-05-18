package com.akgames.biriba;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BiribaController {
    //<editor-fold desc="----- FXML injections -----">
    @FXML
    private Pane hbPlayerHand1, hbPlayerHand2, deck, pDiscarded, pBiribakia;
    @FXML
    private ImageView ivDeck, ivBiribaki1, ivBiribaki2;
    @FXML
    private Button btnSort1, btnSort2, btnCancel, btnConfirm, btnStart, btnDiscard, btnUndo, btnNewTriti, btnAddToTriti, btnEndTurn;
    @FXML
    private Label lblStatus, lblPlayerTurn;
    @FXML
    private GridPane pTrites1, pTrites2;
    @FXML
    private AnchorPane aPane;
    //</editor-fold>

    //<editor-fold desc="----- INITIALIZE -----">
    private Game game = new Game();
    private List<Node> allNodes;
    private List<Double> allNodesOpacity = new ArrayList<>();

    @FXML
    public void initialize() {
        diplayBiribakiaAndDeck();
        game.getPlayer(0).sortHandCards();
        game.getPlayer(1).sortHandCards();
        displayHands();
        displayStatus();
        displayDiscarded();
        createListOfNodes();
    }

    public void createListOfNodes(){
        allNodes = Arrays.asList(new Node[] {hbPlayerHand1,hbPlayerHand2,deck,pDiscarded,pBiribakia,
                btnCancel,btnConfirm,btnSort1, btnSort2, btnStart, btnDiscard, btnUndo, btnNewTriti, btnAddToTriti, btnEndTurn,
                lblStatus, lblPlayerTurn,pTrites1, pTrites2});
    }

    public void onStartClick() {
        hideBtn(btnStart);
        game.play("P1-TURN-START");
        displayStatus();
//        unlock(getCurrentPlayer().getHandCards());
        addListeners(getCurrentPlayer().getHandCards());
        unlock(game.getAllDiscardedCards().get(0));
        addListeners(game.getAllDiscardedCards().get(0));
        addListeners(ivDeck,false);
        hbPlayerHand1.setOpacity(0.5);
        hbPlayerHand2.setOpacity(0.5);
        pBiribakia.setOpacity(0.5);
        setCPPTritesPaneLock(true);

    }
    //</editor-fold>

    //<editor-fold desc="----- UTILITIES -----">
    private Player getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    private boolean chooseTriti() {
        return false;
    }

    private void unlock(Card card) {
        card.unlock();
    }
    private void unlock(ArrayList<Card> cards) {
        for (Card card : cards) {
            unlock(card);
        }
    }
    private void lock(Card card) {
        card.lock();
    }
    private void lock(ArrayList<Card> cards) {
        for (Card card : cards) {
            lock(card);
        }
    }

    private void clearDisplay(Pane pane) {
        pane.getChildren().clear();
    }

    private boolean isCPTriresPaneLocked() {
        if (game.getCurrentPlayerIndex() == 0) {
            if(pTrites1.isDisable()) {
                return true;
            }
        } else {
            if (pTrites2.isDisable()) {
                return true;
            }
        } return false;
    }
    private void setCPPTritesPaneLock(Boolean locked) {
        if (game.getCurrentPlayerIndex() == 0) {
            pTrites1.setDisable(locked);
        } else {
            pTrites2.setDisable(locked);
        }
    }

    private boolean cannotSelectMoreThanOne() {
        if (game.inAddTritiMode &&
        getCurrentPlayer().getChoosenHandCards().size() == 1) {
            return true;
        } return false;
    }

    private void switchSelectedCard(Card card) {
        Card oldCard = getCurrentPlayer().getChoosenHandCards().get(0);
        ImageView ivOld = oldCard.getImageView();
        ImageView iv = card.getImageView();

        getCurrentPlayer().clearChosenHandCards();
        getCurrentPlayer().addChosenHandCards(card);

        ivOld.setY(ivOld.getY() + 10);
        oldCard.unselect();
        iv.setY(iv.getY() - 10);
        card.select();
    }

    /* Returns all Cards image views. */
    private ArrayList<ImageView> getAllCardsImageViews() {
        ArrayList<ImageView> ivs = new ArrayList<>();
        for (Player player : game.getAllPlayers()) {
            ivs.addAll(player.getHandCardsIVs());
            ivs.add(ivDeck);
            for (Card dCard : game.getAllDiscardedCards()) {
                ivs.add(dCard.getImageView());
            }
        }
        return ivs;
    }

    private ArrayList<Card> getAllCards() {
        return game.getAllCards();
    }
    //</editor-fold>

    //<editor-fold desc="----- METHODS -----">
    public void updateScene() {
        displayHands();
        displayStatus();
        displayDiscarded();
        displayTrites1();
        displayTrites2();

        if (game.getDeck().isLocked()) {
            ivDeck.setOpacity(0.5);
        } else {
            ivDeck.setOpacity(1);
        }
    }

    public void hideBtn(Button btn) {
        btn.setVisible(false);
    }
    private void showBtn(Button btn) {
        btn.setVisible(true);
    }
    private void lockBtn(Button btn) {
        btn.setDisable(true);
    }
    private void unlockBtn(Button btn) {
        btn.setDisable(false);
    }

    public void addListeners() {
        addListeners(getAllCards());
    }
    /*
        Adds mouse events to all ImageViews of Cards, if game started.
        and no event already set.
     */
    public void addListeners(ArrayList<Card> cards) {
        if(game.getStatusCode().equals("PREGAME")) {return;}

        for (Card card : cards) {
            ImageView iv = card.getImageView();

            if (card.isLocked()) {
                iv.setOnMouseClicked(e -> {});
                iv.setOnMouseEntered(e -> {});
                iv.setOnMouseExited(e -> {});
            } else {
                iv.setOnMouseClicked(e -> onClickHandCard(card));
                iv.setOnMouseEntered(e -> onMouseOver(iv));
                iv.setOnMouseExited(e -> onMouseExited(iv));
            }
        }
    }
    public void addListeners(Card card) {
        ImageView iv = card.getImageView();
        if (card.isLocked()) {
            iv.setOnMouseClicked(e -> {});
            iv.setOnMouseEntered(e -> {});
            iv.setOnMouseExited(e -> {});
        } else {
            iv.setOnMouseClicked(e -> onClickHandCard(card));
            iv.setOnMouseEntered(e -> onMouseOver(iv));
            iv.setOnMouseExited(e -> onMouseExited(iv));
        }
    }
    public void addListeners(ImageView iv, Boolean remove) {
        if (remove) {
            iv.setOnMouseEntered(e -> {});
            iv.setOnMouseExited(e -> {});
        } else {
            iv.setOnMouseEntered(e -> onMouseOver(iv));
            iv.setOnMouseExited(e -> onMouseExited(iv));
        }
    }

    public void setOpacityToAll(double opacity) {
        for(Node node : allNodes) {
            if(node == null){continue;}
            allNodesOpacity.add(node.getOpacity());
            node.setOpacity(opacity);
        }
    }
    public void setOpacityAsBefore() {
        for (int i=0; i < allNodes.size(); i++) {
            if(allNodes.get(i) == null){continue;}
            allNodes.get(i).setOpacity(allNodesOpacity.get(i));
        }
    }

    public Triti selectTriti() {
        showBtn(btnCancel);
        showBtn(btnConfirm);
        // unlock trites
        setCPPTritesPaneLock(false);
        // hide rest
        setOpacityToAll(0.2);
        btnCancel.setOpacity(1);
        btnConfirm.setOpacity(1);
        if (game.getCurrentPlayerIndex() == 0) {
            pTrites1.setOpacity(1);
            hbPlayerHand1.setOpacity(1);
        } else {
            pTrites2.setOpacity(1);
            hbPlayerHand2.setOpacity(1);
        }
        return game.getSelectedTriti();
    }
    //</editor-fold>

    //<editor-fold desc="----- DISPLAYS -----">
    /*
        Displays back image to deck and biribakia.
     */
    private void diplayBiribakiaAndDeck() {
        ivBiribaki1.setImage(Card.getBackImage());
        ivBiribaki2.setImage(Card.getBackImage());
        ivDeck.setImage(Card.getBackImage());
    }
    /*
        Clears the hands. Redraws from players hands
     */
    public void displayHands() {
        clearDisplay(hbPlayerHand1);
        clearDisplay(hbPlayerHand2);

        for (Player player : game.getAllPlayers()) {
            ArrayList<ImageView> ivs = player.getHandCardsIVs();
            int i = 1;
            double spacing = 20;
            for (ImageView iv : ivs) {
                iv.setX(spacing*i);
                i++;
            }
            if (player.getName().equals("Player1")) {
                hbPlayerHand1.getChildren().addAll(ivs);
            } else {
                hbPlayerHand2.getChildren().addAll(ivs);
            }
        }
    }
    /*
        Clears the diplay. Redraws from game discarded list.
    */
    private void displayDiscarded() {
        int i = 1;
        double spacing = 20;
        ArrayList<ImageView> ivs = new ArrayList<>();
        for (Card card : game.getAllDiscardedCards()) {
            ImageView iv = card.getImageView();
            iv.setX(i*spacing);
            ivs.add(iv);
            i++;
        }
        clearDisplay(pDiscarded);
        pDiscarded.getChildren().addAll(ivs);
    }

    /*
        Updates the status display.
    */
    private void displayStatus() {
        lblStatus.setText(game.getStatusMessage());
    }

    public void displayTrites1() {
        clearDisplay(pTrites1);
        int tritiIndx = 0;
        for (Triti triti : game.getPlayer(0).getTrites()) {
            Debug.print("BC.dT1: triti size " + triti.getSize());
            Pane ptriti = new Pane();
            ptriti.setPrefSize(100,400);
            ptriti.setOnMouseClicked(e -> onTritiMouseClick(triti));
            ptriti.setOnMouseEntered(e -> onTritiMouseEntered(ptriti));
            ptriti.setOnMouseExited(e -> onTritiMouseExited(ptriti));

            Label lblTritiScore = new Label();
            triti.calculateScore();
            lblTritiScore.setText("Score: " + triti.getScore());

            ImageView iv = new ImageView();
            int i = 1;
            int spacing = 20;
            if (game.inAddTritiMode) {
                triti.createTritiTemp();
                ArrayList<Card> tt = triti.getTriti();
                Collections.reverse(tt);
                for (Card card : tt) {
                    if(card == null) {continue;}
                    Debug.print("BC.dT1T: card = " + card);
                    iv = card.getImageView();
//                    card.lock();
//                    card.setInTriti(true);
                    iv.setY(i * spacing);
                    iv.setX(0);
                    iv.setScaleX(0.7);
                    iv.setScaleY(0.7);
                    iv.setViewOrder(-i);
                    addListeners(card);
                    if (!ptriti.getChildren().contains(iv)) {
                        ptriti.getChildren().add(iv);
                    }
                    i++;
                }
            } else {
                triti.tritiBuffer.clear();
                triti.tritiTemp.clear();
                ArrayList<Card> t = triti.getTriti();
                Collections.reverse(t);
                for (Card card : t) {
                    if(card == null) {continue;}
                    Debug.print("BC.dT1: card = " + card);
                    iv = card.getImageView();
                    card.lock();
                    card.setInTriti(true);
                    iv.setY(i * spacing);
                    iv.setX(0);
                    iv.setScaleX(0.7);
                    iv.setScaleY(0.7);
                    iv.setViewOrder(-i);
                    addListeners(card);
                    ptriti.getChildren().add(iv);
                    i++;
                }
                Collections.reverse(t);
            }

            pTrites1.add(lblTritiScore, tritiIndx,0);
            pTrites1.add(ptriti,tritiIndx,1);
            tritiIndx++;
        }
    }
    public void displayTrites2() {
        clearDisplay(pTrites2);
        int tritiIndx = 0;
        for (Triti triti : game.getPlayer(1).getTrites()) {
            Debug.print("BC.dT2: triti size " + triti.getSize());
            Pane ptriti = new Pane();
            ptriti.setPrefSize(150,400);
            ptriti.setOnMouseClicked(e -> onTritiMouseClick(triti));
            ptriti.setOnMouseEntered(e -> onTritiMouseEntered(ptriti));
            ptriti.setOnMouseExited(e -> onTritiMouseExited(ptriti));

            Label lblTritiScore = new Label();
            triti.calculateScore();
            lblTritiScore.setText("Score: " + triti.getScore());

            ImageView iv = new ImageView();
            int i = 1;
            int spacing = 20;
            if (game.inAddTritiMode) {
                triti.createTritiTemp();
                ArrayList<Card> tt = triti.getTriti();
                Collections.reverse(tt);
                for (Card card : tt) {
                    if(card == null) {continue;}
                    Debug.print("BC.dT2T: card = " + card);
                    iv = card.getImageView();
//                    card.lock();
//                    card.setInTriti(true);
                    iv.setY(i * spacing);
                    iv.setX(0);
                    iv.setScaleX(0.7);
                    iv.setScaleY(0.7);
                    iv.setViewOrder(-i);
                    addListeners(card);
                    if (!ptriti.getChildren().contains(iv)) {
                        ptriti.getChildren().add(iv);
                    }
                    i++;
                }
            } else {
                triti.tritiBuffer.clear();
                triti.tritiTemp.clear();
                ArrayList<Card> t = triti.getTriti();
                Collections.reverse(t);
                for (Card card : t) {
                    if(card == null) {continue;}
                    Debug.print("BC.dT2: card = " + card);
                    iv = card.getImageView();
                    card.lock();
                    card.setInTriti(true);
                    iv.setY(i * spacing);
                    iv.setX(0);
                    iv.setScaleX(0.7);
                    iv.setScaleY(0.7);
                    iv.setViewOrder(-i);
                    addListeners(card);
                    ptriti.getChildren().add(iv);
                    i++;
                }
                Collections.reverse(t);
            }
            pTrites1.add(lblTritiScore, tritiIndx,1);
            pTrites2.add(ptriti,tritiIndx,0);
            tritiIndx++;
        }
    }

    public void displayBtnDiscarded() {
        Debug.print(String.valueOf(getCurrentPlayer().getChoosenHandCards().size()));
        if (getCurrentPlayer().getChoosenHandCards().size() == 1 &&
                (getCurrentPlayer().hasPickedFromDiscards() ||
                getCurrentPlayer().hasPickedFromDeck())) {
            unlockBtn(btnDiscard);
            btnDiscard.setOpacity(1);
        } else {
            lockBtn(btnDiscard);
            btnDiscard.setOpacity(0.5);
        }
    }
    public void displayBtnAddToTriti() {
        if (getCurrentPlayer().getTrites().size() > 0) {
            unlockBtn(btnAddToTriti);
        } else {
            lockBtn(btnAddToTriti);
        }
    }
    //</editor-fold>

    //<editor-fold desc="----- EVENTS HANDLING -----">
    /*  Click on Card of current player.
        Selects/unselects the card and adds or remove from selected list. */
    private void onClickHandCard(Card card) {
        if (card.isLocked() || card.isDiscard()) {return;}

        if (getCurrentPlayer().canTakeBiribaki()) {
            pBiribakia.setOpacity(1);
            if (game.isBiribakiaLocked()) {
                ivBiribaki1.setOpacity(1);
                ivBiribaki2.setOpacity(0.5);
                game.unlockBiribakia();
            }
            else {
                ivBiribaki1.setOpacity(0.5);
                ivBiribaki2.setOpacity(1);
            }
        }
        else {
            pBiribakia.setOpacity(0.5);
        }

        displayBtnAddToTriti();
        if (cannotSelectMoreThanOne()) {
            switchSelectedCard(card);
            return;
        }
        ImageView iv = card.getImageView();

        if (card.isSelected()) {
            iv.setY(iv.getY() + 10);
            card.unselect();
            getCurrentPlayer().removeChosenHandCards(card);
        } else {
            iv.setY(iv.getY() - 10);
            card.select();
            getCurrentPlayer().addChosenHandCards(card);
        }
        displayBtnDiscarded();
    }
    // --------------------------- Game Mouse Events ------------------------- //
    /* Pick a card from the deck. */
    public void onDeckClick() {
        Debug.print("BC. Deck clicked");
        if (game.getDeck().isLocked()) {
                Debug.print("deck is locked");
            return;}

        unlock(getCurrentPlayer().getHandCards());
        if (game.getCurrentPlayerIndex() == 0) {
            hbPlayerHand1.setOpacity(1);
        } else {
            hbPlayerHand2.setOpacity(1);
        }

        ArrayList<Card> deckTopCard;
        if (game.getCurrentPlayerIndex() == 0) {
            deckTopCard = game.play("P1-TURN-DECK");
        } else {
            deckTopCard = game.play("P2-TURN-DECK");
        }

        addListeners(deckTopCard);
        addListeners(game.getAllDiscardedCards());
        addListeners(ivDeck,true);
        updateScene();
        unlockBtn(btnNewTriti);
    }

    /* Pick discarded cards. */
    public void onDiscardedClick() {
        Debug.print("BC: Discarded cards clicked");
        if (game.isDiscardedLocked()) {
                Debug.print("discarded is locked");
            return;}

        unlock(getCurrentPlayer().getHandCards());
        if (game.getCurrentPlayerIndex() == 0) {
            hbPlayerHand1.setOpacity(1);
        } else {
            hbPlayerHand2.setOpacity(1);
        }

        getCurrentPlayer().clearPickedFromDiscards();
        ArrayList<Card> discarded;
        if (game.getCurrentPlayerIndex() == 0) {
            discarded = game.play("P1-TURN-DISCARDS");
            getCurrentPlayer().setPickedFromDiscards(discarded);
        } else {
            discarded = game.play("P2-TURN-DISCARDS");
            getCurrentPlayer().setPickedFromDiscards(discarded);
        }
        unlockBtn(btnUndo);
        addListeners(discarded);
        addListeners(ivDeck,true);
        updateScene();
        unlockBtn(btnNewTriti);
    }

    /* Discard the selected card. */
    public void onClickBtnDiscard() {
        Debug.print("BC: Discard btn clicked");
        ArrayList<Card> discardedCard;
        if (game.getCurrentPlayerIndex() == 0) {
            if (game.getAllDiscardedCards().size() != 0) {
                discardedCard = game.play("P1-TURN-DECK-END");
            } else {
                discardedCard = game.play("P1-TURN-TRITI-END");
            }
        } else {
            if (game.getAllDiscardedCards().size() != 0) {
                discardedCard = game.play("P2-TURN-DECK-END");
            } else {
                discardedCard = game.play("P2-TURN-TRITI-END");
            }
        }

        addListeners(discardedCard);
        addListeners(game.getAllDiscardedCards());
        addListeners(getCurrentPlayer().getHandCards());
        addListeners(game.getOtherPlayer().getHandCards());
        updateScene();
        unlockBtn(btnUndo);
        displayBtnDiscarded();
        displayBtnAddToTriti();
        unlockBtn(btnEndTurn);
        btnEndTurn.setOpacity(1);
    }

    // Create NEW triti
    public void onClickBtnTriti() {
        Debug.print("BC: Create new Triti btn clicked");
        if (game.getCurrentPlayerIndex() == 0) {
            game.play("P1-TURN-TRITI-START");
        } else {
            game.play("P2-TURN-TRITI-START");
        }

//        displayStatus();
        displayTrites1();
        displayTrites2();
        displayBtnDiscarded();
        lockBtn(btnUndo);
        unlockBtn(btnAddToTriti);
    }

    // Click End Turn btn
    public void onClickBtnEndTurn() {
        Debug.print("BC: End Turn clicked.");
        if (!getCurrentPlayer().isAllowedToEndTurn()) {return;}
        if (game.getCurrentPlayerIndex() == 0) {
            game.play("P1-TURN-END");
        } else {
            game.play("P2-TURN-END");
        }
        addListeners(game.getAllDiscardedCards());
        addListeners(getCurrentPlayer().getHandCards());
        addListeners(game.getOtherPlayer().getHandCards());
        addListeners(ivDeck,false);
        game.setTempDiscarded(null);
        getCurrentPlayer().clearChosenHandCards();
        lockBtn(btnUndo);
        lockBtn(btnEndTurn);
        btnEndTurn.setOpacity(0.5);
        displayBtnAddToTriti();
        updateScene();
        lblPlayerTurn.setText(getCurrentPlayer().getName());
        if (game.getCurrentPlayerIndex() == 0) {
            hbPlayerHand1.setOpacity(0.5);
            hbPlayerHand2.setOpacity(0.5);
            hideBtn(btnSort2);
            showBtn(btnSort1);
        } else {
            hbPlayerHand1.setOpacity(0.5);
            hbPlayerHand2.setOpacity(0.5);
            hideBtn(btnSort1);
            showBtn(btnSort2);
        }
    }

    // Add Card to triti
    public void onClickBtnAddTriti() {
        // for now only one at a time.
        // controled by cannotSelectMoreThanOne
        //TODO: triti reversed when clicked, card not shown
        Debug.print("Add to Triti clicked");
        game.inAddTritiMode = true;
        Triti triti = selectTriti();
        displayTrites1();
        displayTrites2();
        displayBtnDiscarded();
    }

    // Undo Button
    public void onClickBtnUndo() {
        Debug.print("Undo clicked");

        // undo pick from discards
        if (getCurrentPlayer().hasPickedFromDiscards() &&
                (game.getTempDiscarded() == null)) {
            game.unlockDicarded();
            game.getDeck().unlock();
            game.returnDiscards(getCurrentPlayer());
            addListeners(ivDeck, false);
            if (game.getCurrentPlayerIndex() == 0) {
                game.play("P1-TURN-START");
            } else {
                game.play("P2-TURN-START");
            }
        } else

        // undo discard
        if ( game.getStatusCode().equals("P1-TURN-TRITI-END") ||
                game.getStatusCode().equals("P2-TURN-TRITI-END") ||
                game.getStatusCode().equals("P1-TURN-DECK-END")  ||
                game.getStatusCode().equals("P2-TURN-DECK-END") ) {
            Debug.print("undo");
            Card temp = game.returnDiscardedCardTo(getCurrentPlayer());
            game.setTempDiscarded(null);
            addListeners(ivDeck,true);
            addListeners(temp);
            lockBtn(btnEndTurn);
        }
        updateScene();
        lockBtn(btnUndo);
    }

    // sort
    public void onClickBtnSort() {
        getCurrentPlayer().sortHandCards();
        displayHands();
    }
    // --------------------------------------------------------------------------- //

    public void onMouseOver(ImageView iv) {
        if(iv.getId().equals("triti")) {return;}
        iv.setScaleX(1.02);
        iv.setScaleY(1.02);
    }

    public void onMouseExited(ImageView iv) {
        if(iv.getId().equals("triti")) {return;}
        iv.setScaleX(1.0);
        iv.setScaleY(1.0);
    }

    public void onTritiMouseClick(Triti triti) {
        // exit if trites locked
        if (isCPTriresPaneLocked()) {return;}
        // set to null after
        if (triti == null) {
            Debug.print("Triti is null");
        } else {
            Debug.print("Triti selected " + triti.getSuitValue());
        }
        game.setSelectedTriti(triti);
        triti.addToTritiBuffer(getCurrentPlayer().getChoosenHandCards());
        displayTrites1();
        displayTrites2();
    }

    public void onTritiMouseEntered(Pane ptriti) {
        // if triti unlockesed
        if (isCPTriresPaneLocked()) {return;}
        Debug.print("" + ptriti.getScaleX());
        ptriti.setScaleX(1.1);
        ptriti.setScaleY(1.1);
    }
    public void onTritiMouseExited(Pane ptriti) {
        // if triti unlockesed
        if (isCPTriresPaneLocked()) {return;}
        Debug.print("" + ptriti.getScaleX());
        ptriti.setScaleX(1);
        ptriti.setScaleY(1);
    }

    public void onCencelTritiClick() {
        game.inAddTritiMode = false;
        for (Triti triti : getCurrentPlayer().getTrites()) {
            triti.tritiBuffer.clear();
        }
        getCurrentPlayer().clearChosenHandCards();
        setOpacityAsBefore();
        setCPPTritesPaneLock(true);
        hideBtn(btnCancel);
        hideBtn(btnConfirm);
        displayTrites1();
        displayTrites2();
        displayHands();
        displayBtnDiscarded();
    }
    public void onConfirmTritiClick() {
        //TODO:opacity of handcard
        game.inAddTritiMode = false;
        for (Triti triti : getCurrentPlayer().getTrites()) {
            triti.addToTriti(triti.tritiBuffer);
            for (Card card : triti.tritiBuffer) {
                getCurrentPlayer().removeHandCard(card);
            }
        }

        getCurrentPlayer().clearChosenHandCards();
        setOpacityAsBefore();
        setCPPTritesPaneLock(true);
        hideBtn(btnCancel);
        hideBtn(btnConfirm);
        displayTrites1();
        displayTrites2();
        displayBtnDiscarded();

    }
    //</editor-fold>
}
