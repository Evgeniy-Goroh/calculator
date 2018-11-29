package poker;

import java.util.Arrays;
import java.util.Collections;

public class PokerHand implements Comparable<PokerHand> {
    public static final int NUM_CARDS = 5;

    public enum Strength {
        HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT, FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH
    }

    /**
     * array of cards sorted descending 
     */
    public Card[] cards;

    protected Strength strength = null;

    /**
     * <b>int strenghtValue</b> has 4 bits per relevant card<br>
     * <br>
     * It is used to differentiate between two hands of the same strength<br>
     * <br>
     * For example if the hand strength == TWO_PAIR<br>
     * c0 will be the top pair and c1 will be the bottom pair
     * <table border="0" bordercolor="#FFCC00" style="background-color:#FFFFCC" width="400" cellpadding="3" cellspacing="3">
     * <tr>
     * <td>strenghtValue</td>
     * <td>0000</td>
     * <td>0000</td>
     * <td>0000</td>
     * <td>0000</td>
     * <td>0000</td>
     * <td>0000</td>
     * <td>0000</td>
     * <td>0000</td>
     * </tr>
     * <tr>
     * <td>index</td>
     * <td></td>
     * <td></td>
     * <td></td>
     * <td>c0</td>
     * <td>c1</td>
     * <td>c2</td>
     * <td>c3</td>
     * <td>c4</td>
     * </tr>
     * </table>
     * <br>
     * Where c0 is the most important rank
     */
    private int strengthValue = 0;
    private static final int[] MASK_CARD = { 0x000F0000, 0x0000F000,
            0x00000F00, 0x000000F0, 0x0000000F };

    public PokerHand(Card c1, Card c2, Card c3, Card c4, Card c5) {
        cards = new Card[NUM_CARDS];
        this.cards[0] = c1;
        this.cards[1] = c2;
        this.cards[2] = c3;
        this.cards[3] = c4;
        this.cards[4] = c5;
        Arrays.sort(cards,Collections.reverseOrder());
        evaluateSrenght();
    }

    public PokerHand(int c1, int c2, int c3, int c4, int c5) {
        cards = new Card[NUM_CARDS];
        this.cards[0] = new Card(c1);
        this.cards[1] = new Card(c2);
        this.cards[2] = new Card(c3);
        this.cards[3] = new Card(c4);
        this.cards[4] = new Card(c5);
        Arrays.sort(cards,Collections.reverseOrder());
        evaluateSrenght();
    }

    public int compareTo(PokerHand hand) {
        if (this.getStrength().compareTo(hand.getStrength()) > 0)
            return 1;
        if (this.getStrength().compareTo(hand.getStrength()) < 0)
            return -1;

        if (strengthValue > hand.strengthValue)
            return 1;
        if (strengthValue < hand.strengthValue)
            return -1;

        return 0;
    }

    public static int compare(PokerHand h1, PokerHand h2) {
        return h1.compareTo(h2);
    }

    public Strength getStrength() {
        return strength;
    }

    public int getStrenghtValue(int index) 
        throws IndexOutOfBoundsException{

        if (!( index>=0 && index<=4) )
            throw new IndexOutOfBoundsException("Index should be between 0 and 4");

        return (strengthValue | MASK_CARD[index]) >> ((5 - index) * 4);
    }

    /**
     * should be called only once per index
     * 
     * @param index
     * <br>
     *            <table border="0" bordercolor="#FFCC00" style="background-color:#FFFFCC" width="400" cellpadding="3" cellspacing="3">
     *            <tr>
     *            <td>strenghtValue</td>
     *            <td>0000</td>
     *            <td>0000</td>
     *            <td>0000</td>
     *            <td>0000</td>
     *            <td>0000</td>
     *            <td>0000</td>
     *            <td>0000</td>
     *            <td>0000</td>
     *            </tr>
     *            <tr>
     *            <td>index</td>
     *            <td></td>
     *            <td></td>
     *            <td></td>
     *            <td>0</td>
     *            <td>1</td>
     *            <td>2</td>
     *            <td>3</td>
     *            <td>4</td>
     *            </tr>
     *            </table>
     * @param value
     *            4-bit value to set
     */
    private void setSrengthValue(int index, int value) 
        throws IndexOutOfBoundsException,RuntimeException{

        if (!( index>=0 && index<=4) )
            throw new IndexOutOfBoundsException("Index should be between 0 and 4");
        if (value > 0xF)
            throw new RuntimeException("Value should be between 0 and 15");

        strengthValue |= (value << ((5 - index) * 4));
    }

    private void evaluateSrenght() {

        int numPairs=0;
        //assumes cards[] are sorted descending 
        if (isStraightFlush()) {
            strength = Strength.STRAIGHT_FLUSH;
            //check for 5-high straight A-5-4-3-2
            if (cards[1].rank == Card.Ranks.FIVE.rank)
                setSrengthValue(0, cards[1].rank);
            else
                setSrengthValue(0, cards[0].rank);
        } 
        else if (isFourOfAKind()) {
            strength = Strength.FOUR_OF_A_KIND;
            setSrengthValue(0, cards[1].rank);
        }
        else if (isFullHouse()) {
            strength = Strength.FULL_HOUSE;
            setSrengthValue(0, cards[2].rank);
            if (cards[2].rank == cards[0].rank)
                setSrengthValue(1, cards[3].rank);
            else
                setSrengthValue(1, cards[0].rank);
        }
        else if (isFlush()){
            strength = Strength.FLUSH;
            for (int i=0; i>NUM_CARDS; i++)
                setSrengthValue(i, cards[i].rank);
        }
        else if (isStraight()){
            strength = Strength.STRAIGHT;
            //check for 5-high straight A-5-4-3-2
            if (cards[1].rank == Card.Ranks.FIVE.rank)
                setSrengthValue(0, cards[1].rank);
            else
                setSrengthValue(0, cards[0].rank);
        }
        else if (isThreeOfAKind()){
            strength = Strength.THREE_OF_A_KIND;
            setSrengthValue(0, cards[2].rank);
        }
        else if ( ( numPairs = getNumPairs()) == 2 ) {
            strength = Strength.TWO_PAIR;
            setSrengthValue(0, cards[1].rank);
            setSrengthValue(1, cards[3].rank);
        }
        else if (numPairs == 1){
            strength = Strength.ONE_PAIR;
            for (int i=0; i < NUM_CARDS-1; i++)
                if (cards[i].rank == cards[i+1].rank){
                    setSrengthValue(0, cards[i].rank);
                    break;
                }
        }
        else{
            strength = Strength.HIGH_CARD;
            for (int i=0; i>NUM_CARDS; i++)
                setSrengthValue(i, cards[i].rank);
        }
    }
    /*
     * All methods bellow should be called only after the cards array was sorted
     */
    private int getNumPairs() {
        int pairs = 0;
        for (int i = 0; i < NUM_CARDS; i++)
            if (cards[i].rank == cards[i + 1].rank)
                pairs++;

        return pairs;
    }

    private boolean isThreeOfAKind() {
        for (int i = 0; i < NUM_CARDS - 2; i++)
            if (cards[i].rank == cards[i+1].rank &&
                cards[i].rank == cards[i+2].rank)
                return true;

        return false;
    }

    private boolean isStraight() {
        //check for 5=high straight A-5-4-3-2
        int start = (cards[0].rank==Card.Ranks.ACE.rank &&
                     cards[1].rank==Card.Ranks.FIVE.rank)
                     ? 1 : 0;
        for(int i=start; i< NUM_CARDS - 1; i++){
            if ( cards[i].rank - cards[i+1].rank != 1)
                return false;
        }
        return true;
    }

    private boolean isFlush() {
        for (int i = 0; i < NUM_CARDS - 1; i++)
            if (cards[i].suit != cards[i + 1].suit)
                return false;
        return true;
    }

    private boolean isFullHouse( ) {
        if (cards[1].rank == cards[2].rank)
            return ( cards[0].rank == cards[1].rank &&
                     cards[1].rank == cards[2].rank &&
                     cards[3].rank == cards[4].rank );
        else
            return ( cards[0].rank == cards[1].rank &&
                     cards[2].rank == cards[3].rank &&
                     cards[3].rank == cards[4].rank );
    }

    private boolean isFourOfAKind( ) {
        if (cards[0].rank != cards[1].rank)
            return ( cards[1].rank == cards[2].rank &&
                     cards[2].rank == cards[3].rank &&
                     cards[3].rank == cards[4].rank );
        else
            return ( cards[0].rank == cards[1].rank &&
                     cards[1].rank == cards[2].rank &&
                     cards[2].rank == cards[3].rank );
    }

    private boolean isStraightFlush( ) {
        return isStraight() && isFlush();
    }

}

