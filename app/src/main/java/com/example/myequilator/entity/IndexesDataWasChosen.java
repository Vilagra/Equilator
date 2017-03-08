package com.example.myequilator.entity;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Vilagra on 07.03.2017.
 */

public class IndexesDataWasChosen implements Serializable{

    public enum Type{CARD,RANGE};

    Set<Integer> indexesDataWasChosen;
    Type type;

    public IndexesDataWasChosen(Set<Integer> indexesDataWasChosen, Type type) {
        this.indexesDataWasChosen = indexesDataWasChosen;
        this.type=type;
    }

    public Set<Integer> getIndexesDataWasChosen() {
        return indexesDataWasChosen;
    }

    public Type getType() {
        return type;
    }
}
