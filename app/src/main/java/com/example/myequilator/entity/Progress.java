package com.example.myequilator.entity;

import android.os.Bundle;
import android.util.Log;

import com.example.myequilator.Constants;

import java.util.Arrays;

/**
 * Created by Vilagra on 26.04.2017.
 */

public class Progress {
    private long[] wins;
    private double[] partial;
    private long trail;

    public Progress(Bundle bundle) {
        this.partial = bundle.getDoubleArray(Constants.PARTIAL_POTS);
        this.wins = bundle.getLongArray(Constants.WINS);;
        this.trail = bundle.getLong(Constants.TRAIL);;
    }

/*    public void increaceData(Bundle bundle){
        double[] partialPots = bundle.getDoubleArray(Constants.PARTIAL_POTS);
        trail+=bundle.getLong(Constants.TRAIL);
        long[] wins2 = bundle.getLongArray(Constants.WINS);
        for (int i = 0; i < wins2.length; i++) {
            wins[i]+=wins2[i];
            partial[i]+=partialPots[i];
        }
        Log.d("qqqqq", String.valueOf(trail)+ Arrays.toString(wins));
    }*/



    public double[] result(){
        double[] result = new double[wins.length];
        for (int i = 0; i < result.length; i++) {
            result[i]= (wins[i]+partial[i]) * 100.0 / trail;
        }
        return result;
    }
}
