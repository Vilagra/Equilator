package com.stevebrecher.showdown;

import com.stevebrecher.poker.CardSet;

public final class Output {
	public static double[] result(UserInput ui, Enumerator[] enumerators){
		final CardSet[] holeCards = ui.holeCards();
		final int nPlayers = holeCards.length+ui.nUnknown()+ui.getRange().length;
		long[] wins = new long[nPlayers];
		double[] result = new double[nPlayers];
		double[] partialPots = new double[nPlayers];
		double nPots = 0;
		for (Enumerator e : enumerators) {
				nPots += e.gameAmount;
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
