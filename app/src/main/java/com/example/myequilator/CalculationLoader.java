package com.example.myequilator;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.myequilator.entity.IndexesDataWasChosen;
import com.stevebrecher.showdown.Showdown;

import java.util.ArrayList;

/**
 * Created by Vilagra on 26.04.2017.
 */

public class CalculationLoader  extends AsyncTaskLoader<double[]>{
    IndexesDataWasChosen[] indexes;
    String[] arBoard;

    public CalculationLoader(Context context,IndexesDataWasChosen[] indexes,String[] board) {
        super(context);
        this.indexes=indexes;
        arBoard=board;
    }

    @Override
    public double[] loadInBackground() {
        String hands = "";
        String board = "";
        ArrayList<String> ranges = new ArrayList<>();
        for (IndexesDataWasChosen indexesDataWasChosen : indexes) {
            if(indexesDataWasChosen!=null){
                if(indexesDataWasChosen.getType()== IndexesDataWasChosen.Type.RANGE){
                    ranges.add(AllCards.getSetOfHandFromCombinations(indexesDataWasChosen.getIndexesDataWasChosen()));
                }
                if(indexesDataWasChosen.getType()== IndexesDataWasChosen.Type.HAND){
                    for (Integer i : indexesDataWasChosen.getIndexesDataWasChosen()) {
                        hands+=AllCards.allCards.get(i).getStringOfCard()+",";
                    }
                }
            }
        }
        for (String s : arBoard) {
            board += s;
        }
        if(hands.length()<0){
            hands=hands.substring(0,hands.length()-1);
        }

        double[] res = Showdown.calculate(hands,board,ranges.toArray(new String[0]));
        return res;
    }
}
