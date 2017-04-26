package com.stevebrecher.showdown;

import com.stevebrecher.poker.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


final class Enumerator extends Thread {

    private final int nPlayers;
    private final int rangePlayers;
    private final int startIx;    // where to start outer loop through deck
    private final int increment;    // of outer loop through deck -- number of threads
    private long[] wins, splits;
    private double[] partialPots;
    private long constantBoard;
    private final int nBoardCards;
    private long[] deck;
    private boolean[] dealt;
    private long[] holeHand;
    private int[] handValue;
    private final int limitIx1, limitIx2, limitIx3, limitIx4, limitIx5;
    private long board1, board2, board3, board4, board5;
    private final int firstRangePlayer;
    public int trail = 0;
    static int board = 0;
    private ArrayList<ArrayList<int[]>> arrayListRanges = new ArrayList<>();

    Enumerator(final int instance, final int instances, final CardSet deck,
               final CardSet[] holeCards, String[] range, final CardSet boardCards) {

        super("Enumerator" + instance);
        startIx = instance;
        increment = instances;
        int nCardsInDeck = deck.size();
        this.deck = new long[nCardsInDeck];
        dealt = new boolean[nCardsInDeck];
        int i = 0;
        for (Card c : deck)
            this.deck[i++] = HandEval.encode(c);
        parseRange(range, deck);
        nPlayers = holeCards.length + range.length;
        holeHand = new long[nPlayers];
        i = 0;
        for (CardSet cs : holeCards)
            holeHand[i++] = HandEval.encode(cs);
        this.rangePlayers = range.length;
        nBoardCards = boardCards.size();
        constantBoard = HandEval.encode(boardCards);
        wins = new long[nPlayers];
        splits = new long[nPlayers];
        partialPots = new double[nPlayers];
        handValue = new int[nPlayers];
        limitIx1 = nCardsInDeck - 5;
        limitIx2 = nCardsInDeck - 4;
        limitIx3 = nCardsInDeck - 3;
        limitIx4 = nCardsInDeck - 2;
        limitIx5 = nCardsInDeck - 1;
        firstRangePlayer = nPlayers - rangePlayers;
    }

    public void parseRange(String[] ranges, CardSet deck) {
        for (String range : ranges) {
            String[] rangeInString = range.split(",");
            ArrayList<int[]> arrayListRange = new ArrayList<>();
            for (String s : rangeInString) {
                Card card1 = new Card(s.substring(0, 2));
                Card card2 = new Card(s.substring(2));
                int index1 = deck.getIndex(card1);
                int index2 = deck.getIndex(card2);
                if (index1 != -1 && index2 != -1) {
                    arrayListRange.add(new int[]{index1, index2});
                }
            }
            arrayListRanges.add(arrayListRange);
        }
    }

    long[] getWins() {
        return wins;
    }

    long[] getSplits() {
        return splits;
    }

    double[] getPartialPots() {
        return partialPots;
    }

    @Override
    public final void run() {
        if (rangePlayers == 0) {
            enumBoardsNoUnknown();
        }
        else {
            randomBoard();
        }
    }

    private void enum2GuysNoFlop() { // special case for speed of EnumBoardsNoUnknown

        int handValue0, handValue1;
        int wins0 = 0, splits0 = 0, pots = 0;

        for (int deckIx1 = startIx; deckIx1 <= limitIx1; deckIx1 += increment) {
            board1 = deck[deckIx1];
            for (int deckIx2 = deckIx1 + 1; deckIx2 <= limitIx2; ++deckIx2) {
                board2 = board1 | deck[deckIx2];
                for (int deckIx3 = deckIx2 + 1; deckIx3 <= limitIx3; ++deckIx3) {
                    board3 = board2 | deck[deckIx3];
                    for (int deckIx4 = deckIx3 + 1; deckIx4 <= limitIx4; ++deckIx4) {
                        board4 = board3 | deck[deckIx4];
                        for (int deckIx5 = deckIx4 + 1; deckIx5 <= limitIx5; ++deckIx5) {
                            board5 = board4 | deck[deckIx5];
                            handValue0 = HandEval.hand7Eval(board5 | holeHand[0]);
                            handValue1 = HandEval.hand7Eval(board5 | holeHand[1]);
                            /*
                             * wins[1], splits[1], and partialPots can be inferred
							 */
                            ++pots;
                            ++trail;
                            if (handValue0 > handValue1)
                                ++wins0;
                            else if (handValue0 == handValue1)
                                ++splits0;
                        }
                    }
                }
            }
        }
        wins[0] = wins0;
        wins[1] = pots - wins0 - splits0;
        splits[0] = splits[1] = splits0;
        partialPots[0] = partialPots[1] = splits0 / 2.0;
    }

    private void potResults() {
        trail++;
        int eval, bestEval = 0;
        int winningPlayer = 0, waysSplit = 0;
        double partialPot;

        for (int i = 0; i < nPlayers; ++i) {
            handValue[i] = eval = HandEval.hand7Eval(board5 | holeHand[i]);
            if (eval == 0) {
                HandEval.hand7Eval(board5 | holeHand[i]);
            }
            if (eval > bestEval) {
                bestEval = eval;
                waysSplit = 0;
                winningPlayer = i;
            } else if (eval == bestEval)
                ++waysSplit;
        }
        if (waysSplit == 0)
            ++wins[winningPlayer];
        else {
            partialPot = 1.0 / ++waysSplit;
            for (int i = 0; waysSplit > 0; ++i) {
                if (handValue[i] == bestEval) {
                    partialPots[i] += partialPot;
                    ++splits[i];
                    --waysSplit;
                }
            }
        }
    }

    private void enumBoardsNoUnknown() {
        /*
		 * This is the same as EnumBoards except each case calls
		 * potResults directly.  This one is called when there are
		 * no players with unspecified hole cards (rangePlayers == 0).
		 */

        switch (nBoardCards) {
            case 0:
                if (nPlayers == 2) {
                    enum2GuysNoFlop(); /* special case */
                    break;
                }
                for (int deckIx1 = startIx; deckIx1 <= limitIx1; deckIx1 += increment) {
                    board1 = deck[deckIx1];
                    for (int deckIx2 = deckIx1 + 1; deckIx2 <= limitIx2; ++deckIx2) {
                        board2 = board1 | deck[deckIx2];
                        for (int deckIx3 = deckIx2 + 1; deckIx3 <= limitIx3; ++deckIx3) {
                            board3 = board2 | deck[deckIx3];
                            for (int deckIx4 = deckIx3 + 1; deckIx4 <= limitIx4; ++deckIx4) {
                                board4 = board3 | deck[deckIx4];
                                for (int deckIx51 = deckIx4 + 1; deckIx51 <= limitIx5; ++deckIx51) {
                                    board5 = board4 | deck[deckIx51];
                                    potResults();
                                }
                            }
                        }
                    }
                }
                break;
            case 1:
                for (int deckIx2 = startIx; deckIx2 <= limitIx2; deckIx2 += increment) {
                    board2 = constantBoard | deck[deckIx2];
                    for (int deckIx3 = deckIx2 + 1; deckIx3 <= limitIx3; ++deckIx3) {
                        board3 = board2 | deck[deckIx3];
                        for (int deckIx4 = deckIx3 + 1; deckIx4 <= limitIx4; ++deckIx4) {
                            board4 = board3 | deck[deckIx4];
                            for (int deckIx51 = deckIx4 + 1; deckIx51 <= limitIx5; ++deckIx51) {
                                board5 = board4 | deck[deckIx51];
                                potResults();
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int deckIx3 = startIx; deckIx3 <= limitIx3; deckIx3 += increment) {
                    board3 = constantBoard | deck[deckIx3];
                    for (int deckIx4 = deckIx3 + 1; deckIx4 <= limitIx4; ++deckIx4) {
                        board4 = board3 | deck[deckIx4];
                        for (int deckIx51 = deckIx4 + 1; deckIx51 <= limitIx5; ++deckIx51) {
                            board5 = board4 | deck[deckIx51];
                            potResults();
                        }
                    }
                }
                break;
            case 3:
                for (int deckIx4 = startIx; deckIx4 <= limitIx4; deckIx4 += increment) {
                    board4 = constantBoard | deck[deckIx4];
                    for (int deckIx51 = deckIx4 + 1; deckIx51 <= limitIx5; ++deckIx51) {
                        board5 = board4 | deck[deckIx51];
                        potResults();
                    }
                }
                break;
            case 4:
                // enum 1 board card:
                for (int deckIx5 = startIx; deckIx5 <= limitIx5; deckIx5 += increment) {
                    board5 = constantBoard | deck[deckIx5];
                    potResults();
                }
                break;
            case 5:
                potResults();
                break;
        }
    }


    private void randomBoard() {
        while (trail < 100000) {
            Random random = new Random();
            HashSet<Integer> set = new HashSet<>();
            long result = constantBoard;
            int amountMissingCardInBoard = 5 - nBoardCards;
            while (set.size() != amountMissingCardInBoard) {
                int r = random.nextInt(dealt.length);
                if (!dealt[r]) {
                    set.add(r);
                    dealt[r] = true;
                    result += deck[r];
                }
            }
            board5 = result;
            vsRandomHandFromRange(0);

            for (Integer integer : set) {
                dealt[integer] = false;
            }
        }
    }

    private void vsRandomHandFromRange(int index) {
        ArrayList<int[]> current = arrayListRanges.get(index);
        Random random =new Random();
        int[] indexes = current.get(random.nextInt(current.size()));
        if (dealt[indexes[0]] || dealt[indexes[1]]) {
            return;
        } else {
            holeHand[firstRangePlayer + index] = deck[indexes[0]] | deck[indexes[1]];
            dealt[indexes[0]] = true;
            dealt[indexes[1]] = true;
            if (index == arrayListRanges.size() - 1) {
                potResults();
            } else {
                vsRandomHandFromRange(index + 1);
            }
            dealt[indexes[0]] = false;
            dealt[indexes[1]] = false;
        }
    }


}
