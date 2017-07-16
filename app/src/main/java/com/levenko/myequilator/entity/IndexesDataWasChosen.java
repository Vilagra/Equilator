package com.levenko.myequilator.entity;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Vilagra on 07.03.2017.
 */

@SuppressWarnings("ALL")
public class IndexesDataWasChosen implements Serializable{

    public enum Type{HAND,RANGE};

    private final Set<Integer> indexesDataWasChosen;
    private final Type type;

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

    @Override
    public String toString() {
        return "IndexesDataWasChosen{" +
                "indexesDataWasChosen=" + indexesDataWasChosen +
                ", type=" + type +
                '}';
    }
}
