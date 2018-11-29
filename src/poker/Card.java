package poker;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class Card implements Comparable<Card> {
    public enum Suit {
        HEARTS, SPADES, DIAMONS, CLUBS;

        public static Suit fromCode(int c) {
            int i = c / 4;
            return ((i >= 2) ? (i == 3 ? CLUBS : DIAMONS) : (i == 1 ? SPADES : HEARTS));
        }
    }

    public enum Ranks {
        DEUCE(0),
        THREE(1),
        FOUR(2),
        FIVE(3),
        SIX(4),
        SEVEN(5),
        EIGHT(6),
        NINE(7),
        TEN(8),
        JACK(9),
        QUEEN(10),
        KING(11),
        ACE(12);

        private static final Map<Integer, Ranks> lookup = new HashMap<Integer, Ranks>();

        static {
            for (Ranks s : EnumSet.allOf(Ranks.class))
                lookup.put(s.rank, s);
        }

        public final int rank;

        private Ranks(int rank) {
            this.rank = rank;
        }

        public static Ranks fromCode(int code) {
            return lookup.get(code%13);
        }

        public static Ranks fromRank(int rank) {
            return lookup.get(rank);
        }
}
    /**
     * int from 0 to 51 inclusive represinting a card
     */
    public final int code;
    public final int rank;
    public final Suit suit;

    public Card(int code) {
        this.code = code;
        this.rank = (code % 13);
        this.suit = Suit.fromCode(code);
    }

    public String toString() {
        return Ranks.fromCode(code) + " of " + suit;
    }

    public int compareTo(Card card) {
        return this.rank - card.rank;
    }
}
