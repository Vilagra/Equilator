package com.example.myequilator.entity;

/**
 * Created by Vilagra on 07.03.2017.
 */

public class IndexesDataWasChosen {

    public enum Type{CARD,RANGE};

    int[] indexesDataWasChosen;
    Type type;

    public IndexesDataWasChosen(int[] indexesDataWasChosen,Type type) {
        this.indexesDataWasChosen = indexesDataWasChosen;
        this.type=type;
    }

    public int[] getIndexesDataWasChosen() {
        return indexesDataWasChosen;
    }

    public Type getType() {
        return type;
    }
}
