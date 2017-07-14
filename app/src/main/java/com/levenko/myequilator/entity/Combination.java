package com.levenko.myequilator.entity;

/**
 * Created by Vilagra on 14.03.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class Combination implements Comparable<Combination> {


    @Override
    public int compareTo(Combination o) {
        return this.rankingOfHand>o.rankingOfHand?1:(this.rankingOfHand==o.rankingOfHand?0:-1);
    }

    public enum Kind{
        POCKET,SUITED,OFFSUITED
    }
    private final String combination;
    private final double rankingOfHand;
    private final Kind kind;
    private final int indexInMatrixForRecycler;

    public Combination(String combination, int indexInMatrixForRecycler, Kind kind, double rankingOfHand) {
        this.combination = combination;
        this.indexInMatrixForRecycler = indexInMatrixForRecycler;
        this.kind = kind;
        this.rankingOfHand = rankingOfHand;
    }

    @Override
    public String toString() {
        return "Combination{" +
                "combination='" + combination + '\'' +
                ", rankingOfHand=" + rankingOfHand +
                ", kind=" + kind +
                ", indexInMatrixForRecycler=" + indexInMatrixForRecycler +
                '}';
    }

    public String getCombination() {
        return combination;
    }

    public int getIndexInMatrixForRecycler() {
        return indexInMatrixForRecycler;
    }

    public Kind getKind() {
        return kind;
    }

    public double getRankingOfHand() {
        return rankingOfHand;
    }
}
