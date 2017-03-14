package com.example.myequilator.entity;

import java.util.Comparator;

/**
 * Created by Vilagra on 14.03.2017.
 */

public class Combination implements Comparable<Combination> {


    @Override
    public int compareTo(Combination o) {
        return this.rankingOfHand>o.rankingOfHand?1:(this.rankingOfHand==o.rankingOfHand?0:-1);
    }

    public enum Kind{
        POCKET,SUITED,OFFSUITED
    }
    String combination;
    double rankingOfHand;
    Kind kind;
    int indexInMatrixForRecycler;

    public Combination(String combination, int indexInMatrixForRecycler, Kind kind, double rankingOfHand) {
        this.combination = combination;
        this.indexInMatrixForRecycler = indexInMatrixForRecycler;
        this.kind = kind;
        this.rankingOfHand = rankingOfHand;
    }
}
