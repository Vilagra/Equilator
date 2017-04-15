package com.stevebrecher.showdown;

import java.io.PrintStream;
import com.stevebrecher.poker.Card;
import com.stevebrecher.poker.CardSet;

final class Output {

	private static final int	HANDS_PER_LINE = 4;	// output

	private static final String FP_FORMAT = "%13.6f";
	static double[] result(UserInput ui, Enumerator[] enumerators){
		final CardSet[] holeCards = ui.holeCards();
		final int nPlayers = holeCards.length;
		long[] wins = new long[holeCards.length];
		double[] result = new double[holeCards.length];
		double[] partialPots = new double[nPlayers];
		double nPots = ui.nPots();
		for (Enumerator e : enumerators)
			for (int i = 0; i < nPlayers; i++) {

				wins[i] += e.getWins()[i];
				partialPots[i] += e.getPartialPots()[i];
			}
		for (int i = 0; i < nPlayers; i++) {
			result[i]= (wins[i]+partialPots[i]) * 100.0 / nPots;
		}
		return result;
	}
	
	static void resultsOut(UserInput ui, Enumerator[] enumerators, PrintStream fileOut) {
		final CardSet[] holeCards = ui.holeCards();
		final CardSet boardCards = ui.boardCards();
		final CardSet deadCards = ui.deadCards();
		final int nUnknown = ui.nUnknown();
		double nPots = ui.nPots();
		final int nPlayers = holeCards.length+ nUnknown+ui.getRange().length;
		long[] wins = new long[nPlayers], splits = new long[nPlayers];
		double[] partialPots = new double[nPlayers];
		int j, n, nbrToPrint;
		long yoy=0;
		for (Enumerator e : enumerators) {
			yoy+=e.trail;
			System.out.println(yoy+" "+e.trail);
			for (int i = 0; i < nPlayers; i++) {
				wins[i] += e.getWins()[i];
				splits[i] += e.getSplits()[i];
				partialPots[i] += e.getPartialPots()[i];
			}
		}
		nPots=yoy;

		nbrToPrint = nPlayers;
		if (nUnknown == 2) {
			/* show the total of the two as one entry */
			--nbrToPrint;
			wins[nPlayers - 2] += wins[nPlayers - 1];
			splits[nPlayers - 2] += splits[nPlayers - 1];
			partialPots[nPlayers - 2] += partialPots[nPlayers - 1];
		}

		fileOut.printf("%n%,.0f pots with board cards:", nPots);
		if (boardCards.size() == 0)
			fileOut.print(" (unspecified)");
		for (Card c : boardCards)
			fileOut.print(" " + c);
		if (deadCards.size() > 0) {
			fileOut.printf("%nDead/exposed cards:");
			for (Card c : deadCards)
				fileOut.print(" " + c);
		}
		fileOut.println();

		for (PrintStream f : new PrintStream[] {System.out, fileOut}) {
			n = nbrToPrint;
			j = 0;
			while (n > 0) {
				f.printf("%n                     ");
				for (int i = j; i < j + HANDS_PER_LINE && i < nbrToPrint; ++i)
					if (i >= nPlayers - nUnknown-1) {
						f.print("         Unknown");
						if (nUnknown > 1)
							f.print("s");
					} else {
						f.print("         ");
						for (Card c : holeCards[i])
							f.print(c);
					}
				f.printf("%n%% chance of outright win ");
				for (int i = j; i < j + HANDS_PER_LINE && i < nbrToPrint; ++i) {
					f.printf(FP_FORMAT, wins[i] * 100.0 / nPots);
					--n;
				}
				f.printf("%n%% chance of win or split ");
				for (int i = j; i < j + HANDS_PER_LINE && i < nbrToPrint; ++i)
					f.printf(FP_FORMAT, (wins[i] + splits[i]) * 100.0 / nPots);
				f.printf("%nexpected return, %% of pot");
				for (int i = j; i < j + HANDS_PER_LINE && i < nbrToPrint; ++i)
					f.printf(FP_FORMAT, (wins[i] + partialPots[i]) * 100.0 / nPots);
				f.printf("%nfair pot odds:1          ");
				for (int i = j; i < j + HANDS_PER_LINE && i < nbrToPrint; ++i) {
					if (wins[i] > 0 || partialPots[i] > 0.1E-9)
						f.printf(FP_FORMAT, (nPots - (wins[i] + partialPots[i])) / (wins[i] + partialPots[i]));
					else
						f.print(" infinite");
				}
				f.printf("%npots won:                ");
				for (int i = j; i < j + HANDS_PER_LINE && i < nbrToPrint; ++i) {
					f.printf("%13.2f", wins[i] + partialPots[i]);

				}
				f.println();
				j += HANDS_PER_LINE;
			}
			f.flush();
		}
	}

}
