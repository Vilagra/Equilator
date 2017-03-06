package com.example.myequilator;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.myequilator.entity.Card;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Vilagra on 11.01.2017.
 */

public class AllCards {
    private static final Character[] allSuit = {'d', 's', 'h', 'c'};
    private static final Character[] allRank = {'2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};
    public static final ArrayList<Card> allCards = new ArrayList<>(52);
    public static final ArrayList<String> allCombinations = new ArrayList<>();
    public static final HashMap<String, Card> cardsMap = new HashMap<>();
    public static final boolean[] wasChosen = new boolean[52];
    static {
        for (Character rank : allRank) {
            for (Character suit : allSuit) {
                Card card = new Card(rank, suit);
                allCards.add(card);
                cardsMap.put(card.getStringOfCard(), card);
            }
        }
        Collections.reverse(allCards);
        for (Character rank: allRank){
            boolean afterPocket=false;
            for (Character rank2: allRank){
                if (!afterPocket){
                    if(rank.equals(rank2)){
                        allCombinations.add(""+rank+rank2);
                        afterPocket=true;
                        continue;
                    }else{
                        allCombinations.add(""+rank+rank2+"o");
                    }
                }else {
                    allCombinations.add(""+rank2+rank+"s");
                }
            }
        }
        Collections.reverse(allCombinations);
    }

    public static Card findCardByString(String s){
        return cardsMap.get(s);
    }

    public static void resetWasChosen(){
        Arrays.fill(wasChosen, false);
    }
}




