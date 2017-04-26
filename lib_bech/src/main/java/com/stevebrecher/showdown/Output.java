package com.stevebrecher.showdown;

import java.io.PrintStream;
import com.stevebrecher.poker.Card;
import com.stevebrecher.poker.CardSet;

final class Output {
	static double[] result(UserInput ui, Enumerator[] enumerators){
		final CardSet[] holeCards = ui.holeCards();
		final int nPlayers = holeCards.length+ui.nUnknown()+ui.getRange().length;
		long[] wins = new long[nPlayers];
		double[] result = new double[nPlayers];
		double[] partialPots = new double[nPlayers];
		double nPots = 0;
		for (Enumerator e : enumerators) {
				nPots += e.trail;
			for (int i = 0; i < nPlayers; i++) {
				wins[i] += e.getWins()[i];
				partialPots[i] += e.getPartialPots()[i];
			}
		}
		if(ui.getRange().length==0){
			if(ui.boardCards().size()==5){
				nPots = nPots*enumerators.length;
			}
		}
		for (int i = 0; i < nPlayers; i++) {
			result[i]= (wins[i]+partialPots[i]) * 100.0 / nPots;
		}
		return result;
	}

}
