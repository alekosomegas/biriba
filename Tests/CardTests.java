//import com.akgames.biriba.Card;
//import com.akgames.biriba.Deck;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class CardTests {
//    @Test
//    public void basicCardTest() {
//        Card card = new Card(0);
//        assertEquals(0, card.getSuitValue());
//
//        Card card1 = new Card(0);
//        assertEquals(0, card1.getSuitValue());
//        assertEquals("Diamond", card1.getSuit());
//        assertEquals("A", card1.getRank());
//        assertEquals(1, card1.getRankValue());
//
//        Card card2 = new Card(51);
//        assertEquals(3, card2.getSuitValue());
//        assertEquals("Spade", card2.getSuit());
//        assertEquals("K", card2.getRank());
//        assertEquals(13, card2.getRankValue());
//
//        Card joker = new Card(-1);
//        assertEquals(-1, joker.getSuitValue());
//        assertEquals("Joker", joker.getSuit());
//        assertEquals("Joker", joker.getRank());
//        assertEquals(0, joker.getRankValue());
//    }
//
//    @Test
//    public void compareCardTest() {
//        Card card1 = new Card(10);
//        Card card2 = new Card(22);
//        assertEquals(1,card1.compareTo(card2));
//
//    }
//
//    @Test
//    public void deckTest() {
//        Deck deck = new Deck(2, 2);
//        for (int i=0; i<52*2; i++) {
//            assertEquals(i%52/13, deck.getFullDeck()[i].getSuitValue());
//            assertEquals(i%52%13+1, deck.getFullDeck()[i].getRankValue());
//        }
//        assertEquals("Joker", deck.getFullDeck()[104].toString());
//        assertEquals("Joker", deck.getFullDeck()[105].toString());
//    }
//
//    @Test
//    public void deckShufflePrintTest() {
//        Deck deck = new Deck(2, 2);
//        deck.shuffleDeck();
//        for (int i=0; i<52*2+2; i++) {
//            System.out.println(deck.getFullDeck()[i].toString());
//        }
//    }
//}