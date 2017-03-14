package com.example.myequilator;

import com.example.myequilator.entity.Card;
import com.example.myequilator.entity.Combination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vilagra on 11.01.2017.
 */

public class AllCards {
    private static final Character[] allSuit = {'d', 's', 'h', 'c'};
    private static final Character[] allRank = {'A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2'};
    private static final String handsRankingInProcent ="AA=0.5,KK=0.9,QQ=1.4,JJ=1.8,TT=2.3,99=3.0,88=5.3,77=10.3,66=16.1,55=25.6,44=36.7,33=48.6,22=59.6," +
            "AKs=2.6,AQs=3.3,AJs=4.5,ATs=5.6,A9s=9.8,A8s=12.7,A7s=14.2,A6s=16.4,A5s=15.7,A4s=19.2,A3s=21.9,A2s=23.4,KQs=4.8,KJs=6.8,KTs=7.1,K9s=13.0,K8s=19.5," +
            "K7s=20.7,K6s=23.7,K5s=27.1,K4s=31.4,K3s=34.7,K2s=38.8,QJs=7.4,QTs=9.8,Q9s=15.4,Q8s=22.2,Q7s=27.5,Q6s=32.6,Q5s=35.3,Q4s=40.0,Q3s=45.4,Q2s=47.8,JTs=11.5," +
            "J9s=17.6,J8s=24.0,J7s=32.9,J6s=41.2,J5s=45.7,J4s=48.1,J3s=53.4,J2s=57.3,T9s=18.9,T8s=24.3,T7s=33.2,T6s=43.3,T5s=52.2,T4s=56.1,T3s=59.1,T2s=62.0,98s=26.8," +
            "97s=34.4,96s=45.1,95s=53.7,94s=64.4,93s=67.7,92s=70.4,87s=35.0,86s=44.8,85s=54.9,84s=64.1,83s=73.8,82s=76.2,76s=44.5,75s=51.9,74s=60.8,73s=70.7,72s=82.5," +
            "65s=48.9,64s=58.8,63s=69.8,62s=79.2,54s=57.6,53s=67.4,52s=75.0,43s=70.1,42s=80.4,32s=84.6,AKo=4.2,AQo=6.5,AJo=8.3,ATo=11.2,A9o=18.6,A8o=21.6,A7o=25.2,A6o=31.1," +
            "A5o=29.3,A4o=34.1,A3o=38.5,A2o=43.0,KQo=9.2,KJo=12.4,KTo=15.1,K9o=23.1,K8o=32.3,K7o=36.2,K6o=42.1,K5o=46.6,K4o=51.6,K3o=57.0,K2o=60.5,QJo=13.9,QTo=17.3," +
            "Q9o=26.5,Q8o=37.6,Q7o=47.5,Q6o=53.1,Q5o=58.5,Q4o=62.9,Q3o=68.6,Q2o=72.5,JTo=20.4,J9o=28.4,J8o=39.7,J7o=49.8,J6o=63.8,J5o=69.5,J4o=73.5,J3o=78.9,J2o=82.2," +
            "T9o=30.2,T8o=40.9,T7o=50.7,T6o=66.2,T5o=77.1,T4o=81.3,T3o=84.3,T2o=87.3,98o=44.2,97o=55.8,96o=67.1,95o=78.0,94o=88.2,93o=90.0,92o=92.8,87o=54.6,86o=65.3," +
            "85o=75.9,84o=86.4,83o=94.6,82o=96.4,76o=61.7,75o=74.7,74o=85.5,73o=93.7,72o=99.1,65o=71.6,64o=83.4,63o=91.0,62o=98.2,54o=80.1,53o=89.1,52o=95.5,43o=91.9," +
            "42o=97.3,32o=100.0";
    public static final ArrayList<Card> allCards = new ArrayList<>(52);
    public static final ArrayList<String> allCombinationsInRecyclerOrderInStrings = new ArrayList<>();
    public static final ArrayList<Combination> allCombinationsInRankingOrder = new ArrayList<>();
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
        //Collections.reverse(allCards);
        HashMap<String,Double> hashMap = new HashMap<>();
        for (String s : handsRankingInProcent.split(",")) {
            String[] strings=s.split("=");
            hashMap.put(strings[0],Double.valueOf(strings[1]));
        }
        for (Character rank : allRank) {
            boolean afterPocket = false;
            for (Character rank2 : allRank) {
                if (!afterPocket) {
                    if (rank.equals(rank2)) {
                        String combination="" + rank + rank2;
                        allCombinationsInRecyclerOrderInStrings.add(combination);
                        allCombinationsInRankingOrder.add(new Combination(combination,allCombinationsInRecyclerOrderInStrings.indexOf(combination),
                                Combination.Kind.POCKET,hashMap.get(combination)));
                        afterPocket = true;
                        continue;
                    } else {
                        String combination="" + rank2 + rank + "o";
                        allCombinationsInRecyclerOrderInStrings.add(combination);
                        allCombinationsInRankingOrder.add(new Combination(combination,allCombinationsInRecyclerOrderInStrings.indexOf(combination),
                                Combination.Kind.OFFSUITED,hashMap.get(combination)));
                    }
                } else {
                    String combination="" + rank + rank2 + "s";
                    allCombinationsInRecyclerOrderInStrings.add(combination);
                    allCombinationsInRankingOrder.add(new Combination(combination,allCombinationsInRecyclerOrderInStrings.indexOf(combination),
                            Combination.Kind.SUITED,hashMap.get(combination)));
                }
            }
        }
        Collections.sort(allCombinationsInRankingOrder);
        //Collections.reverse(allCombinationsInRecyclerOrderInStrings);
    }

    public static void main(String[] args) {
/*        System.out.println(allCombinationsInRecyclerOrderInStrings);
        for (int i = 0; i < allCombinationsInRecyclerOrderInStrings.size(); ) {
            System.out.print(allCombinationsInRecyclerOrderInStrings.get(i)+"="+0.0+",");
            i+=14;
        }
        System.out.println();
        for (int i = 0; i < 13; i++) {
            for (int j = i+1; j < 13; j++) {
                System.out.print(allCombinationsInRecyclerOrderInStrings.get(i*13+j)+"="+0.0+",");
            }
        }
        System.out.println();
        for (int i = 0; i < 13; i++) {
            for (int j = i+1; j < 13; j++) {
                System.out.print(allCombinationsInRecyclerOrderInStrings.get(j*13+i)+"="+0.0+",");
            }
        }*/
    }
    public static Set<Integer> getIndexesByRecyclerBaseOnRanking(double ranking){
        Set<Integer> set = new HashSet<>();
        for (Combination combination : allCombinationsInRankingOrder) {
            if (combination.getRankingOfHand()<=ranking){
                set.add(combination.getIndexInMatrixForRecycler());
            }
            else{
                break;
            }
        }
        return set;
    }

    public static String getStringFromRange(Set<Integer> set){
        StringBuilder stringBuilder = new StringBuilder();
        String startOfRange="";
        String endOfRange="";
        boolean rangeIsInterrupted=true;
        String firstHand= allCombinationsInRecyclerOrderInStrings.get(0);
        for (int i = 0; i < allCombinationsInRecyclerOrderInStrings.size(); i+=14) {
            if(set.contains(i)){
                String value= allCombinationsInRecyclerOrderInStrings.get(i);
                if(startOfRange.equals("")){
                    startOfRange=value;
                    endOfRange=value;
                    rangeIsInterrupted=false;
                }
                else {
                    endOfRange=value;
                }
            }
            else{
                rangeIsInterrupted=true;
            }
            if((rangeIsInterrupted||i+14>= allCombinationsInRecyclerOrderInStrings.size())&&!startOfRange.equals("")){
                if(startOfRange.equals(endOfRange)){
                    stringBuilder.append(startOfRange+",");
                }
                else if (startOfRange.equals(firstHand)){
                    stringBuilder.append(endOfRange+"+,");
                }
                else {
                    stringBuilder.append(startOfRange+"-"+endOfRange+",");
                }
                startOfRange="";
                endOfRange="";
                firstHand="";
                rangeIsInterrupted=true;
            }
        }
        for (int i = 0; i < 13; i++) {
            for (int j = i+1; j < 13; j++) {
                if(j==i+1){
                    firstHand= allCombinationsInRecyclerOrderInStrings.get(i*13+j);
                }
                if(set.contains(i*13+j)){
                    String value= allCombinationsInRecyclerOrderInStrings.get(i*13+j);
                    if(startOfRange.equals("")){
                        startOfRange=value;
                        endOfRange=value;
                        rangeIsInterrupted=false;
                    }
                    else {
                        endOfRange=value;
                    }
                }
                else{
                    rangeIsInterrupted=true;
                }
                if((rangeIsInterrupted||j+1>=13)&&!startOfRange.equals("")){
                    if(startOfRange.equals(endOfRange)){
                        stringBuilder.append(startOfRange+",");
                    }
                    else if (startOfRange.equals(firstHand)){
                        stringBuilder.append(endOfRange+"+,");
                    }
                    else {
                        stringBuilder.append(startOfRange+"-"+endOfRange+",");
                    }
                    startOfRange="";
                    endOfRange="";
                    firstHand="";
                    rangeIsInterrupted=true;
                }

                //System.out.print(allCombinationsInRecyclerOrderInStrings.get(i*13+j)+" ");
            }
        }
        //System.out.println();
        for (int i = 0; i < 13; i++) {
            for (int j = i+1; j < 13; j++) {
                if (j==i+1){
                    firstHand= allCombinationsInRecyclerOrderInStrings.get(j*13+i);
                }
                if(set.contains(j*13+i)){
                    String value= allCombinationsInRecyclerOrderInStrings.get(j*13+i);
                    if(startOfRange.equals("")){
                        startOfRange=value;
                        endOfRange=value;
                        rangeIsInterrupted=false;
                    }
                    else {
                        endOfRange=value;
                    }
                }
                else{
                    rangeIsInterrupted=true;
                }
                if((rangeIsInterrupted||j+1>=13)&&!startOfRange.equals("")){
                    if(startOfRange.equals(endOfRange)){
                        stringBuilder.append(startOfRange+",");
                    }
                    else if (startOfRange.equals(firstHand)){
                        stringBuilder.append(endOfRange+"+,");
                    }
                    else {
                        stringBuilder.append(startOfRange+"-"+endOfRange+",");
                    }
                    startOfRange="";
                    endOfRange="";
                    firstHand="";
                    rangeIsInterrupted=true;
                }
            }
        }
        if(stringBuilder.length()>0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
    public static String getStringFromCard(Set<Integer> set){
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer integer : set) {
            stringBuilder.append(allCards.get(integer).getStringOfCard());
        }
        return stringBuilder.toString();
    }

    public static void unCheckFlags(Set<Integer> setPositioWasChoosen){
        for (Integer integer : setPositioWasChoosen) {
            wasChosen[integer] = false;
        }
    }
    public static void checkFlags(Set<Integer> setPositioWasChoosen){
        for (Integer integer : setPositioWasChoosen) {
            wasChosen[integer] = true;
        }
    }

    public static Card findCardByString(String s) {
        return cardsMap.get(s);
    }

    public static void resetWasChosen() {
        Arrays.fill(wasChosen, false);
    }
}




