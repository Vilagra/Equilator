package com.stevebrecher.showdown;

import java.io.*;
import static java.lang.System.*;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Iterator;

import static com.stevebrecher.showdown.Help.*;

import com.stevebrecher.poker.Card;
import com.stevebrecher.poker.CardSet;

public final class UserInput {

	/*
	 * Cards specified by user, stored in input order so
	 * they can be output in the same order
	 */
	private CardSet[]		holeCards;
	private final CardSet	boardCards = new CardSet();
	private final CardSet	deadCards = new CardSet();

	private int				nUnknown;	// number of players with unknown hole cards

	private double			nPots;	// number of showdowns

	private CardSet			deck;

	private static final BufferedReader
							stdin = new BufferedReader(new InputStreamReader(System.in));
	private static boolean	userQuits;
	private String[] range;


	private UserInput() {}
	
	public static UserInput newUserInput(String cards, String board, String[] range) {
		UserInput ui = new UserInput();
		ui.nUnknown= 0;
		ui.range=range;

		if (ui.getUserInput(cards,board))
			return ui;
		else
			return null;
	}

	public CardSet[] holeCards() {
		CardSet[] result = new CardSet[holeCards.length];
		for (int i = 0; i < holeCards.length; ++i)
			result[i] = new CardSet(holeCards[i]);
		return result;
	}

	public CardSet boardCards() {
		return new CardSet(boardCards);
	}

	CardSet deadCards() {
		return new CardSet(deadCards);
	}

	int nUnknown() {
		return nUnknown;
	}

	public String[] getRange() {
		return range;
	}

	double nPots() {
		return nPots;
	}
	
	public CardSet deck() {
		return new CardSet(deck);
	}

	private boolean getUserInput(String cards,String board) {
		
		boolean gotIt = false;

		while (!gotIt && !userQuits) {
			deck = CardSet.freshDeck();
			boardCards.clear();
			deadCards.clear();
			out.println();
			if (getHoleCards(cards))
				//if (getNbrUnknown())
	/*				if (holeCards.length+nUnknown+range.length < 2)
						out.println("At least two players are required.");*/
						if (getBoard(board))
							if (getDeadCards()) {
								nPots = nbrEnumerations();
								if (nPots > Long.MAX_VALUE - 1e9)
									out.printf(
										"%,.0f pots required, which is higher than this program can count.%n",
										nPots);
								else
								/*
								 * Due to an optimization, deals == pots/2 when
								 * there are two players with unknown hole cards...
								 */
								if (userConfirm((nUnknown == 2) ? nPots / 2.0 : nPots))
									gotIt = true;
							}
			if (!gotIt && !userQuits)
				out.printf("%nRestarting...%n");			
		}
		return !userQuits;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean getCards(String source, CardSet cardsDest) {
		
		Scanner scanner = new Scanner(source);
		Pattern cardsPattern = Pattern.compile("(?:(?:[23456789TJQKA]|10)[cdhs])+",
												Pattern.CASE_INSENSITIVE);
		
		scanner.useDelimiter("\\s+|,");	//whitespace or comma
		while (scanner.hasNext()) {
			if (!scanner.hasNext(cardsPattern)) {
				out.println("Please specify each card as a rank (2..9/10/T/J/Q/K/A) and a suit (c/d/h/s).");
				return false;
			}
			String cards = scanner.next(cardsPattern);
			int i = 0;
			while (i < cards.length()) {
				char rank;
				if ((rank = cards.charAt(i++)) == '1') {
					rank = 'T';
					i++;	//skip over the '0'
				}
				Card card = Card.getInstance(String.format("%c%c", rank, /*suit:*/cards.charAt(i++)));
				if (!deck.remove(card)) {
					out.println("There's only one " + card + " in the deck!");
					return false;
				}
				cardsDest.add(card);
			}
		}
		return true;
	}

	private static String readLine() {

		String s = "--";	//suffix indicates continuation on next input line

		try {
			do {
				String input = stdin.readLine();
				if (input == null) {
					s = null;
					break;
				}
				s = s.substring(0, s.length() - 2) + input;
			} while (s.endsWith("--"));
		} catch (IOException e) {
			//noinspection ThrowablePrintedToSystemOut
			err.println(e);
		}
		return s;
	}

	private static String getResponse(String prompt, String[] help_text) {

		String s;

		do {
			out.print(prompt);
			out.flush();
			s = readLine();
			if (s == null) {	/* EOF */
				userQuits = true;
				return null;
			}
			else if (s.toLowerCase().equals("h")) {
				generalHelp();
				return null;
			}
			else if (s.equals("?"))
				for (String h : help_text)
					out.println(h);
		} while (s.equals("?"));
		
		return s;
	}

	private boolean getHoleCards(String cards) {

		//String	s;
		CardSet	holes = new CardSet();

/*		if ((s = getResponse("Known hole cards; two per player: ", holeHelp)) == null)
			return false;*/
		if (!getCards(cards, holes))
			return false;
/*		if (deck.size() < 5) {
			out.println("Too many cards -- not enough left for the board.");
			return false;
		}
		if ((holes.size() % 2) != 0 || holes.size() < 2) {
			out.println("Number of hole cards must be at least two, and even.");
			return false;
		}*/
		holeCards = new CardSet[holes.size()/2];
		Iterator<Card> iter = holes.iterator();
		for (int i = 0; i < holeCards.length; ++i) {
			holeCards[i] = new CardSet(2);
			holeCards[i].add(iter.next());
			holeCards[i].add(iter.next());
		}
		return true;
	}

	private boolean getBoard(String s) {

		//String s;

/*		if ((s = getResponse("Known board cards [none]: ", boardHelp)) == null)
			return false;*/
		if (!getCards(s, boardCards))
			return false;
/*		if (boardCards.size() > 4) {
			out.println("Number of board cards cannot exceed 4 (flop and turn).");
			return false;
		}*/
		return true;
	}

	private boolean getDeadCards() {
		String s="";
/*		if (deck.size() < (5 - boardCards.size()))
			return true;
		if ((s = getResponse("Dead/exposed cards [none]: ", deadHelp)) == null)
			return false;*/
		if (!getCards(s, deadCards))
			return false;
		if (deck.size() < (5 - boardCards.size())) {
			out.println("Not enough cards left for the board!");
			return false;
		}
		return true;
	}


	private static boolean userConfirm(double nDeals) {

		String s, prompt;
		s="y";

		while (true) {
/*			if ((s = getResponse(prompt, confirmHelp)) == null)
				return false;*/
			if (s.length() == 0)
				return true;
			s = s.toLowerCase();
			if (s.equals("y"))
				return true;
			if (s.equals("n"))
				return false;
		}
	}

	private static double factorial(double n) {
		if (n > 1.0)
			return n * factorial(n - 1.0);
		return 1.0;
	}

	private static double combos(int chooseFrom, int choose) {
		return factorial(chooseFrom) / (factorial(choose) * factorial((chooseFrom - choose)));
	}

	private double nbrEnumerations() {

		double enums;

		enums = combos(deck.size() - 2*nUnknown, 5 - boardCards.size()); // number of boards
		if (nUnknown > 0)
			// *= the number of ORDERED sets of hole cards for the players with unknown cards
			enums *= combos(deck.size(), 2*nUnknown) * factorial(2*nUnknown) / ((1 << nUnknown)/* 2^^nUnknown */);
		return enums;
	}


	public void setnPots(int nPots) {
		this.nPots = nPots;
	}
}
