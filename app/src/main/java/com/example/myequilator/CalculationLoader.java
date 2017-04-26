package com.example.myequilator;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.myequilator.entity.IndexesDataWasChosen;
import com.stevebrecher.showdown.CalculatingInProgressListener;
import com.stevebrecher.showdown.Showdown;

import java.util.ArrayList;

/**
 * Created by Vilagra on 26.04.2017.
 */

public class CalculationLoader extends AsyncTaskLoader<double[]> implements CalculatingInProgressListener {
    IndexesDataWasChosen[] indexes;
    String[] arBoard;
    Handler handler;

    public CalculationLoader(Context context, IndexesDataWasChosen[] indexes, String[] board, Handler handler) {
        super(context);
        this.indexes = indexes;
        arBoard = board;
        this.handler = handler;
    }

    @Override
    public double[] loadInBackground() {
        String hands = "";
        String board = "";
        ArrayList<String> ranges = new ArrayList<>();
        for (IndexesDataWasChosen indexesDataWasChosen : indexes) {
            if (indexesDataWasChosen != null) {
                if (indexesDataWasChosen.getType() == IndexesDataWasChosen.Type.RANGE) {
                    ranges.add(AllCards.getSetOfHandFromCombinations(indexesDataWasChosen.getIndexesDataWasChosen()));
                }
                if (indexesDataWasChosen.getType() == IndexesDataWasChosen.Type.HAND) {
                    for (Integer i : indexesDataWasChosen.getIndexesDataWasChosen()) {
                        hands += AllCards.allCards.get(i).getStringOfCard() + ",";
                    }
                }
            }
        }
        for (String s : arBoard) {
            board += s;
        }
        if (hands.length() < 0) {
            hands = hands.substring(0, hands.length() - 1);
        }

        double[] res = Showdown.calculate(hands, board, ranges.toArray(new String[0]), this);
        return res;
    }

    @Override
    public void sendProgress(long[] wins, double[] pars, long trail) {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putLongArray(Constants.WINS,wins);
        bundle.putDoubleArray(Constants.PARTIAL_POTS,pars);
        bundle.putLong(Constants.TRAIL,trail);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
