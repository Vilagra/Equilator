package com.levenko.myequilator;

import android.content.AsyncTaskLoader;
import android.content.Context;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.levenko.myequilator.entity.IndexesDataWasChosen;
import com.stevebrecher.showdown.CalculatingInProgressListener;
import com.stevebrecher.showdown.Enumerator;
import com.stevebrecher.showdown.Output;
import com.stevebrecher.showdown.UserInput;

import java.util.ArrayList;

/**
 * Created by Vilagra on 26.04.2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class CalculationLoader extends AsyncTaskLoader<double[]> implements CalculatingInProgressListener {
    private final IndexesDataWasChosen[] indexes;
    private final String[] arBoard;
    private Handler handler;
    private final int threads = 3;
    private final Enumerator[] enumerators = new Enumerator[threads];

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void finishLoad() {
        for (Enumerator enumerator : enumerators) {
            if(enumerator!=null)
            enumerator.interrupt();
        }
    }

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
        UserInput ui = UserInput.newUserInput(hands, board,ranges.toArray(new String[0]));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        int numberOfHands = Integer.valueOf(sharedPref.getString(getContext().getString(R.string.speed_accuracy), "-1"));
        long nanosecs = System.currentTimeMillis();
        for (int i = 0; i < enumerators.length; i++) {
            enumerators[i] = new Enumerator(i, threads,
                    ui.deck(), ui.holeCards(),ui.getRange(), ui.boardCards(),this,numberOfHands);
            enumerators[i].start();
        }
        for (Enumerator enumerator : enumerators) {
            //noinspection EmptyCatchBlock
            try {
                enumerator.join();
            } catch (InterruptedException never) {
            }
        }
        nanosecs = System.currentTimeMillis() - nanosecs;
        System.out.println("sec"+nanosecs);
        return Output.result(ui,enumerators);
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
