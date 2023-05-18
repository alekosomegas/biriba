package com.akgames.biriba;
// TODO: Redo the card insert to triti.
//       First add all to indeces/boxes in buffer.
//       Then Check. change joker rank if needed.



import java.util.ArrayList;
import java.util.Collections;

public class Triti {
    private ArrayList<Card> triti = new ArrayList<>(14);
    public ArrayList<Card> tritiBuffer = new ArrayList<>();
    public ArrayList<Card> tritiTemp = new ArrayList<>();
    private int suitValue;
    private boolean isClean;
    private int score;
    private boolean isClosed;
    private Card bufferJoker;
    private Card tritiJoker;

    public Triti() {
        for (int i=0;i<14;i++) {
            triti.add(null);
        }


//        addToTriti(cards);
//        if (suitValue > 0) {
//        this.suitValue = suitValue;}
//        checkIfClosed();
//        checkIfClean();
    }

    public ArrayList<Card> getTriti() {
        return triti;
    }
    public void addToTriti(ArrayList<Card> cards) {
//        triti.addAll(cards);
        for (Card card : cards) {
            addToTriti(card);
        }
    }

    public void addToTritiBuffer(Card card) {
        tritiBuffer.add(card);
    }
    public void addToTritiBuffer(ArrayList<Card> cards) {
        tritiBuffer.addAll(cards);
    }
    public void addToTriti(Card card) {
        //TODO: joker index
//        triti.add(card);
        if (card == null) {return;}
        Debug.print("Rank: " + card.getRankValue());
        triti.set(card.getRankValue()-1, card);

    }
    public int getSuitValue() {
        return suitValue;
    }

    public boolean isClean() {
        return isClean;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void checkIfClean() {
        for (Card card: triti) {
            if (card == null) {continue;}
            if (card.isAJoker()) {isClean = false;}
            else {isClean = true;}
        }
    }

    public void checkIfClosed() {
        int nulls = Collections.frequency(triti, null);
        if (triti.size() - nulls < 7) {
            this.isClosed = false;
        } else {this.isClosed = true;}
    }

    public void calculateScore() {
        score = 0;
        for (Card card : triti) {
            if(card == null) {continue;}
            score += card.getScore();
        }
        if (isClosed) {
            score += 100;
            if (isClean) {
                score += 200;
            }
        }

    }
    public int getScore() {
        return score;
    }

    public void createTritiTemp() {
        tritiTemp.addAll(triti);
        tritiTemp.addAll(tritiBuffer);
//        tritiTemp.sort(Collections.reverseOrder());
    }
    public ArrayList<Card> getTritiTemp() {
        return tritiTemp;
    }

    public int indexOfTopCard() {
        int top = 0;
        for (Card card : triti) {
            if (card == null) {continue;}
            if (card.getRankValue() > top) {
                top = card.getRankValue();
            }
        }
        return top;
    }

    public int indexOfBottomCard() {
        int bottom = 15;
        for (Card card : triti) {
            if (card == null) {continue;}
            if (card.getRankValue() < bottom) {
                bottom = card.getRankValue();
            }
        }
        return bottom;
    }

    public int getSize() {
        int size= 0;
        for (Card card : triti) {
            if (card == null) {continue;}
            size ++;
        }
        return size;
    }

    // Adds cards to a buffer first and check them, If valid adds them
    // to the triti list and returns true.
    public boolean createNewTriti(ArrayList<Card> cards) {
        tritiBuffer.clear();
        addToTritiBuffer(cards);
        if (checkBuffer()) {
            addToTriti(cards);
            tritiBuffer.clear();
            tritiJoker = bufferJoker;
            bufferJoker = null;
            checkIfClosed();
            checkIfClean();
            return true;
        }
        tritiBuffer.clear();
        bufferJoker = null;
        return false;
    }

    private boolean checkBuffer() {
        if (tritiBuffer.size() < 3) {
            Debug.print("size less than 3");
            return false;}

        joker2Condition();
        aceCondition();

        int numOfJokers = containsNumOfJokers(tritiBuffer);
        if (numOfJokers > 1) {
            Debug.print("more than 1 joker");
            return false;}

        if (numOfJokers == 1) {
            tritiBuffer.remove(bufferJoker);
        }

        tritiBuffer.sort(Collections.reverseOrder());


        if (isValidTriti(tritiBuffer)) {
            return true;
        } else {
            for (Card two : gatherTwos()) {
                    two.setActiveJoker2(true);
            } return false;}
    }

    private void aceCondition() {
        Card ace = contains(tritiBuffer, 1);
        boolean containsQorK = (contains(tritiBuffer, 13) != null ||
                                contains(tritiBuffer, 12) != null);
        boolean containsBothQandK = ( contains(tritiBuffer, 13) != null &&
                                       contains(tritiBuffer, 12) != null );
        if (ace != null) {
            // King and Queen
            if ( containsBothQandK ||
                 (bufferJoker != null && containsQorK) ) {
                ace.setRank(14);
                ace.setValue(100); //always top
            }
        }
    }

    private Card contains(ArrayList<Card> array, int rank) {
        for (Card card : array) {
            if (card.getRankValue() == rank) {
                return card;
            }
        }
        return null;
    }
    private int containsNumOfJokers(ArrayList<Card> array) {
        int numOfJokers = 0;
        for (Card card : array) {
            if (card.isAJoker()) {
                Debug.print("joker: " + card);
                Debug.print("act: " + card.getIsActiveJoker2());
                numOfJokers++;
                bufferJoker = card;
            }
        }
        return numOfJokers;
    }

    private ArrayList<Card> gatherTwos() {
        ArrayList<Card> jokerTwos = new ArrayList<>();
        for (Card card : tritiBuffer) {
            if (card.getIsJoker2()) {
                jokerTwos.add(card);
            }
        }
        return jokerTwos;
    }

    private void joker2Condition() {
        ArrayList<Card> jokerTwos = gatherTwos();
        Card three, four, ace;
        three = contains(tritiBuffer, 3);
        four = contains(tritiBuffer, 4);
        ace = contains(tritiBuffer, 1);

        if ((three != null && four != null) &&
            three.getSuitValue() == four.getSuitValue()) {
            for (Card two : jokerTwos) {
                if (two.suitJ == three.getSuitValue()) {
                    two.setActiveJoker2(false);
                    two.setActiveJoker2(false);
                    two.setSuit(two.suitJ);
                    two.setRank(two.rankJ+1);
                    two.setValue(two.valueJ);
                }
            }
        }
        else if ((ace != null && three != null) &&
                ace.getSuitValue() == three.getSuitValue()) {
            for (Card two : jokerTwos) {
//                Debug.print("card " + two);
//                Debug.print("two " + two.getSuitValue());
//                Debug.print("three " + three.getSuitValue());
//                Debug.print("two j " + two.suitJ);
                if (two.suitJ == three.getSuitValue()) {
                    two.setActiveJoker2(false);
                    two.setSuit(two.suitJ);
                    two.setRank(two.rankJ+1);
                    two.setValue(two.valueJ);
                    Debug.print("two joker act " + two.getIsActiveJoker2());
                }
            }
        }
    }


    private boolean isValidTriti(ArrayList<Card> cards) {
        // No jokers allowed, Ace at top

        int suitValue = cards.get(0).getSuitValue();
        int topCardRank = cards.get(0).getRankValue();
        int bottomCardRank = topCardRank;
        Card prevCard;
        Card currCard;
        int i = 0;
        if (suitValue < 0) {
            Debug.print("suit value");
            return false;}

        for (Card card : cards) {
//            if (card.isAJoker() || card.getIsActiveJoker2()) {
//                card.setSuit(suitValue);
//            }
            if (card.getSuitValue() != suitValue) {
                Debug.print("different suit");
                return false;}

            if (i==0) {
                prevCard = card;
            } else {
                prevCard = cards.get(i - 1);
            }
            currCard = card;
            bottomCardRank = card.getRankValue();
            i++;
            if(currCard.equals(prevCard)) {continue;}

            if (prevCard.getRankValue() - currCard.getRankValue() > 2 ||
                    prevCard.getRankValue() - currCard.getRankValue() == 0) {
                Debug.print("same card or more than 2 difference");
                Debug.print("p " + prevCard + " pr " + prevCard.getRankValue());
                Debug.print("c " + currCard + " cr " + currCard.getRankValue());
                return false;}
            if (prevCard.getRankValue() - currCard.getRankValue() == 2) {
                if (bufferJoker != null) {
                    bufferJoker.setSuit(suitValue);
                    bufferJoker.setRank(currCard.getRankValue() + 1);
                    bufferJoker.setValue(currCard.getValue() + 1);
                    bufferJoker = null;
                } else {
                    Debug.print("no joker for 2 difference");
                    return false;}
            }
        }

        if (bufferJoker != null) {
            bufferJoker.setSuit(suitValue);
            // full except 2
            if (topCardRank == 14 && bottomCardRank == 3) {

                bufferJoker.setRank(2);
                bufferJoker.setValue(suitValue * 13 + 1); // value for 2
                bufferJoker = null;
            }
            if (topCardRank == 13 && bottomCardRank == 2) {
                bufferJoker.setRank(1);
                bufferJoker.setValue(suitValue * 13 ); // value for 1
                bufferJoker = null;
            }
            if (topCardRank < 13) {
                bufferJoker.setRank(topCardRank+1);
                bufferJoker.setValue(suitValue * 13 + topCardRank);
                bufferJoker = null;
            }
        }
        cards.sort(Collections.reverseOrder());
        return true;
    }

}
