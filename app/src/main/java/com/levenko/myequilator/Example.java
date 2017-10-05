package com.levenko.myequilator;

import com.levenko.myequilator.entity.IndexesDataWasChosen;

/**
 * Created by dev6 on 04.10.2017.
 */

public class Example {
    public static void main(String[] args) {
        IndexesDataWasChosen[] indexes = new IndexesDataWasChosen[12];
        Object[] objects = indexes;
        indexes = (IndexesDataWasChosen[]) objects;
        System.out.println(indexes);
    }
}
