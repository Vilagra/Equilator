package com.stevebrecher.showdown;

import com.stevebrecher.poker.Card;
import com.stevebrecher.poker.CardSet;

/**
 * Created by Vilagra on 27.01.2017.
 */

public class Proxy {
    public static void start(int instance, int instances, CardSet deck, CardSet[] holeCards, int nUnknown, CardSet boardCards){
        Enumerator enumerator=new Enumerator(instance, instances, deck, holeCards, nUnknown, boardCards);
        enumerator.start();
        try {
            enumerator.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(enumerator.getPartialPots());
    }

    public static void main(String[] args) {
        CardSet deck=CardSet.freshDeck();
        CardSet[] holeCard= new CardSet[2];
        CardSet first = new CardSet();
        first.add(Card.getInstance("Ah"));
        first.add(Card.getInstance("As"));
        CardSet second = new CardSet();
        second.add(Card.getInstance("Kh"));
        second.add(Card.getInstance("Ks"));
        holeCard[0]=first;
        holeCard[1]=second;
        deck.removeAll(first);
        deck.removeAll(second);
        start(1,1,deck,holeCard,0,new CardSet());
    }

}
